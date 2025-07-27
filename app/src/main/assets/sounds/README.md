# Audio Assets Directory

Place your `chill_alert.mp3` file in this directory.

The app will automatically copy this file to the app's internal storage and use it for notifications.

**File Requirements:**
- Format: MP3
- Size: Recommended under 1MB for faster loading
- Duration: 2-5 seconds recommended for notification sounds

**How it works:**
1. The app copies `chill_alert.mp3` from assets to internal storage
2. The notification system uses this custom sound
3. If the file is not found, the app falls back to the default notification sound

**To add your file:**
1. Place `chill_alert.mp3` in `app/src/main/assets/sounds/`
2. Rebuild the app
3. The custom sound will be used automatically
