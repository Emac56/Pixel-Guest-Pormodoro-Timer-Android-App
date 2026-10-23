# Pixel Quest Pomodoro - Android version

Ang website mo (HTML/CSS/JS) na naka-wrap sa isang Android app (WebView). Gumagana offline.
Naka-target sa **Android 10 (API 29)** at pataas.

## Paano i-run
1. **Android Studio** -> File -> Open -> piliin ang folder na ito (`Pixel Guest Pormodoro Timer`).
2. Hintayin matapos ang **Gradle Sync**. Kung may sasabihin ang Android Studio na i-upgrade ang Gradle/AGP o mag-install ng SDK 35, i-click ang suggested fix.
3. Sa phone: Settings -> About phone -> pindutin ng 7x ang *Build number* -> Developer options -> i-on ang **USB debugging**. Ikabit sa PC, i-allow ang prompt.
4. I-click ang **Run (green play)**. Para sa APK na ipapasa: Build -> Build APK(s) -> `app/build/outputs/apk/debug/app-debug.apk`.

## IMPORTANTE: yung pixel font (1 manual step)
Hindi ko ma-download ang "Press Start 2P" font mula sa environment ko, kaya wala pa ito sa zip.
- Download: https://fonts.google.com/specimen/Press+Start+2P
- Ilagay ang `PressStart2P-Regular.ttf` sa `app/src/main/assets/www/fonts/`
- Kung wala ito, gumagana pa rin ang app pero monospace ang font (hindi gaanong pixel).

## Ano ang binago
- `MainActivity` - WebView na full screen, serve ng `assets/www/` (offline, walang internet permission).
- `AndroidBridge` + `AlarmReceiver` + `Notifier` - native alarm (AlarmManager) para tumunog ang notification kapag tapos na ang focus/reward/break, **kahit naka-close ang app**.
- `script.js` - tatawag sa native alarm kapag may bridge. Dagdag din: **nase-save na ang timer state**, kaya kung ma-kill ng Android ang app habang tumatakbo, hindi mawawala ang timer at makukuha mo pa rin ang XP pag binuksan mo ulit. Gumagana pa rin ito sa normal na browser.
- Bagong pixel hero icon (yung dating `icon-192/512.png` ay sira - mali ang PNG header kaya hindi mabuksan).
- Hindi ginalaw: `app/build.gradle`, `libs.versions.toml`, Gradle wrapper.

## Mga limitasyon (honest)
- **Hindi ko na-compile o na-test sa totoong phone** (walang Android SDK sa environment ko). Yung JS logic lang ang na-test (headless browser). Kung may error sa Gradle/build, i-send ang eksaktong error message.
- Ang alarm ay nawawala kapag nag-reboot ang phone; ire-re-arm ito pag binuksan mo ang app.
- Ang ilang brand (Xiaomi, Oppo/Realme, Vivo, Infinix, atbp.) ay agresibong pumapatay ng background apps. Kung hindi tumunog kapag naka-close: Settings -> Apps -> Pixel Quest Pomodoro -> Battery -> **Unrestricted / No restrictions**, at i-allow ang Autostart kung meron.
- Ang "SND OFF" button ay para lang sa beeps sa loob ng app; ang notification sound ay sundin ang system settings ng notification channel.
- **Wala pang SQLite.** Naka-save pa rin ang progress sa localStorage ng WebView (nagpe-persist kahit i-close ang app). Madadagdag ang SQLite kung gusto mo ng session history/stats.
