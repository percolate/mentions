#!/bin/bash

# Note: $ANDROID_HOME and $GRADLE_HOME are defined in circle.yml
echo "Setting up CircleCI Environment"

# Create cache dirs if they do not exist.
mkdir -p $CACHE_DIR $ANDROID_HOME $GRADLE_HOME

# Fix CircleCI path
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools:$GRADLE_HOME/bin:$PATH"

# Files that get `touch`ed when cached items are installed
ANDROID_SDK_UPDATES_INSTALLED_TOUCH_FILE="/home/ubuntu/cache/android-updated.touch"
GRADLE_UPDATE_INSTALLED_TOUCH_FILE="/home/ubuntu/cache/gradle-updated.touch"

# Install required android tools / updates
if [ ! -e $ANDROID_SDK_UPDATES_INSTALLED_TOUCH_FILE ]; then
  cp -r /usr/local/android-sdk-linux $CACHE_DIR &&
  echo y | android update sdk --no-ui -a --filter "android-24" &&
  echo y | android update sdk --no-ui -a --filter "tools" &&
  echo y | android update sdk --no-ui -a --filter "build-tools-24.0.2" &&
  echo y | android update sdk --no-ui -a --filter "platform-tool" &&
  echo y | android update sdk --no-ui -a --filter "extra-android-m2repository" &&
  echo y | android update sdk --no-ui -a --filter "extra-android-support" &&
  touch $ANDROID_SDK_UPDATES_INSTALLED_TOUCH_FILE
else
  echo "Skipping Android SDK updates.  Clear cache to make them happen again."
fi

# Update Gradle to version 2.14.1
if [ ! -e $GRADLE_UPDATE_INSTALLED_TOUCH_FILE ]; then
  wget https://services.gradle.org/distributions/gradle-2.14.1-all.zip
  unzip -q gradle-2.14.1-all.zip
  mv gradle-2.14.1 $CACHE_DIR
  touch $GRADLE_UPDATE_INSTALLED_TOUCH_FILE
else
  echo "Skipping Gradle update.  Clear cache to make it happen again."
fi
