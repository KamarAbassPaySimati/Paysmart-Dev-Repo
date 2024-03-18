#!/usr/bin/env bash

# Install AVD files
echo "y" | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --install 'system-images;android-27;google_apis;x86'

for i in {1..2}; do 
  echo "no" | $ANDROID_HOME/cmdline-tools/latest/bin/avdmanager create avd -n andro$i -k 'system-images;android-27;google_apis;x86' --force
done

$ANDROID_HOME/emulator/emulator -list-avds

echo "Restarting emulator"
echo "Starting emulator"

# Start emulator in background
`nohup $ANDROID_HOME/emulator/emulator -avd andro1 -no-snapshot -skin 1080x2400 > /dev/null 2>&1 &` && `$ANDROID_HOME/platform-tools/adb -s emulator-5554 wait-for-device shell 'while [[ -z $(getprop sys.boot_completed | tr -d '\r') ]]; do sleep 1; done; input keyevent 82'`
`nohup $ANDROID_HOME/emulator/emulator -avd andro2 -no-snapshot -skin 1080x2400 > /dev/null 2>&1 &` && `$ANDROID_HOME/platform-tools/adb -s emulator-5556 wait-for-device shell 'while [[ -z $(getprop sys.boot_completed | tr -d '\r') ]]; do sleep 1; done; input keyevent 82'`
echo "Alllllllllll Emulator started..."

$ANDROID_HOME/platform-tools/adb devices

echo "Successfully Installed"

