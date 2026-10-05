param(
    [Parameter(Mandatory=$true)][string]$ManifestPath,
    [ValidateSet('run','reload')][string]$Action = 'run'
)
$ErrorActionPreference = 'Stop'
function ConvertTo-ProcessArgument([string]$Value) {
    # Windows command-line quoting: double backslashes before quotes and the
    # closing delimiter. This is passed directly to an executable, never cmd.
    $escaped = [regex]::Replace($Value, '(\\*)"', '$1$1\"')
    return '"' + [regex]::Replace($escaped, '(\\+)$', '$1$1') + '"'
}
try {
    $manifest = Get-Content -LiteralPath $ManifestPath -Raw | ConvertFrom-Json
    $runtimeRoot = $manifest.titan_install_root
    if (-not $runtimeRoot) { $runtimeRoot = $env:TITAN_CLIENT_ROOT }
    if (-not $runtimeRoot) {
        $repository = Join-Path $env:USERPROFILE '.titanclient/repository'
        $state = Get-Content -LiteralPath (Join-Path $repository 'state.json') -Raw | ConvertFrom-Json
        if ($state.current_version -notmatch '^[A-Za-z0-9._-]{1,64}$') { throw 'Invalid launcher runtime version' }
        $runtimeRoot = Join-Path $repository ('releases/' + $state.current_version)
    }
    $controller = Join-Path $runtimeRoot 'controller.exe'
    if (-not [IO.File]::Exists($controller)) {
        throw "controller.exe was not found at $runtimeRoot. Build the source runtime, run TitanLauncher once, or set TITAN_CLIENT_ROOT."
    }
    $controllerArgs = @('--dev-mode', '--dev-manifest', [IO.Path]::GetFullPath($ManifestPath))
    if ($Action -eq 'reload') { $controllerArgs += '--reload-dev-plugin' }
    else { $controllerArgs += '--launch-new-client' }
    $start = New-Object System.Diagnostics.ProcessStartInfo
    $start.FileName = [IO.Path]::GetFullPath($controller)
    $start.Arguments = ($controllerArgs | ForEach-Object { ConvertTo-ProcessArgument $_ }) -join ' '
    $start.WorkingDirectory = [IO.Path]::GetFullPath($runtimeRoot)
    $result = 0
    if ($Action -eq 'reload') {
        # Reload is a request/response command: preserve its complete result and
        # diagnostic output even for a GUI-subsystem controller. PowerShell 5
        # does not reliably wait for GUI executables or set LASTEXITCODE.
        $start.UseShellExecute = $false
        $start.CreateNoWindow = $true
        $start.RedirectStandardOutput = $true
        $start.RedirectStandardError = $true
        $process = [Diagnostics.Process]::Start($start)
        if (-not $process) { throw 'Controller could not be started.' }
        try {
            # Drain both pipes concurrently so either diagnostic stream can
            # exceed its buffer without blocking the controller's exit.
            $stdout = $process.StandardOutput.ReadToEndAsync()
            $stderr = $process.StandardError.ReadToEndAsync()
            $process.WaitForExit()
            $result = $process.ExitCode
            [Console]::Out.Write($stdout.GetAwaiter().GetResult())
            [Console]::Error.Write($stderr.GetAwaiter().GetResult())
        } finally { $process.Dispose() }
    } else {
        # A new controller stays alive for the whole GUI session. Detach its
        # lifetime (and console handles) from the build so watch can start.
        $start.UseShellExecute = $true
        $process = [Diagnostics.Process]::Start($start)
        if (-not $process) { throw 'Controller could not be started.' }
        try {
            # Observe immediate launch/handoff errors, without waiting for a
            # successfully opened controller to close. Later errors appear in UI.
            if ($process.WaitForExit(1500)) { $result = $process.ExitCode }
        } finally { $process.Dispose() }
    }
    if ($result -ne 0) { throw "Controller $Action failed (exit $result). See the controller diagnostic; check the DEV tab before retrying." }
} catch {
    [Console]::Error.WriteLine("[titan-dev] $($_.Exception.Message)")
    exit 1
}
