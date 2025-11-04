# Guide rapide de publication

## 🚀 Publication en 5 étapes

### 1. Préparer le package

```bash
# Installer les dépendances et builder
npm install
npm run build
```

### 2. Vérifier le contenu

```bash
# Voir ce qui sera publié
npm pack --dry-run
```

### 3. Se connecter à npm

```bash
npm login
```

### 4. Publier

```bash
npm publish --access public
```

### 5. Vérifier

Visitez: https://www.npmjs.com/package/@webify/capacitor-thunder-bg-service

---

## 📝 Commandes rapides

### Publication manuelle

```bash
npm run build
npm publish --access public
```

### Utiliser le script (Windows)

```bash
publish.bat
```

### Utiliser le script (Linux/Mac)

```bash
chmod +x publish.sh
./publish.sh
```

### Nouvelle version

```bash
# Version patch (0.1.0 → 0.1.1)
npm version patch
npm publish --access public

# Version minor (0.1.0 → 0.2.0)
npm version minor
npm publish --access public
```

---

## ⚠️ Important

- Assurez-vous d'être connecté à npm (`npm login`)
- Vérifiez que vous avez les permissions pour publier sous `@webify`
- Le package doit être buildé avant publication (`npm run build`)
- Testez localement avec `npm pack` avant de publier

---

## 📚 Documentation complète

Voir [docs/PUBLISHING.md](./docs/PUBLISHING.md) pour le guide complet.

