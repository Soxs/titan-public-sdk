# Build output is mutable; every successful build snapshots it before a session
# is published. Published generations are never overwritten; obsolete unlocked
# generations are cleaned only after a successful publication.
param(
    [Parameter(Mandatory=$true)][string]$SpecPath,
    [switch]$ReuseIfUnchanged
)
$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
$utf8 = New-Object System.Text.UTF8Encoding($false)

function Hash-Text([string]$Value) {
    $sha = [Security.Cryptography.SHA256]::Create()
    try { return ([BitConverter]::ToString($sha.ComputeHash($utf8.GetBytes($Value)))).Replace('-', '').ToLowerInvariant() }
    finally { $sha.Dispose() }
}
function Hash-File([string]$Path) {
    $sha = [Security.Cryptography.SHA256]::Create()
    $stream = [IO.File]::OpenRead($Path)
    try { return ([BitConverter]::ToString($sha.ComputeHash($stream))).Replace('-', '').ToLowerInvariant() }
    finally { $stream.Dispose(); $sha.Dispose() }
}
function Full-Path([string]$Path) { return [IO.Path]::GetFullPath($Path) }
function Json-Path([string]$Path) { return $Path.Replace('\', '/') }
function Require-Relative([string]$Path) {
    if ([string]::IsNullOrWhiteSpace($Path) -or [IO.Path]::IsPathRooted($Path) -or
        $Path -match '(^|[\\/])\.\.([\\/]|$)' -or $Path.Contains(':')) {
        throw "Runtime destination must be relative and remain inside the generation: $Path"
    }
    return $Path.Replace('\', '/')
}
function Atomic-Write([string]$Path, [string]$Content) {
    $temporary = "$Path.tmp-$([Guid]::NewGuid().ToString('N'))"
    [IO.File]::WriteAllText($temporary, $Content, $utf8)
    if ([IO.File]::Exists($Path)) { [IO.File]::Replace($temporary, $Path, [NullString]::Value) }
    else { [IO.File]::Move($temporary, $Path) }
}
function Add-Input([string]$Source, [string]$Destination) {
    $sourcePath = Full-Path $Source
    $relative = Require-Relative $Destination
    if (-not [IO.File]::Exists($sourcePath)) { throw "Runtime input is missing: $sourcePath" }
    if ($destinations.ContainsKey($relative)) { throw "Duplicate runtime destination: $relative" }
    if ($relative -ieq 'artifact.json') { throw 'artifact.json is reserved for staging metadata' }
    $destinations[$relative] = $true
    $inputs.Add([ordered]@{ source = $sourcePath; destination = $relative; sha256 = (Hash-File $sourcePath) })
}

function Read-GenerationHighWater([string]$CounterPath, [string]$LoadRoot) {
    [long]$highest = 0
    if ([IO.File]::Exists($CounterPath)) {
        $counter = [IO.File]::ReadAllText($CounterPath).Trim()
        if ($counter -notmatch '^[0-9]+$' -or -not [long]::TryParse($counter, [ref]$highest)) {
            throw "Invalid native generation counter: $CounterPath"
        }
    }
    if ([IO.Directory]::Exists($LoadRoot)) {
        foreach ($entry in Get-ChildItem -LiteralPath $LoadRoot -Directory -Force) {
            if ($entry.Name -cmatch '^gen-([1-9][0-9]*)$') {
                [long]$number = 0
                if (-not [long]::TryParse($Matches[1], [ref]$number)) { throw 'Native generation number is out of range' }
                $highest = [Math]::Max($highest, $number)
            }
        }
    }
    return $highest
}

function Cleanup-PublishedGeneration([string]$LoadRoot, [string]$GenerationPath) {
    try {
        . (Join-Path $PSScriptRoot 'titan_cleanup_native.ps1')
        Remove-TitanOldNativeGenerations -LoadRoot $LoadRoot -KeepGeneration $GenerationPath
    } catch {
        Write-Output "[titan-dev] old generation cleanup skipped: $($_.Exception.Message)"
    }
}

$mutex = $null
$locked = $false
try {
    $spec = Get-Content -LiteralPath $SpecPath -Raw | ConvertFrom-Json
    if ($spec.slug -notmatch '^[A-Za-z0-9_-]+$') { throw 'Invalid canonical plugin slug' }
    if ($spec.build_config -notmatch '^[A-Za-z0-9_-]+$') { throw 'Invalid build configuration' }
    $slugs = @($spec.slugs)
    if ($slugs.Count -eq 0 -or $slugs[0] -cne $spec.slug -or @($slugs | Select-Object -Unique).Count -ne $slugs.Count) {
        throw 'slugs must contain unique IDs with the canonical slug first'
    }
    foreach ($slug in $slugs) { if ($slug -notmatch '^[A-Za-z0-9_-]+$') { throw "Invalid plugin slug: $slug" } }
    if ([int]$spec.native_abi_version -ne 1) { throw 'Unsupported native ABI version' }
    if ($spec.loader_mode -notin @('Restart', 'DebuggableLoadLibrary', 'MemoryBlob')) { throw 'Invalid loader mode' }
    $root = Full-Path $spec.session_root
    $configRoot = Join-Path $root $spec.build_config
    $loadRoot = Join-Path $configRoot 'load'
    $manifestPath = Join-Path $configRoot 'session.json'
    $counterPath = Join-Path $configRoot 'generation.txt'
    [IO.Directory]::CreateDirectory($configRoot) | Out-Null
    $mutex = New-Object Threading.Mutex($false, ('Local\TitanNativeStage-' + (Hash-Text $root.ToLowerInvariant())))
    try { $locked = $mutex.WaitOne(60000) } catch [Threading.AbandonedMutexException] { $locked = $true }
    if (-not $locked) { throw "Another build is staging $($spec.slug); retry after it completes" }

    $inputs = New-Object 'System.Collections.Generic.List[object]'
    $destinations = @{}
    $dllName = [IO.Path]::GetFileName($spec.source_dll)
    Add-Input $spec.source_dll $dllName
    if ((Get-Item -LiteralPath $spec.source_dll).Length -eq 0) { throw 'The plugin DLL is empty' }
    $pdbName = ''
    if ($spec.source_pdb -and [IO.File]::Exists($spec.source_pdb)) {
        $pdbName = [IO.Path]::GetFileName($spec.source_pdb)
        Add-Input $spec.source_pdb $pdbName
    } elseif ($spec.source_pdb -and $spec.build_config -in @('Debug', 'RelWithDebInfo')) {
        throw "The matching PDB is missing: $($spec.source_pdb)"
    }
    if ($spec.PSObject.Properties['runtime_dlls']) {
        foreach ($dependency in @($spec.runtime_dlls)) {
            if ($dependency) { Add-Input $dependency ([IO.Path]::GetFileName($dependency)) }
        }
    }
    foreach ($asset in @($spec.runtime_files)) {
        $assetPath = Full-Path $asset.source
        $assetDestination = Require-Relative $asset.destination
        if ([IO.Directory]::Exists($assetPath)) {
            foreach ($entry in Get-ChildItem -LiteralPath $assetPath -Recurse -Force | Sort-Object FullName) {
                if ($entry.Attributes -band [IO.FileAttributes]::ReparsePoint) { throw "Runtime assets must not contain symbolic links: $($entry.FullName)" }
                if (-not $entry.PSIsContainer) {
                    $suffix = $entry.FullName.Substring($assetPath.TrimEnd('\','/').Length).TrimStart('\','/')
                    Add-Input $entry.FullName (Join-Path $assetDestination $suffix)
                }
            }
        } else { Add-Input $assetPath $assetDestination }
    }
    $fingerprintData = [ordered]@{ spec = $spec; inputs = @($inputs.ToArray()) }
    $fingerprint = Hash-Text (ConvertTo-Json -InputObject $fingerprintData -Depth 12 -Compress)
    if ($ReuseIfUnchanged -and [IO.File]::Exists($manifestPath)) {
        $previousJson = [IO.File]::ReadAllText($manifestPath)
        $previous = $previousJson | ConvertFrom-Json
        if ($previous.PSObject.Properties['artifact_fingerprint'] -and $previous.artifact_fingerprint -eq $fingerprint) {
            $previousArtifact = Join-Path $previous.dev_plugin_dir 'artifact.json'
            $intact = [IO.File]::Exists($previousArtifact) -and [IO.File]::ReadAllText($previousArtifact) -ceq $previousJson
            foreach ($inputFile in $inputs) {
                $priorFile = Join-Path $previous.dev_plugin_dir $inputFile.destination
                if (-not [IO.File]::Exists($priorFile) -or (Hash-File $priorFile) -ne $inputFile.sha256) { $intact = $false; break }
            }
            if ($intact) {
                $sidecar = $spec.source_dll + '.titan-dev.json'
                if (-not [IO.File]::Exists($sidecar) -or [IO.File]::ReadAllText($sidecar) -cne $previousJson) {
                    Atomic-Write $sidecar $previousJson
                }
                # Seed the persistent counter before pruning older/incomplete
                # directories, including migrations from the pre-cleanup helper.
                try {
                    $highest = [Math]::Max((Read-GenerationHighWater $counterPath $loadRoot), [long]$previous.generation)
                    if (-not [IO.File]::Exists($counterPath) -or [IO.File]::ReadAllText($counterPath).Trim() -cne $highest.ToString()) {
                        Atomic-Write $counterPath ($highest.ToString() + "`n")
                    }
                    Cleanup-PublishedGeneration $loadRoot $previous.dev_plugin_dir
                } catch {
                    Write-Output "[titan-dev] old generation cleanup skipped: $($_.Exception.Message)"
                }
                Write-Output "[titan-dev:$($spec.slug)] generation $($previous.generation) is current"
                exit 0
            }
        }
    }
    $identityPath = Join-Path $root 'session_id.txt'
    $sessionId = ''
    if ([IO.File]::Exists($identityPath)) { $sessionId = [IO.File]::ReadAllText($identityPath).Trim() }
    $parsedId = [Guid]::Empty
    if (-not [Guid]::TryParse($sessionId, [ref]$parsedId)) { $sessionId = [Guid]::NewGuid().ToString() }
    [IO.Directory]::CreateDirectory($loadRoot) | Out-Null
    $highest = Read-GenerationHighWater $counterPath $loadRoot
    if ($highest -ge [int]::MaxValue) { throw 'Native generation counter is exhausted' }
    $generation = [int]($highest + 1)
    # Reserve before creating the directory. Failed or subsequently pruned
    # snapshots never cause a generation number to be reused.
    Atomic-Write $counterPath ($generation.ToString() + "`n")
    $generationPath = Join-Path $loadRoot "gen-$generation"
    # Reserve a fresh generation under the staging mutex. It remains invisible
    # to loaders until its completed manifest is published below. Renaming a
    # populated directory can fail while a Windows scanner holds any handle in
    # it, even when every artifact copied and verified successfully.
    [IO.Directory]::CreateDirectory($generationPath) | Out-Null
    foreach ($inputFile in $inputs) {
        $outputPath = Join-Path $generationPath $inputFile.destination
        [IO.Directory]::CreateDirectory([IO.Path]::GetDirectoryName($outputPath)) | Out-Null
        [IO.File]::Copy($inputFile.source, $outputPath, $false)
        if ((Hash-File $outputPath) -ne $inputFile.sha256) { throw "Runtime input changed during staging: $($inputFile.source)" }
    }
    foreach ($inputFile in $inputs) {
        if ((Hash-File $inputFile.source) -ne $inputFile.sha256) { throw "Runtime input changed during staging: $($inputFile.source)" }
    }
    $manifest = [ordered]@{
        schema_version = 1; session_id = $sessionId; slug = $spec.slug; slugs = $slugs
        runtime_kind = 'native'; native_abi_version = 1
        source_root = (Json-Path (Full-Path $spec.source_root)); build_config = $spec.build_config
        sdk_version = [int]$spec.sdk_version; titan_install_root = (Json-Path $spec.titan_install_root)
        dev_plugin_dir = (Json-Path $generationPath); staged_dll = (Json-Path (Join-Path $generationPath $dllName))
        staged_pdb = ''; loader_mode = $spec.loader_mode; generation = $generation
        dll_sha256 = $inputs[0].sha256; artifact_fingerprint = $fingerprint
        runtime_files = @($inputs | ForEach-Object { [ordered]@{ path = $_.destination; sha256 = $_.sha256 } })
    }
    if ($pdbName) {
        $manifest.staged_pdb = Json-Path (Join-Path $generationPath $pdbName)
        $manifest.pdb_sha256 = $inputs[1].sha256
    }
    $json = ConvertTo-Json -InputObject $manifest -Depth 12
    [IO.File]::WriteAllText((Join-Path $generationPath 'artifact.json'), $json, $utf8)
    Atomic-Write $identityPath ($sessionId + "`n")
    # Ordinary repository disk scans use this sidecar to find the same immutable
    # DLL, symbols and declared assets as manifest-driven development sessions.
    Atomic-Write ($spec.source_dll + '.titan-dev.json') $json
    Atomic-Write $manifestPath $json
    Cleanup-PublishedGeneration $loadRoot $generationPath
    Write-Output "[titan-dev:$($spec.slug)] staged generation $generation -> $generationPath"
} catch {
    # Failed snapshots remain unpublished and never trigger cleanup. A later
    # successful build may remove them after checking the entire tree for locks.
    $failureLine = 1
    if ($_.InvocationInfo -and $_.InvocationInfo.ScriptLineNumber -gt 0) {
        $failureLine = $_.InvocationInfo.ScriptLineNumber
    }
    # Use MSBuild's diagnostic format so the actual cause appears in the IDE's
    # Error List alongside the generic post-build MSB3073 wrapper.
    [Console]::Error.WriteLine("${PSCommandPath}(${failureLine},1): error TITANDEV001: [titan-dev] staging failed: $($_.Exception.Message)")
    exit 1
} finally {
    if ($locked) { $mutex.ReleaseMutex() }
    if ($null -ne $mutex) { $mutex.Dispose() }
}
