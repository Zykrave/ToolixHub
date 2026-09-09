<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# ToolixHub

An offline-first, all-in-one Android multitool app. No ads, no analytics, no
network calls, no server-side dependencies — every tool runs entirely on
your device.

View the original app in AI Studio: https://ai.studio/apps/ea9a0913-ab75-431a-b068-1f84b593771d

## Run Locally

**Prerequisites:** [Android Studio](https://developer.android.com/studio)

1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project
4. Remove this line from the app's `build.gradle.kts` file before your first local run: `signingConfig = signingConfigs.getByName("debugConfig")`
5. Run the app on an emulator or physical device
6. If you have already published this app before, please [request an upload key reset](https://support.google.com/googleplay/android-developer/answer/9842756#zippy=%2Crequest-an-upload-key-reset) in Google Play Console before publishing an update signed with a different key
