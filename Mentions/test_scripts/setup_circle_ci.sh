IMPORTANT:
# IF YOU CHANGE THIS FILE YOU MUST LOG INTO CIRCLECI AND CLEAR THE CACHE
# OTHERWISE CHANGES TO THIS FILE WILL NOT EXECUTE
#
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
  echo y | android update sdk --no-ui -a --filter "android-23" &&
  echo y | android update sdk --no-ui -a --filter "build-tools-23.0.0" &&
  echo y | android update sdk --no-ui -a --filter "platform-tool" &&
  echo y | android update sdk --no-ui -a --filter "extra-android-m2repository" &&
  echo y | android update sdk --no-ui -a --filter "extra-android-support" &&
  echo y | android update sdk --no-ui -a --filter "extra-google-m2repository" &&
  echo y | android update sdk --no-ui -a --filter "extra-google-google_play_services" &&
  touch $ANDROID_SDK_UPDATES_INSTALLED_TOUCH_FILE
else
  echo "Skipping Android SDK updates.  Clear cache to make them happen again."
fi

