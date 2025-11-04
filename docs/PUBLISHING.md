# Guide de publication sur npm

Guide complet pour publier le package `@webify/capacitor-thunder-bg-service` sur npm.

## 📋 Prérequis

1. **Compte npm** - Créez un compte sur [npmjs.com](https://www.npmjs.com/)
2. **npm CLI** - Installez npm si ce n'est pas déjà fait
3. **Organisation npm** - Si vous publiez sous `@webify`, créez l'organisation sur npm

## 🚀 Étapes de publication

### 1. Préparer le package

#### Vérifier `package.json`

Assurez-vous que votre `package.json` contient toutes les informations nécessaires :

```json
{
  "name": "@webify/capacitor-thunder-bg-service",
  "version": "0.1.0",
  "description": "Capacitor 7 plugin - Android foreground service with notification and location",
  "main": "dist/index.js",
  "types": "dist/index.d.ts",
  "files": [
    "dist/",
    "android/",
    "ios/",
    "src/",
    "README.md"
  ],
  "keywords": [
    "capacitor",
    "plugin",
    "android",
    "foreground-service",
    "notification",
    "background-tasks",
    "location"
  ],
  "author": "Votre nom <votre.email@example.com>",
  "license": "MIT",
  "repository": {
    "type": "git",
    "url": "https://github.com/votre-username/votre-repo.git"
  },
  "bugs": {
    "url": "https://github.com/votre-username/votre-repo/issues"
  },
  "homepage": "https://github.com/votre-username/votre-repo#readme"
}
```

#### Vérifier les fichiers à inclure

Le champ `files` dans `package.json` détermine ce qui sera publié. Vérifiez que tous les fichiers nécessaires sont inclus :

- ✅ `dist/` - Code TypeScript compilé
- ✅ `android/` - Code Android natif
- ✅ `ios/` - Code iOS natif (stub)
- ✅ `src/` - Code source TypeScript (optionnel mais recommandé)
- ✅ `README.md` - Documentation

### 2. Build du package

```bash
# Installer les dépendances
npm install

# Builder le TypeScript
npm run build

# Vérifier que dist/ contient les fichiers
ls dist/
```

### 3. Tester localement (optionnel mais recommandé)

```bash
# Créer un package local
npm pack

# Cela crée un fichier .tgz
# Testez-le dans un projet de test :
cd /chemin/vers/votre/projet/test
npm install /chemin/vers/thunder-bg-service/@webify-capacitor-thunder-bg-service-0.1.0.tgz
```

### 4. Se connecter à npm

```bash
# Se connecter à npm
npm login

# Vous serez invité à entrer :
# - Username
# - Password
# - Email
# - OTP (One-Time Password si 2FA activé)
```

### 5. Vérifier l'organisation

Si vous publiez sous `@webify`, assurez-vous que :

1. L'organisation existe sur npmjs.com
2. Vous êtes membre de cette organisation
3. Vous avez les permissions de publication

Pour créer/joindre une organisation :
- Allez sur [npmjs.com](https://www.npmjs.com/)
- Settings → Organizations
- Créez ou joignez l'organisation `@webify`

### 6. Publier le package

```bash
# Version publique (par défaut)
npm publish --access public

# Si vous publiez pour la première fois sous @webify
npm publish --access public --scope=@webify
```

### 7. Vérifier la publication

Après la publication :

1. Vérifiez sur [npmjs.com](https://www.npmjs.com/package/@webify/capacitor-thunder-bg-service)
2. Testez l'installation :
   ```bash
   npm install @webify/capacitor-thunder-bg-service
   ```

## 📝 Gestion des versions

### Version sémantique (SemVer)

Format : `MAJOR.MINOR.PATCH`

- **MAJOR** : Changements incompatibles avec l'API
- **MINOR** : Nouvelles fonctionnalités compatibles
- **PATCH** : Corrections de bugs compatibles

### Publier une nouvelle version

```bash
# Version patch (0.1.0 → 0.1.1)
npm version patch
npm publish --access public

# Version minor (0.1.0 → 0.2.0)
npm version minor
npm publish --access public

# Version major (0.1.0 → 1.0.0)
npm version major
npm publish --access public
```

Ou manuellement :

1. Modifiez `version` dans `package.json`
2. `npm publish --access public`

## 🔐 Authentification et sécurité

### Two-Factor Authentication (2FA)

Recommandé pour la sécurité :

```bash
# Activer 2FA sur npmjs.com
# Settings → Two-Factor Authentication
```

### Tokens d'authentification

Pour CI/CD, utilisez des tokens :

```bash
# Créer un token
npm token create --read-only  # Pour lecture
npm token create              # Pour publication

# Utiliser le token
npm config set //registry.npmjs.org/:_authToken VOTRE_TOKEN
```

## 📦 Configuration `.npmignore`

Créez un fichier `.npmignore` pour exclure les fichiers inutiles :

```
# Dépendances
node_modules/
npm-debug.log*
yarn-debug.log*
yarn-error.log*

# Build temporaire
*.tgz
*.log

# IDE
.vscode/
.idea/
*.swp
*.swo

# OS
.DS_Store
Thumbs.db

# Tests
coverage/
.nyc_output/

# Git
.git/
.gitignore

# Documentation de développement
docs/
examples/
```

## 🏷️ Tags et distributions

### Tags npm

```bash
# Publier avec un tag spécifique
npm publish --tag beta --access public

# Installer un tag spécifique
npm install @webify/capacitor-thunder-bg-service@beta
```

### Tags par défaut

- `latest` : Version stable (par défaut)
- `beta` : Version bêta
- `alpha` : Version alpha
- `next` : Version de développement

## ✅ Checklist avant publication

- [ ] `package.json` est complet et correct
- [ ] Le code TypeScript est compilé (`dist/` existe)
- [ ] Tous les fichiers nécessaires sont inclus
- [ ] Le README.md est à jour
- [ ] Les tests passent (si vous avez des tests)
- [ ] Le package a été testé localement (`npm pack`)
- [ ] Vous êtes connecté à npm (`npm login`)
- [ ] Vous avez les permissions pour publier
- [ ] La version est correcte dans `package.json`

## 🔄 Workflow de publication recommandé

```bash
# 1. Mettre à jour la version
npm version patch  # ou minor, ou major

# 2. Builder le package
npm run build

# 3. Vérifier le contenu
npm pack --dry-run

# 4. Tester localement (optionnel)
npm pack
# Tester dans un projet

# 5. Publier
npm publish --access public

# 6. Créer un tag Git (optionnel)
git tag v0.1.0
git push origin v0.1.0
```

## 🐛 Dépannage

### Erreur : "You do not have permission"

**Solution** :
- Vérifiez que vous êtes membre de l'organisation `@webify`
- Vérifiez que vous avez les permissions de publication
- Contactez l'administrateur de l'organisation

### Erreur : "Package name already exists"

**Solution** :
- Le nom du package est déjà pris
- Changez le nom dans `package.json` ou contactez le propriétaire

### Erreur : "Invalid package name"

**Solution** :
- Le nom doit respecter les règles npm
- Pas de majuscules (sauf pour les scopes)
- Pas de caractères spéciaux

### Erreur : "You must verify your email"

**Solution** :
- Vérifiez votre email sur npmjs.com
- Cliquez sur le lien dans l'email de vérification

## 📚 Ressources

- [npm Documentation](https://docs.npmjs.com/)
- [Package.json Reference](https://docs.npmjs.com/cli/v9/configuring-npm/package-json)
- [Semantic Versioning](https://semver.org/)
- [npm Publishing Guide](https://docs.npmjs.com/packages-and-modules/contributing-packages-to-the-registry)

## 🎯 Exemple de workflow complet

```bash
# 1. Préparation
cd thunder-bg-service
npm install
npm run build

# 2. Vérification
npm pack --dry-run  # Voir ce qui sera publié

# 3. Test local
npm pack
# Dans un autre projet :
# npm install ../thunder-bg-service/@webify-capacitor-thunder-bg-service-0.1.0.tgz

# 4. Publication
npm login
npm publish --access public

# 5. Vérification
npm view @webify/capacitor-thunder-bg-service
```

---

**Bon courage pour votre publication !** 🚀

