# Ashen Realms Mobile — Cloud Build

A small native Android top-down RPG prototype. This is a lightweight Android project, not a Unity project.

## Build APK without a computer (GitHub Actions)

1. Sign in to GitHub in your phone browser and create a new repository.
2. Upload the project files, including `.github/workflows/android.yml`.
3. Open the repository's Actions tab and wait for **Build Android APK** to finish successfully.
4. Open the workflow run and download the `AshenRealms-debug-apk` artifact.
5. Unzip it to get `app-debug.apk`, then open it on your Android phone to install.

## Controls
- Left virtual stick: move.
- ATTACK: hit nearby slimes.
- POTION: restore up to 40 HP (3 potions).
- Defeat slimes to earn XP and gold; level up with XP.
- Quest completes after 5 slime defeats.

Debug APK is for testing, not a Play Store release.
