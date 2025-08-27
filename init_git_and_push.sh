#!/usr/bin/env bash
# Usage: run from your Android project root (where settings.gradle(.kts) lives)
# This script creates a .gitignore and README.md, initializes git, and pushes to GitHub.

set -euo pipefail

REPO_URL="https://github.com/myreal84/HubIdeas.git"  # change to SSH if you prefer

# 1) Sanity check: are we in the project root?
if [[ ! -f "settings.gradle" && ! -f "settings.gradle.kts" ]]; then
  echo "✋ Please run this script from your project root (where settings.gradle(.kts) is)." >&2
  exit 1
fi

# 2) Ensure gradlew is executable (if present)
if [[ -f gradlew ]]; then
  chmod +x gradlew
fi

# 3) Create .gitignore (Android + Gradle + JetBrains)
cat > .gitignore <<'EOF'
# --- Android / Gradle ---
*.apk
*.ap_
*.aab
*.dex
*.class

# Build outputs
/out/
/build/
**/build/
/captures/

# Gradle
.gradle/
/local.properties
/.gradle/
!gradle/wrapper/gradle-wrapper.jar

# NDK / CMake
.externalNativeBuild/
.cxx/

# Keystores (do NOT commit)
*.jks
*.keystore
*.keystore.properties

# --- JetBrains / Android Studio ---
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

# Kotlin/Java
*.log

# OS files
.DS_Store
Thumbs.db
EOF

# 4) Create README.md
cat > README.md <<'EOF'
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
EOF

# 5) Initialize git (or reuse) and set main branch
if ! git rev-parse --git-dir > /dev/null 2>&1; then
  git init
  git checkout -b main
else
  current_branch=$(git symbolic-ref --short HEAD || echo "")
  if [[ "$current_branch" != "main" && -n "$current_branch" ]]; then
    git branch -m "$current_branch" main
  fi
fi

# 6) Add and commit
git add .
if git diff --cached --quiet; then
  echo "No changes to commit."
else
  git commit -m "Initial commit: Android Compose scaffold + Dark Theme + README + .gitignore"
fi

# 7) Set remote origin (replace if exists) and push
if git remote | grep -q '^origin$'; then
  git remote remove origin
fi

git remote add origin "$REPO_URL"

echo "\nPushing to $REPO_URL ...\n"

git push -u origin main

echo "\n✅ Done. Repo pushed to: $REPO_URL\n"
