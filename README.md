# HubIdeas

Ein ruhiger **Ideenspeicher** im Dark Mode. Schnell Notizen erfassen – später zu Aufgaben/Projekten ausbauen.

## Aktueller Stand (Step 5 – Daten-Layer mit Projekten)
- **Kotlin + Jetpack Compose (Material 3, Dark Theme)**
- Notiz-UI: **Hinzufügen**, **Umbenennen**, **Löschen**
- Persistenz via **Room**
- **DB v2** mit Tabellen:
  - `projects` (Unique-Index auf `name`), Seed: **Inbox (id=1)**
  - `notes.projectId` (NOT NULL, Default 1) mit **FK → projects(id)**, `ON DELETE RESTRICT`, `ON UPDATE CASCADE`
- UI zeigt **noch keine** Projekt-Auswahl/Chips (Step 6 wurde zurückgenommen)

## Schnellstart
```bash
./gradlew assembleDebug
```

## Architektur
- **MVVM**, **StateFlow**
- **Entities**: `NoteEntity` (FK zu `ProjectEntity`), `ProjectEntity`
- **DAOs/Repos**: Note/Project, Migration 1→2 vorhanden

## Roadmap
1. **Projekt-UI** (Projekt-Chip je Notiz + „Projekt ändern“-Dialog) – *Step 6*
2. **To-Dos** + Relationen – *Step 7*
3. **Navigation** (Projekte/Filter) – *Step 8*
4. Sanfte **Erinnerungen** bei Inaktivität, später AI-Helfer

## Debug/Tipps
- **Wireless Debugging** (Android 11+): Pairing-Code → `adb pair <ip:pairport>` → `adb connect <ip:debugport>`
- **Database Inspector**: Tabellen & FK prüfen (`projects`, `notes.projectId`)
