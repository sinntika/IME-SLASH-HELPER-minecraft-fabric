# 全バージョン（1.20.x / 1.21.x / 26.x）を順番にビルドして dist\ に集める。
# ※ legacy と modern を並行実行しないこと（共通 src\ を書き換えるため）。
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

if (-not $env:JDK21) { $env:JDK21 = "C:\Program Files\Eclipse Adoptium\jdk-21" }
if (-not $env:JDK25) { $env:JDK25 = "C:\Program Files\Eclipse Adoptium\jdk-25" }

if (Test-Path dist) { Remove-Item dist -Recurse -Force }
New-Item -ItemType Directory -Path dist | Out-Null

Write-Host "==> [1/2] legacy (1.20.x / 1.21.x) with JDK 21"
Push-Location legacy
.\gradlew.bat --no-daemon "-Dorg.gradle.java.home=$env:JDK21" collectJars
Pop-Location

Write-Host "==> [2/2] modern (26.x) with JDK 25"
Push-Location modern
.\gradlew.bat --no-daemon "-Dorg.gradle.java.home=$env:JDK25" collectJars
Pop-Location

Write-Host ""
Write-Host "==> done. jars in dist\:"
Get-ChildItem dist | Select-Object -ExpandProperty Name
