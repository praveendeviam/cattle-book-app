# CattleBook

A simple Android app I built for a dairy farmer to replace their paper diary. Tracks daily milk (morning + evening), calculates earnings, and handles settlement payments.

Works fully offline. Data is backed up automatically via Google Auto Backup.

## What it does

- Add morning/evening milk entries with litres
- Edit or delete any entry
- Pick a date range and see total milk + earnings
- Record settlements — just enter the rate, it calculates the amount
- View payment history
- Optional PIN lock if you don't want others opening it

## Stack

Kotlin, Jetpack Compose, Room, Hilt

## Running it

Open in Android Studio, connect a device or start an emulator (Android 8.0+), and hit run.
