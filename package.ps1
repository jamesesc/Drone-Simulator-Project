# Remove-Item -Path "out/package/FileWatcher" -Recurse -Force

$jdkPath = "C:\Program Files\Java\jdk-25\bin"
$jpackageExecutable = Join-Path -Path $jdkPath -ChildPath "jpackage.exe"
$appVersion = "0.1.0"
$description = "TCSS360 Drone Simulator"
$appName = "Drone Simulator"
$vendor = "Group 5"
$mainJar = "./Drone Simulator Project.jar"
$inputPath = "./out/artifacts/Drone_Simulator_Project_jar"
$mainClass = "app.Simulation"
$destination = "out/package"

# Execute jpackage with the specified arguments
& $jpackageExecutable `
    --type app-image `
    --app-version $appVersion `
    --description $description `
    --name $appName `
    --vendor $vendor `
    --main-jar $mainJar `
    --input $inputPath `
    --main-class $mainClass `
    --dest $destination `
#     --icon FileWatcher.ico

if ($LASTEXITCODE -eq 0) {
    Write-Host "jpackage completed successfully."
} else {
    Write-Error "jpackage failed with exit code: $LASTEXITCODE"
}