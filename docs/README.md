# Documentation

Bienvenue dans la documentation complète du plugin `@ahmed-mili/capacitor-thunder-bg-service`.

## ✨ Version actuelle

**UI 100% App-Driven** - Le plugin n'a plus de UI par défaut. Toute l'interface est contrôlée par votre application.

### 🆕 Nouvelles fonctionnalités

- **`viewData`** : Injection dynamique de textes dans n'importe quel `TextView` par ID
- **`buttons`** : Boutons cliquables avec actions personnalisées via `BroadcastReceiver`
- **`customLayout`** : **REQUIS** - Le plugin n'a plus de layout par défaut
- **Persistance automatique** : L'état (layout, viewData, buttons) est automatiquement sauvegardé et restauré

## 📚 Fichiers disponibles

### Guides principaux

1. **[QUICK_START.md](./QUICK_START.md)** - 🚀 Guide de démarrage rapide
   - Installation en 2 minutes
   - Code minimal fonctionnel avec `customLayout` et `viewData`
   - Cas d'usage courants
   - Checklist de configuration

2. **[API_REFERENCE.md](./API_REFERENCE.md)** - 📘 Référence API complète
   - Toutes les méthodes TypeScript/JavaScript
   - Toutes les méthodes Java
   - Signatures complètes avec `viewData` et `buttons`
   - Exemples de code
   - Types et interfaces

3. **[USE_CASES.md](./USE_CASES.md)** - 💡 Cas d'usage pratiques
   - Application de livraison
   - Application de fitness
   - Application de transport (Uber-like)
   - Stepper multi-pages avec boutons
   - Code complet pour chaque cas avec `viewData` et `buttons`

4. **[FEATURES.md](./FEATURES.md)** - ✨ Vue d'ensemble des fonctionnalités
   - Liste complète des fonctionnalités
   - Architecture
   - Capacités techniques (UI dynamique, persistance)
   - Checklist

5. **[ORGANIZATION.md](./ORGANIZATION.md)** - 🏗️ Architecture et organisation
   - Structure des dossiers
   - Organisation du code
   - Imports requis
   - Avantages de l'organisation

## 🎯 Par où commencer ?

### Nouveau utilisateur ?
1. Commencez par **[README.md principal](../README.md)** - Documentation complète avec exemples
2. Puis consultez les **[exemples](../examples/)** - Code prêt à l'emploi

### Besoin d'une référence rapide ?
- **[README.md principal](../README.md)** - Section "API complète"
- **[API_REFERENCE.md](./API_REFERENCE.md)** - Toutes les méthodes détaillées (à créer)

### Cherchez des exemples ?
- **[EXAMPLE_usage.ts](../examples/EXAMPLE_usage.ts)** - Exemples complets avec `viewData` et `buttons`
- **[EXAMPLE_Stepper.ts](../examples/EXAMPLE_Stepper.ts)** - Exemple stepper multi-pages
- **[USE_CASES.md](./USE_CASES.md)** - 5 cas d'usage complets avec code (à créer)

### Voulez comprendre l'architecture ?
- **[ORGANIZATION.md](./ORGANIZATION.md)** - Structure et organisation (à créer)
- **[FEATURES.md](./FEATURES.md)** - Vue d'ensemble des fonctionnalités (à créer)

## 📖 Documentation principale

Pour la documentation complète, consultez le **[README.md principal](../README.md)** à la racine du package.

### 🎨 Points clés de la nouvelle version

1. **`customLayout` est REQUIS** : Le plugin n'a plus de UI par défaut. Vous devez toujours fournir un layout personnalisé.

2. **`viewData` pour l'injection dynamique** :
```typescript
await ThunderBgService.start({
  customLayout: 'notification_online',
  titleViewId: 'txtTitle',
  viewData: {
    txtTitle: 'Online',
    txtSubtitle: 'En attente',
  }
});
```

3. **`buttons` pour les boutons cliquables** :
```typescript
buttons: [
  { viewId: 'btnAction', action: 'com.yourapp.ACTION_CLICK' }
]
```

4. **Persistance automatique** : L'état est automatiquement sauvegardé. Si vous fermez/rouvrez l'app, tout est restauré.

## 🔗 Liens rapides

- [README principal](../README.md) - **Commencer ici !**
- [Exemples](../examples/)
- [Index des exemples](../examples/INDEX.md)

## 📝 Notes importantes

- Tous les layouts XML doivent être dans `android/app/src/main/res/layout/`
- Les IDs des TextViews/Buttons doivent correspondre entre le XML et votre code
- Le `BroadcastReceiver` doit être déclaré dans `AndroidManifest.xml` avec `android:exported="true"`
- Consultez le README principal pour la configuration complète du `BroadcastReceiver`

---

**Bonne lecture !** 📚
