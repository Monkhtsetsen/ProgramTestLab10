$root = Split-Path $PSScriptRoot -Parent
Set-Location $root
$main = "src\main\java\mn\edu\must\sqat\DataFlowTargets.java"
$utf8NoBom = New-Object System.Text.UTF8Encoding $false
$orig = [System.IO.File]::ReadAllText((Join-Path $root $main))

function Write-Source([string]$text) {
    [System.IO.File]::WriteAllText((Join-Path $root $main), $text, $utf8NoBom)
}

New-Item -ItemType Directory -Force -Path results | Out-Null

$m1 = $orig -replace "(?m)^\s*streak = streak \+ 1;\r?\n", ""
Write-Source $m1
mvn test 2>&1 | Out-File -Encoding utf8 results/mvn-test-M1.txt
Write-Source $orig

$m2 = $orig -replace "best = streak;", "best = present;"
Write-Source $m2
mvn test 2>&1 | Out-File -Encoding utf8 results/mvn-test-M2.txt
Write-Source $orig

$m3 = $orig -replace "(?m)(\r?\n        int score = 0;)", "`r`n        int total = present + best;`$1"
Write-Source $m3
mvn test 2>&1 | Out-File -Encoding utf8 results/mvn-test-M3.txt
Write-Source $orig

Write-Source $m2
mvn test "-Dtest=BonusTest`$AllDuPairs#m2_kill_bestMustTrackStreakNotPresent" 2>&1 | Out-File -Encoding utf8 results/mvn-test-M2-kill.txt
Write-Source $orig

Write-Output "Fault seeding logs written to results/"