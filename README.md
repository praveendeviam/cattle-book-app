# CattleBook

A simple Android app I built for a dairy farmer to replace their paper diary. Tracks daily milk (morning + evening), calculates earnings, and handles settlement payments.

Works fully offline. Data is backed up automatically via Google Auto Backup.

## Download & Install

1. Go to the [Releases](https://github.com/praveendeviam/cattle-book-app/releases/latest) page
2. Download **CattleBook-v1.0.0.apk**
3. Open the APK on your Android phone — if prompted, allow installation from unknown sources
4. Done, no sign-in or internet needed

Requires Android 8.0 or above.

## How to use

- **Add entry** — tap the + button, pick morning or evening, enter the litres
- **Edit / delete** — long press any entry
- **Summary** — go to the Summary tab, pick a date range to see total milk and earnings
- **Settlement** — record a payment by entering the rate per litre, the amount is auto-calculated
- **History** — all past payments are listed under the Payments tab
- **PIN lock** — set a PIN from Settings if you want to restrict access

## What it does

- Add morning/evening milk entries with litres
- Edit or delete any entry
- Pick a date range and see total milk + earnings
- Record settlements — just enter the rate, it calculates the amount
- View payment history
- Optional PIN lock if you don't want others opening it

## Stack

Kotlin, Jetpack Compose, Room, Hilt

## Build from source

Open in Android Studio, connect a device or start an emulator (Android 8.0+), and hit run.
