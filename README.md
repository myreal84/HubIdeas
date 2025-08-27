# HubIdeas

**Ideenspeicher ("Hirn-Auslagerung") für schnelle Notizen**, später automatisch zu To-Dos gruppiert und Projekten zugeordnet. Fokus: ruhiger Dark Mode, lokale Persistenz; später leichte Erinnerungen, wenn Projekte lange nicht angefasst wurden.

## Aktueller Stand
- Kotlin + Jetpack Compose (Material 3, Dark Theme)
- Minimaler Notiz-Screen (in-memory)
- UI: schlankes Layout, Eingabe + Liste

## Nächste Schritte (geplant)
1. **Persistenz mit Room** (Note / Project / Todo + DAO + DB)
2. **Auto-Projektanlage** bei Notizen ohne Projekt
3. **To-Dos aus Notizen** (später AI-unterstützt)

## Projekt starten
- Empfohlen: Android Studio (Koala o. neuer).
- Oder CLI:

```bash
./gradlew assembleDebug
```

## Struktur (wichtigste Datei)
- `app/src/main/java/.../MainActivity.kt` – Compose UI Einstieg

## Build-Tools
- Gradle Wrapper im Repo enthalten (`gradlew`, `gradlew.bat`)

---
_Maintainer: @myreal84_
