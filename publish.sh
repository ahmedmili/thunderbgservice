#!/bin/bash

# Script de publication pour @webify/capacitor-thunder-bg-service

echo "🚀 Préparation de la publication..."

# Vérifier que nous sommes dans le bon répertoire
if [ ! -f "package.json" ]; then
    echo "❌ Erreur: package.json introuvable. Exécutez ce script depuis la racine du package."
    exit 1
fi

# Nettoyer
echo "🧹 Nettoyage..."
npm run clean

# Installer les dépendances
echo "📦 Installation des dépendances..."
npm install

# Builder
echo "🔨 Build du package..."
npm run build

# Vérifier que dist/ existe
if [ ! -d "dist" ]; then
    echo "❌ Erreur: Le dossier dist/ n'existe pas. Le build a échoué."
    exit 1
fi

# Vérifier le contenu qui sera publié
echo "📋 Contenu qui sera publié:"
npm pack --dry-run

# Demander confirmation
read -p "✅ Continuer avec la publication ? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Publication annulée."
    exit 1
fi

# Publier
echo "📤 Publication sur npm..."
npm publish --access public

if [ $? -eq 0 ]; then
    echo "✅ Publication réussie !"
    echo "📦 Package disponible sur: https://www.npmjs.com/package/@webify/capacitor-thunder-bg-service"
else
    echo "❌ Erreur lors de la publication."
    exit 1
fi

