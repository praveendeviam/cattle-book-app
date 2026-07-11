# Paal Book

A simple Android app built for dairy farmers to replace their paper diary. Tracks daily milk (morning + evening sessions), calculates earnings, and records settlement payments — all in English and Tamil.

Works fully offline. No sign-in, no internet required. Data is backed up automatically via Google Auto Backup.

## Download

Get it on Google Play *(coming soon)* or download the latest APK from the [Releases](https://github.com/praveendeviam/paal-book-app/releases/latest) page.

Requires Android 8.0 (API 24) or above.

## Features

- **Milk entries** — log morning and evening milk in litres; edit or delete any entry
- **Dashboard** — pick a date range to see total milk collected and daily average
- **Record payment** — select a period, enter the rate per litre; amount is auto-calculated
- **Payment history** — all past settlements listed with dates and amounts
- **Milk history** — calendar-style view to browse entries day by day
- **Language** — switch between English and Tamil instantly from the home screen

## How to use

- **Add milk** — tap **+ Add Milk**, choose Morning or Evening, enter the litres
- **Edit / delete** — open History, tap any day, long-press an entry
- **Record a payment** — tap **Record** tab, set the date range and rate; confirm to close the period
- **Switch language** — tap the **த / EN** badge in the top-right of the home screen

## Stack

Kotlin · Jetpack Compose · Material 3 · Room · Navigation Compose · Coroutines · DataStore

## Build from source

```bash
git clone https://github.com/praveendeviam/paal-book-app.git
```

Open in Android Studio, connect a device or start an emulator (Android 8.0+), and hit **Run**.

For a signed release build, create `keystore.properties` in the project root:

```
storeFile=<path-to-your.jks>
storePassword=<password>
keyAlias=<alias>
keyPassword=<password>
```

Then run `./gradlew bundleRelease`.
