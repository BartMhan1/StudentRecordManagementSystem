@echo off
if not exist bin mkdir bin
javac --release 11 -d bin src\*.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b
)
java -cp bin Main
pause
