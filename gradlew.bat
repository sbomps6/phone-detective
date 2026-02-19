@echo off
set APP_HOME=%~dp0
set GRADLE_WRAPPER_JAR=%APP_HOME%gradle\wrapper\gradle-wrapper.jar

if not exist "%GRADLE_WRAPPER_JAR%" (
    echo Downloading Gradle Wrapper JAR...
    if not exist "%APP_HOME%gradle\wrapper" mkdir "%APP_HOME%gradle\wrapper"
    powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/gradle/gradle/v8.2.0/gradle/wrapper/gradle-wrapper.jar' -OutFile '%GRADLE_WRAPPER_JAR%'"
)

"%JAVA_HOME%\bin\java.exe" -classpath "%GRADLE_WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
