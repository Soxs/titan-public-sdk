# Loaded only after a successful publication (including reuse of a good stage).
# Cleanup is best effort: locks/permissions never turn a valid build into failure.
function Remove-TitanOldNativeGenerations {
    param(
        [Parameter(Mandatory=$true)][string]$LoadRoot,
        [Parameter(Mandatory=$true)][string]$KeepGeneration
    )
    try {
        $rootPath = [IO.Path]::GetFullPath($LoadRoot).TrimEnd('\','/')
        $keepPath = [IO.Path]::GetFullPath($KeepGeneration).TrimEnd('\','/')
        if (-not [IO.Directory]::Exists($rootPath)) { return }
        if ([IO.Path]::GetDirectoryName($keepPath) -ine $rootPath -or
            [IO.Path]::GetFileName($keepPath) -notmatch '^gen-[1-9][0-9]*$') {
            throw 'Published generation is outside the expected configuration load directory'
        }
        $candidates = @(Get-ChildItem -LiteralPath $rootPath -Directory -Force |
            Where-Object { $_.Name -cmatch '^(gen-[1-9][0-9]*|\.pending-[a-fA-F0-9]{32})$' -and $_.FullName -ine $keepPath })
        if ($candidates.Count -eq 0) { return }
        if (-not ('Titan.NativeDevelopment.GenerationCleanup' -as [type])) {
            Add-Type -Path (Join-Path $PSScriptRoot 'titan_cleanup_native.cs')
        }
        foreach ($candidate in $candidates) {
            $reason = ''
            if ([Titan.NativeDevelopment.GenerationCleanup]::TryRemove($rootPath, $candidate.FullName, $keepPath, [ref]$reason)) {
                Write-Output "[titan-dev] removed old staging directory $($candidate.Name)"
            } else {
                Write-Output "[titan-dev] cleanup deferred for $($candidate.Name): $reason"
            }
        }
    } catch {
        Write-Output "[titan-dev] old generation cleanup skipped: $($_.Exception.Message)"
    }
}
