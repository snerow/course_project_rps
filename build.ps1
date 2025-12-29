# Build script for the project
# This script tries to find Maven and build the project

Write-Host "Searching for Maven..." -ForegroundColor Yellow

# Check if Maven is in PATH
$mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue

if ($mvnCmd) {
    Write-Host "Maven found in PATH: $($mvnCmd.Source)" -ForegroundColor Green
    Write-Host "Building project..." -ForegroundColor Yellow
    & mvn clean compile
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Build completed successfully!" -ForegroundColor Green
        Write-Host "You can now run the project from IntelliJ IDEA" -ForegroundColor Green
    } else {
        Write-Host "Build error. Check the output above." -ForegroundColor Red
    }
} else {
    Write-Host "Maven not found in PATH." -ForegroundColor Red
    Write-Host ""
    Write-Host "Build instructions:" -ForegroundColor Yellow
    Write-Host "1. Open the project in IntelliJ IDEA" -ForegroundColor White
    Write-Host "2. Right-click on pom.xml file" -ForegroundColor White
    Write-Host "3. Select 'Maven' -> 'Reload Project'" -ForegroundColor White
    Write-Host "4. Then select 'Build' -> 'Build Project' (or press Ctrl+F9)" -ForegroundColor White
    Write-Host "5. After successful build, run the project via Run Configuration" -ForegroundColor White
    Write-Host ""
    Write-Host "Or install Maven and add it to PATH:" -ForegroundColor Yellow
    Write-Host "https://maven.apache.org/download.cgi" -ForegroundColor Cyan
}

