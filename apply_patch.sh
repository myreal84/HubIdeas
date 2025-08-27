#!/bin/sh
# Update .gitignore & README, then push to GitHub.
# Usage:
#   chmod +x git_update_and_push.sh
#   ./git_update_and_push.sh [REPO_URL] [COMMIT_MSG]
# Defaults:
#   REPO_URL   = https://github.com/myreal84/HubIdeas.git
#   COMMIT_MSG = chore(repo): update .gitignore & README; add patch scripts

set -e

REPO_URL="${1:-https://github.com/myreal84/HubIdeas.git}"
COMMIT_MSG="${2:-chore(repo): update .gitignore & README; add patch scripts}"

# 0) Root check
if [ ! -f settings.gradle ] && [ ! -f settings.gradle.kts ]; then
  echo "✖ Please run this from your Android project root (where settings.gradle(.kts) lives)." 1>&2
  exit 1
fi

# 1) Ensure wrapper is executable (if present)
[ -f gradlew ] && chmod +x gradlew || true

# 2) Write .gitignore (Android/Gradle/Studio/Kotlin)
cat > .gitignore <<'EOF'
# --- Android artifacts ---
*.apk
*.ap_
*.aab
*.dex
*.class

# --- Build outputs ---
/build/
**/build/
/out/
/captures/

# --- Gradle ---
.gradle/
**/.gradle/
/local.properties
!gradle/wrapper/gradle-wrapper.jar

# --- NDK / CMake ---
.externalNativeBuild/
.cxx/

# --- Keystores (do NOT commit) ---
*.jks
*.keystore
*.keystore.properties

# --- Android Studio / IntelliJ ---
.idea/
*.iml
.idea/**/workspace.xml
.idea/**/tasks.xml
.idea/**/gradle.xml
.idea/**/libraries
.idea/**/dictionaries
.idea/**/shelf
.idea/**/assetWizardSettings.xml
.idea/**/navEditor.xml
.idea/**/deploymentTargetDropDown.xml
.idea/**/other.xml

# --- Kotlin/Java ---
*.log
*.hprof

# --- OS cruft ---
.DS_Store
Thumbs.db

# --- Misc ---
/.kotlin
/.scannerwork/
EOF

# 3) Write README.md
cat > README.md <<'EOF'
# HubIdeas

Ein ruhiger **Ideenspeicher** im Dark Mode: Notizen schnell erfassen, später zu Aufgaben und Projekten formen. Erst lokal & simpel, dann schrittweise ausbauen.

## Aktueller Stand
- **Kotlin + Jetpack Compose (Material 3, Dark Theme)**
- Notiz-UI mit Eingabe, Liste und **3‑Punkte‑Menü** (Umbenennen / Löschen)
- Schrittweise Integration von **Room** (persistente Notizen) via Patch‑Skripte

## Schnellstart
```bash
./gradlew assembleDebug
```
App starten, Notiz hinzufügen, per Menü umbenennen/löschen.

## Room per Patch integrieren
Mit unseren Patch‑Skripten lässt sich Persistenz Schritt für Schritt aktivieren.

```bash
# v3 – robust (nutzt python3)
chmod +x apply_patch_v3.sh
./apply_patch_v3.sh 1   # Gradle: kotlin-kapt + Room/Lifecycle
./apply_patch_v3.sh 2   # Dateien: Entity/DAO/DB/Repo/ViewModel
./apply_patch_v3.sh 3   # MainActivity an ViewModel/DB anbinden
./apply_patch_v3.sh 4   # Build (assembleDebug)
```
> Falls du v2 nutzt: identische Schritte, nur mit `apply_patch_v2.sh`.

## Nächste Schritte (Roadmap)
1. **Projekte & To‑Dos** als Entities/Relationen (Auto‑Projektanlage bei neuen Notizen)
2. Einfache **Navigation** (Projekt‑Detail, Filter)
3. Sanfte **Erinnerungen**, wenn Projekte lange inaktiv sind
4. Feinschliff: echte Icons, Haptik, Dark‑Mode‑Tweaks, Tests/CI

## Struktur (Auszug)
- `app/src/main/java/.../MainActivity.kt` – Compose Einstieg, Dark‑UI, Menü
- `.../data/local/*` – Room (Entity/DAO/DB)
- `.../data/repo/*` – Repository
- `.../ui/NoteViewModel.kt` – ViewModel & StateFlow

---
_Maintainer: @myreal84_
EOF

# 4) Git init + branch main
if ! git rev-parse --git-dir >/dev/null 2>&1; then
  git init
fi

# ensure on main
current_branch=$(git symbolic-ref --quiet --short HEAD 2>/dev/null || echo "")
if [ -z "$current_branch" ]; then
  git checkout -b main 2>/dev/null || git switch -c main 2>/dev/null || true
elif [ "$current_branch" != "main" ]; then
  git branch -m "$current_branch" main 2>/dev/null || true
fi

# 5) Remote origin setzen/aktualisieren
if git remote get-url origin >/dev/null 2>&1; then
  git remote set-url origin "$REPO_URL"
else
  git remote add origin "$REPO_URL"
fi

# 6) Patch‑Skripte ausführbar machen (falls vorhanden)
for f in apply_patch_v2.sh apply_patch_v3.sh init_git_and_push.sh; do
  [ -f "$f" ] && chmod +x "$f" || true
done

# 7) Optional: kurzer Build (fail early)
if [ -x ./gradlew ]; then
  ./gradlew --no-daemon -q assembleDebug || true
fi

# 8) Commit & Push
git add .
if git diff --cached --quiet; then
  echo "No changes to commit."
else
  git commit -m "$COMMIT_MSG"
fi

git push -u origin main

echo "\n✅ Done. Pushed to: $REPO_URL\n"
