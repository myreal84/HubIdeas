# HubIdeas 🧠✨
> Ideen schnell festhalten → Projekte → To-Dos. Mit sanftem Fokus statt Alarm-Flut.

![HubIdeas cover](docs/screenshots/cover.png)

<p align="left">
  <a href="https://kotlinlang.org/"><img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-1.x-7F52FF?logo=kotlin&logoColor=white"></a>
  <a href="https://developer.android.com/jetpack/compose"><img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white"></a>
  <a href="https://developer.android.com/jetpack/androidx/releases/room"><img alt="Room" src="https://img.shields.io/badge/Room-DB-5C6BC0"></a>
  <img alt="Min SDK" src="https://img.shields.io/badge/minSdk-24-3DDC84?logo=android&logoColor=white">
  <img alt="Target SDK" src="https://img.shields.io/badge/targetSdk-36-3DDC84?logo=android&logoColor=white">
  <a href="#license"><img alt="License" src="https://img.shields.io/badge/License-MIT-black"></a>
</p>

---

## ✨ Features (aktueller Stand)
- 🌚 **Dark Theme** mit Material 3
- 🗂️ **Projektübersicht**: Projekte erstellen, umbenennen, in den **Papierkorb** verschieben (Zugriff über 3‑Punkte‑Menü)
- ✅ **Projekt-Detail**: To-Dos hinzufügen, abhaken, löschen
- 📝 **Auto-Notiz beim Projekt-Anlegen**: Es wird automatisch eine gleichnamige **Notiz** im Projekt angelegt (für spätere KI-Verbesserung des Namens)
- 🗑️ **Papierkorb**: Projekte wiederherstellen oder endgültig löschen
- ♻️ **Auto-Löschen**: Einträge im Papierkorb werden **nach 30 Tagen** automatisch entfernt
- ⤴️ **Zurück-Button** in der **Top-App-Bar** (links), **Projekt-Checkbox** rechts neben dem Titel

---

## 📸 Screenshots

| Start (Projekte) | Detail (To-Dos) | Papierkorb |
|---|---|---|
| ![projects](docs/screenshots/projects.png) | ![detail](docs/screenshots/detail.png) | ![trash](docs/screenshots/trash.png) |

---

## 🚀 Setup & Start

### Voraussetzungen
- Android Studio (aktuelle Version)
- Android SDK, Gradle Wrapper (inklusive)
- Gerät/Emulator mit **minSdk 24**, **targetSdk 36**

### Start in der IDE
1. Projekt in Android Studio öffnen
2. Modul **`app`** wählen
3. ▶️ **Run** drücken

### Start per CLI
```bash
./gradlew assembleDebug
# APK dann z.B. via adb installieren:
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### WLAN-Debugging (Kurz)
1. Auf dem Gerät **Entwickleroptionen → Wireless debugging** aktivieren  
2. In Android Studio: **Device Manager → Pair using Wi‑Fi** → QR scannen / Code eingeben  
3. Gerät erscheint als Zielgerät

---

## 🧱 Architektur

- **MVVM** (ViewModel, StateFlow)  
- **Jetpack Compose** (UI)  
- **Room** (lokale Datenbank mit Migrationen)  
- **Kotlin Coroutines** (asynchron)  
- **Navigation Compose** (Screen-Navigation)

### Datenmodell (vereinfacht)
- `projects`: `id`, `name`, `description?`, `isDone`, `createdAt`, `updatedAt?`, `trashedAt?`  
- `todos`: `id`, `projectId` (**FK CASCADE**), `title`, `isDone`, `createdAt`, `trashedAt?`  
- `notes`: `id`, `projectId` (**FK RESTRICT**), `content`, `createdAt`, `trashedAt?`

### Papierkorb-Logik
- „Löschen“ = **Soft Delete** → `trashedAt` wird gesetzt (Projekt **und** alle zugehörigen `todos`/`notes`)
- **Wiederherstellen**: `trashedAt = NULL` (alles wieder sichtbar)
- **Endgültig löschen**: harte Löschung in korrekter Reihenfolge (erst To-Dos, dann Notizen, dann Projekt), um FK-Fehler zu vermeiden
- **Auto-Purge**: Beim Öffnen der DB werden Einträge mit `trashedAt < now − 30 Tage` automatisch entfernt

---

## 📁 Projektstruktur (gekürzt)
```
app/src/main/java/com/.../hubideas/
├─ data/
│  ├─ local/
│  │  ├─ AppDatabase.kt
│  │  ├─ dao/
│  │  │  ├─ ProjectDao.kt
│  │  │  ├─ TodoDao.kt
│  │  │  └─ NoteDao.kt
│  │  └─ entity/
│  │     ├─ ProjectEntity.kt
│  │     ├─ TodoEntity.kt
│  │     └─ NoteEntity.kt
│  └─ repo/
│     ├─ ProjectRepository.kt
│     └─ TodoRepository.kt
└─ ui/
   ├─ MainActivity.kt
   ├─ ProjectListViewModel.kt
   ├─ ProjectDetailViewModel.kt
   └─ TrashViewModel.kt
```

---

## 🗺️ Roadmap / Nächste Schritte
- 🔎 Suche/Filter, Prioritäten, Fälligkeitsdaten
- 🔔 Sanfte Erinnerungen („lange nicht bearbeitet“)
- 🗒️ Notes-Ansicht im Projekt-Detail (lesen/bearbeiten)
- ✂️ KI-gestützte Projektnamen (Original bleibt als Notiz erhalten)
- ☁️ Sync/Backup (z. B. Firestore)
- 🎨 UI-Polish, Animationen, Tests

---

## 🧩 Troubleshooting

**Room: „Migration didn’t properly handle …“**  
→ App **deinstallieren** oder App-Daten löschen und neu starten (frische DB anlegen lassen).

**`FOREIGN KEY constraint failed`**  
→ Tritt bei harter Löschung auf. In der App nutzen wir Soft Delete (Papierkorb) und löschen endgültig **in vorgegebener Reihenfolge**.

**Gerät erscheint nicht (WLAN/USB)**  
→ USB-Debugging/Wireless-Pairing prüfen. In Android Studio im **Device Manager** neu koppeln.

---

## 🤝 Mitwirken
PRs willkommen! Kurze Beschreibung, ggf. Screens anhängen.

---

## 📜 License
MIT – siehe [`LICENSE`](LICENSE).
