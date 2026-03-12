@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "PROJECT_DIR=%~dp0"
cd /d "%PROJECT_DIR%"

if "%~1"=="" goto :usage

set "ACTION=%~1"
set "ACTION_ARGS=%~2 %~3 %~4 %~5 %~6 %~7 %~8 %~9"

if /I "%ACTION%"=="help" goto :usage
if /I "%ACTION%"=="-h" goto :usage
if /I "%ACTION%"=="--help" goto :usage
if /I "%ACTION%"=="compose-up" goto :compose_up
if /I "%ACTION%"=="compose-down" goto :compose_down

call :resolve_java
if errorlevel 1 exit /b 1

call :resolve_gradle
if errorlevel 1 exit /b 1

if /I "%ACTION%"=="assemble" goto :assemble
if /I "%ACTION%"=="test" goto :test
if /I "%ACTION%"=="build" goto :build
if /I "%ACTION%"=="run" goto :run

echo [ERROR] Unknown command: %ACTION%
goto :usage_error

:compose_up
call :resolve_docker
if errorlevel 1 exit /b 1
echo [INFO] Starting docker compose services in detached mode
call docker compose -f "%PROJECT_DIR%docker-compose.yml" up -d %ACTION_ARGS%
exit /b %ERRORLEVEL%

:compose_down
call :resolve_docker
if errorlevel 1 exit /b 1
echo [INFO] Stopping docker compose services
call docker compose -f "%PROJECT_DIR%docker-compose.yml" down %ACTION_ARGS%
exit /b %ERRORLEVEL%

:assemble
echo [INFO] Running Gradle task: assemble
call :run_gradle assemble %ACTION_ARGS%
exit /b %ERRORLEVEL%

:test
echo [INFO] Running Gradle task: test
call :run_gradle test %ACTION_ARGS%
exit /b %ERRORLEVEL%

:build
echo [INFO] Running Gradle task: build
call :run_gradle build %ACTION_ARGS%
exit /b %ERRORLEVEL%

:run
echo [INFO] Starting Spring Boot service via bootRun
echo [INFO] Expected database: PostgreSQL on localhost:5432 ^(see docker-compose.yml^)
call :run_gradle bootRun %ACTION_ARGS%
exit /b %ERRORLEVEL%

:resolve_java
set "JAVA_CANDIDATE="
call :try_java_home "%JAVA_HOME%"
if defined JAVA_CANDIDATE goto :java_ready

for /d %%D in ("%ProgramFiles%\Java\jdk-21*") do (
    call :try_java_home "%%~fD"
    if defined JAVA_CANDIDATE goto :java_ready
)

for /d %%D in ("%ProgramFiles%\Eclipse Adoptium\jdk-21*") do (
    call :try_java_home "%%~fD"
    if defined JAVA_CANDIDATE goto :java_ready
)

for /d %%D in ("%ProgramFiles%\Microsoft\jdk-21*") do (
    call :try_java_home "%%~fD"
    if defined JAVA_CANDIDATE goto :java_ready
)

echo [ERROR] JDK 21 or newer was not found.
echo [ERROR] Install JDK 21 and set JAVA_HOME, or place it in a standard Program Files location.
exit /b 1

:java_ready
set "JAVA_HOME=%JAVA_CANDIDATE%"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo [INFO] Using JAVA_HOME=%JAVA_HOME%
exit /b 0

:try_java_home
set "CURRENT_JAVA_HOME=%~1"
if not defined CURRENT_JAVA_HOME exit /b 1
if not exist "%CURRENT_JAVA_HOME%\bin\java.exe" exit /b 1
if not exist "%CURRENT_JAVA_HOME%\release" exit /b 1

set "JAVA_VERSION_RAW="
for /f "usebackq tokens=2 delims==" %%V in (`findstr /b "JAVA_VERSION=" "%CURRENT_JAVA_HOME%\release"`) do (
    set "JAVA_VERSION_RAW=%%~V"
    goto :version_found
)

exit /b 1

:version_found
set "JAVA_VERSION_RAW=!JAVA_VERSION_RAW:"=!"
set "JAVA_VERSION_MAJOR="

for /f "tokens=1 delims=." %%M in ("!JAVA_VERSION_RAW!") do set "JAVA_VERSION_MAJOR=%%M"
if "!JAVA_VERSION_MAJOR!"=="1" (
    for /f "tokens=2 delims=." %%M in ("!JAVA_VERSION_RAW!") do set "JAVA_VERSION_MAJOR=%%M"
)

if not defined JAVA_VERSION_MAJOR exit /b 1
if !JAVA_VERSION_MAJOR! GEQ 21 set "JAVA_CANDIDATE=%CURRENT_JAVA_HOME%"
exit /b 0

:resolve_gradle
set "GRADLE_WRAPPER=%PROJECT_DIR%gradlew.bat"
if exist "%GRADLE_WRAPPER%" exit /b 0

where gradle >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Gradle was not found in PATH, and gradlew.bat is absent.
    echo [ERROR] Install Gradle or add Gradle Wrapper to the project.
    exit /b 1
)

exit /b 0

:resolve_docker
where docker >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker was not found in PATH.
    echo [ERROR] Install Docker Desktop or add docker.exe to PATH.
    exit /b 1
)

if not exist "%PROJECT_DIR%docker-compose.yml" (
    echo [ERROR] docker-compose.yml was not found in project root.
    exit /b 1
)

exit /b 0

:run_gradle
if exist "%GRADLE_WRAPPER%" (
    call "%GRADLE_WRAPPER%" %*
) else (
    call gradle %*
)
exit /b %ERRORLEVEL%

:usage
echo Usage:
echo   service.bat compose-up [docker-compose-options]
echo   service.bat compose-down [docker-compose-options]
echo   service.bat assemble [gradle-options]
echo   service.bat test [gradle-options]
echo   service.bat build [gradle-options]
echo   service.bat run [gradle-options]
echo.
echo Examples:
echo   service.bat compose-up
echo   service.bat compose-down
echo   service.bat build
echo   service.bat test --info
echo   service.bat run --args="--spring.profiles.active=local"
exit /b 0

:usage_error
call :usage
exit /b 1
