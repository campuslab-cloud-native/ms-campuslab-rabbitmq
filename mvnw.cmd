@echo off
setlocal
set BASE_DIR=%~dp0
set MAVEN_VERSION=3.9.9
set MAVEN_HOME=%BASE_DIR%.mvn\apache-maven-%MAVEN_VERSION%
set ARCHIVE=%BASE_DIR%.mvn\apache-maven-%MAVEN_VERSION%-bin.tar.gz

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
  if not exist "%BASE_DIR%.mvn" mkdir "%BASE_DIR%.mvn"
  echo Downloading Apache Maven %MAVEN_VERSION%...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.tar.gz' -OutFile '%ARCHIVE%'"
  tar -xzf "%ARCHIVE%" -C "%BASE_DIR%.mvn"
)

call "%MAVEN_HOME%\bin\mvn.cmd" %*
