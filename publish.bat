@echo off
REM Script de publication pour @webify/capacitor-thunder-bg-service

echo 🚀 Préparation de la publication...

REM Vérifier que nous sommes dans le bon répertoire
if not exist "package.json" (
    echo ❌ Erreur: package.json introuvable. Exécutez ce script depuis la racine du package.
    exit /b 1
)

REM Nettoyer
echo 🧹 Nettoyage...
call npm run clean

REM Installer les dépendances
echo 📦 Installation des dépendances...
call npm install

REM Builder
echo 🔨 Build du package...
call npm run build

REM Vérifier que dist/ existe
if not exist "dist" (
    echo ❌ Erreur: Le dossier dist/ n'existe pas. Le build a échoué.
    exit /b 1
)

REM Vérifier le contenu qui sera publié
echo 📋 Contenu qui sera publié:
call npm pack --dry-run

REM Demander confirmation
set /p confirm="✅ Continuer avec la publication ? (y/n) "
if /i not "%confirm%"=="y" (
    echo ❌ Publication annulée.
    exit /b 1
)

REM Publier
echo 📤 Publication sur npm...
call npm publish --access public

if %errorlevel% equ 0 (
    echo ✅ Publication réussie !
    echo 📦 Package disponible sur: https://www.npmjs.com/package/@webify/capacitor-thunder-bg-service
) else (
    echo ❌ Erreur lors de la publication.
    exit /b 1
)

