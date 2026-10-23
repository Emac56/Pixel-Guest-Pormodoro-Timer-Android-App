# ⚔ Pixel Quest Pomodoro

A gamified Pomodoro timer for Android. Focus sessions become "quests": finish one to earn XP, coins, and a reward break you chose yourself. Built with HTML/CSS/JavaScript, wrapped in a native Android app using Java and WebView.

> **Version 1.0** · Works fully offline · Android 10 (API 29) and up

<!-- Add screenshots here, e.g. docs/screenshots/home.png -->
<!-- ![Home screen](docs/screenshots/home.png) -->

---

## Why I built it

Regular Pomodoro timers are boring, and I kept quitting them. I wanted one that gives a reason to keep going, so I turned focus time into a small pixel-art RPG: every session moves the hero, gains XP, and unlocks a reward you set for yourself.

## Features

- **Focus / Break / Reward cycle** with automatic long break after every N sessions
- **Quest system**: name what you're working on (e.g. "Study Java OOP")
- **XP, levels, and coins** earned from completed focus sessions
- **Achievements** (First Quest, Coding Apprentice, Focus Warrior, Night Owl)
- **Custom reward**: choose the reward name, length, and description
- **Fully adjustable timers**: focus, short break, long break, sessions per long break
- **Pixel-art world** drawn on an HTML canvas; the hero walks as the timer progresses
- **Alarm that works even when the app is closed** (native Android `AlarmManager`)
- **Timer survives app kill**: state is saved and restored, and XP is still awarded when you reopen the app
- **Offline-first**: no internet permission, no account, no tracking

## Tech stack

| Layer | Technology |
|---|---|
| UI / game logic | HTML, CSS, vanilla JavaScript (no frameworks) |
| Pixel world | Canvas 2D API (sprites defined in code, no image files) |
| Android shell | Java, `WebView`, `WebViewClient` asset serving |
| Native alarms | `AlarmManager`, `BroadcastReceiver`, `NotificationChannel` |
| JS ↔ Java | `@JavascriptInterface` bridge |
| Storage | `localStorage` (progress and timer state) |
| Build | Gradle, Android Studio |

## How it works

```
┌────────────────────────── Android app ──────────────────────────┐
│  MainActivity ── WebView ── loads assets/www/index.html         │
│        │                         │                              │
│        │                  script.js (timer, XP, quests)         │
│        │                         │  window.AndroidBridge        │
│        ▼                         ▼                              │
│  AndroidBridge  ◄────  scheduleAlarm() / cancelAlarm()          │
│        │                                                        │
│        ▼  AlarmManager fires at the end time                    │
│  AlarmReceiver ──► Notifier ──► "Quest complete!" notification  │
└─────────────────────────────────────────────────────────────────┘
```

A few design decisions worth mentioning:

- **The timer stores an end timestamp, not a countdown counter.** This keeps it accurate when the app is in the background or the screen is off.
- **Native alarm instead of a JS timer.** JavaScript stops when Android suspends the app, so the finish notification is scheduled with `AlarmManager` (exact alarms when allowed, with a fallback to inexact).
- **State restore.** The timer's mode, status, and end time are saved, so if Android kills the app, reopening it picks up where it should be.
- **Same code runs in a browser.** The JS checks whether `window.AndroidBridge` exists, so it falls back to the web Notification API outside the app.
- **Hardened WebView.** Assets are served from a private HTTPS origin, file/content access is disabled, requests to any other host are blocked, and navigation away from the app is prevented.
- **Input validation.** Saved data is sanitized on load (type and range checks), so corrupted storage can't break the app.

## Project structure

```
app/src/main/
├── java/com/app/pixelguestpormodorotimer/
│   ├── MainActivity.java     # WebView host, offline asset serving
│   ├── AndroidBridge.java    # JS → Java: schedule/cancel alarm, permission
│   ├── AlarmReceiver.java    # Receives the alarm broadcast
│   └── Notifier.java         # Notification channel + notification
├── assets/www/
│   ├── index.html
│   ├── style.css
│   └── script.js             # All game and timer logic
└── AndroidManifest.xml
```

## Run it

1. Open the project folder in **Android Studio** and let Gradle sync.
2. *(Optional, for the pixel look)* Download **Press Start 2P** from Google Fonts and place `PressStart2P-Regular.ttf` in `app/src/main/assets/www/fonts/`. Without it, the app falls back to a monospace font.
3. Connect a phone with USB debugging on, then press **Run**.
4. For a shareable APK: **Build → Build APK(s)** → `app/build/outputs/apk/debug/app-debug.apk`

The web part also runs in a normal browser: open `app/src/main/assets/www/index.html`.

## Known limitations

- Some phone brands (Xiaomi, Oppo, Realme, Vivo, Infinix) aggressively kill background apps. If the alarm doesn't ring while the app is closed, set the app's battery usage to **Unrestricted**.
- Scheduled alarms are cleared when the phone reboots; they're re-armed the next time the app is opened.
- Progress is stored in WebView `localStorage`, so clearing app data resets it.

## Roadmap

- [ ] Coin shop (hero hats, backgrounds) so coins have a use
- [ ] Session history and weekly stats
- [ ] Ongoing notification with live countdown
- [ ] Export / import progress as JSON

## Author

**Emac** · BSIT student · Spring Boot backend focus
[GitHub](#) · [LinkedIn](#) · [Email](#)
