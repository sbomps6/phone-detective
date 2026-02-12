#!/usr/bin/env sh
# Gradle Wrapper Bootstrap Script
APP_HOME=$(pwd)
GRADLE_WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if [ ! -e "$GRADLE_WRAPPER_JAR" ]; then
    echo "Downloading Gradle Wrapper JAR..."
    mkdir -p "$APP_HOME/gradle/wrapper"
    curl -L -o "$GRADLE_WRAPPER_JAR" https://raw.githubusercontent.com/gradle/gradle/v8.2.0/gradle/wrapper/gradle-wrapper.jar
fi

exec java -jar "$GRADLE_WRAPPER_JAR" "$@"
