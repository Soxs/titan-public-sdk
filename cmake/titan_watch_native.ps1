param(
    [Parameter(Mandatory=$true)][string]$BuildDirectory,
    [Parameter(Mandatory=$true)][string]$Target,
    [string]$Configuration = 'Debug',
    [Parameter(Mandatory=$true)][string]$SourceRoot,
    [Parameter(Mandatory=$true)][string]$SdkRoot
)
$ErrorActionPreference = 'Stop'
# Watch source trees, build descriptions and runtime data.
# Snapshot polling coalesces editor save/rename events and notices changes made
# during a build. Failed builds keep watching and never request a replacement.
$roots = @([IO.Path]::GetFullPath($SourceRoot), [IO.Path]::GetFullPath($SdkRoot)) | Select-Object -Unique
$buildRoot = [IO.Path]::GetFullPath($BuildDirectory).TrimEnd('\','/') + [IO.Path]::DirectorySeparatorChar
function Snapshot {
    $lines = foreach ($root in $roots) {
        foreach ($file in Get-ChildItem -LiteralPath $root -File -Recurse -ErrorAction SilentlyContinue) {
            if ($file.FullName.StartsWith($buildRoot, [StringComparison]::OrdinalIgnoreCase)) { continue }
            $relative = $file.FullName.Substring($root.TrimEnd('\','/').Length)
            if ($relative -match '[\\/](\.git|\.titan|\.idea|\.vscode|\.gradle|node_modules|build|build-[^\\/]*|cmake-build[^\\/]*|out|runtime)[\\/]') { continue }
            '{0}|{1}|{2}' -f $file.FullName, $file.LastWriteTimeUtc.Ticks, $file.Length
        }
    }
    return (($lines | Sort-Object) -join "`n")
}
Write-Output "[titan-dev] watching $SourceRoot; run the plugin once first; Ctrl+C stops watching"
$last = Snapshot
while ($true) {
    Start-Sleep -Milliseconds 500
    $current = Snapshot
    if ($current -ceq $last) { continue }
    Start-Sleep -Milliseconds 250
    $last = Snapshot
    $buildArgs = @('--build', $BuildDirectory, '--target', $Target)
    if ($Configuration) { $buildArgs += @('--config', $Configuration) }
    & cmake @buildArgs
    if ($LASTEXITCODE -ne 0) { [Console]::Error.WriteLine('[titan-dev] build/reload failed; fix the error and save to retry') }
}
