# 👁️ EyeRest Reminder

A simple Android app that helps you follow the **20-20-20 rule** to protect your eyes from digital eye strain.

## 📱 What is the 20-20-20 Rule?

Every **20 minutes**, look at something **20 feet away** for at least **20 seconds**. This helps reduce eye strain caused by prolonged screen time.

## ✨ Features

- 🔔 **Smart Notifications**: Regular reminders every 20 minutes
- ⏰ **Background Service**: Works even when the app is closed
- 🎯 **Simple Interface**: Easy start/stop controls
- 🔋 **Battery Optimized**: Minimal resource usage
- 📱 **Modern Design**: Material Design UI

## 🚀 Getting Started

### Prerequisites

- Android device running **API 21 (Android 5.0)** or higher
- **Notification permissions** (Android 13+)
- **Exact alarm permissions** (Android 12+)

### Installation

#### Option 1: Download APK
1. Go to the [Releases](../../releases) page
2. Download the latest `app-debug.apk`
3. Enable "Install from unknown sources" in your device settings
4. Install the APK

#### Option 2: Build from Source
```bash
# Clone the repository
git clone https://github.com/yourusername/EyeRest.git
cd EyeRest

# Build the project
./gradlew assembleDebug

# Install to connected device
./gradlew installDebug
```

## 🛠️ Development

### Tech Stack

- **Language**: Kotlin
- **UI**: Android Views with Material Design
- **Architecture**: MVVM pattern
- **Notifications**: Android Notification API
- **Background Tasks**: BroadcastReceiver + AlarmManager
- **Build System**: Gradle

### Project Structure

```
app/
├── src/main/
│   ├── java/com/example/eyerestreminder/
│   │   ├── MainActivity.kt          # Main UI controller
│   │   └── EyeRestReceiver.kt       # Background alarm handler
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml    # Main UI layout
│   │   ├── values/
│   │   │   ├── strings.xml          # App strings
│   │   │   ├── colors.xml           # Color definitions
│   │   │   └── themes.xml           # App themes
│   │   └── drawable/                # App icons and graphics
│   └── AndroidManifest.xml          # App configuration
├── build.gradle                     # App-level build config
└── proguard-rules.pro              # Code obfuscation rules
```

### Building the Project

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/EyeRest.git
   cd EyeRest
   ```

2. **Set up Android SDK**:
   - Install [Android Studio](https://developer.android.com/studio)
   - Or download [Command Line Tools](https://developer.android.com/studio#command-tools)
   - Update `local.properties` with your SDK path

3. **Build**:
   ```bash
   # Debug build
   ./gradlew assembleDebug
   
   # Release build
   ./gradlew assembleRelease
   
   # Run tests
   ./gradlew test
   ```

### Key Components

#### MainActivity.kt
- Handles UI interactions (start/stop buttons)
- Manages notification permissions
- Controls the alarm scheduling

#### EyeRestReceiver.kt
- BroadcastReceiver that triggers every 20 minutes
- Creates and displays eye rest notifications
- Handles notification channel setup

## 📋 Permissions

The app requires these permissions:

- `SCHEDULE_EXACT_ALARM` - For precise 20-minute intervals (Android 12+)
- `POST_NOTIFICATIONS` - For showing eye rest reminders (Android 13+)

## 🎨 Screenshots

*Coming soon...*

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit your changes**: `git commit -m 'Add amazing feature'`
4. **Push to the branch**: `git push origin feature/amazing-feature`
5. **Open a Pull Request**

### Development Guidelines

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Write unit tests for new features
- Update documentation for API changes
- Ensure the app builds successfully

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🏥 Health Disclaimer

This app is designed to help reduce digital eye strain but is not a substitute for professional medical advice. If you experience persistent eye problems, consult an eye care professional.

## 🙏 Acknowledgments

- [Material Design](https://material.io/) for UI guidelines
- [Android Developers](https://developer.android.com/) for documentation
- Eye care professionals who promote the 20-20-20 rule

## 📞 Support

- 🐛 **Bug Reports**: [Open an issue](../../issues)
- 💡 **Feature Requests**: [Open an issue](../../issues)
- 📧 **Contact**: [your.email@example.com](mailto:your.email@example.com)

---

**⭐ Star this repo if it helps you take better care of your eyes!**

---

## 📊 Project Status

- ✅ Core functionality (20-minute reminders)
- ✅ Notification system
- ✅ Background operation
- ✅ Material Design UI
- 🔄 Additional customization options (planned)
- 🔄 Usage statistics (planned)
- 🔄 Reminder sound customization (planned)

---

*Built with ❤️ for healthier screen time*
