# Index des exemples

Guide rapide pour trouver l'exemple dont vous avez besoin.

## 📁 Structure des exemples

### Java - Tâches en arrière-plan

#### `MyCustomBackgroundTask.java`
**Utilité**: Exemple de base pour créer une tâche personnalisée
**Contenu**:
- Implémentation de `BackgroundTask`
- Méthodes `execute()`, `onRegistered()`, `onUnregistered()`
- Exemples de logique métier

**Quand l'utiliser**: Pour créer votre première tâche en arrière-plan

#### `TaskWithJSCommunication.java`
**Utilité**: Tâche qui communique avec JS/TS
**Contenu**:
- Utilisation de `TaskResultStorage`
- Utilisation de `TaskEventEmitter`
- Stockage et émission de données

**Quand l'utiliser**: Si vous voulez que votre tâche communique avec le code JS

---

### Java - Utilisation native

#### `EXAMPLE_NativeJavaUsage.java`
**Utilité**: Guide complet d'utilisation depuis Java natif
**Contenu**:
- Démarrer/arrêter le service
- Mettre à jour la notification
- Enregistrer des tâches
- Vérifier les layouts

**Quand l'utiliser**: Si vous utilisez le plugin depuis du code Java natif (sans Capacitor bridge)

#### `EXAMPLE_ActivityUsage.java`
**Utilité**: Utilisation dans une Activity Android
**Contenu**:
- Intégration dans une Activity
- Boutons de contrôle
- Gestion du cycle de vie

**Quand l'utiliser**: Pour intégrer le plugin dans une Activity native

---

### TypeScript/JavaScript

#### `EXAMPLE_usage.ts`
**Utilité**: Exemples d'utilisation de base
**Contenu**:
- Démarrer/arrêter le service
- Mettre à jour la notification
- Layouts personnalisés
- Changement dynamique de layout

**Quand l'utiliser**: Pour apprendre les bases du plugin

#### `EXAMPLE_BackgroundTask_usage.ts`
**Utilité**: Utilisation des tâches en arrière-plan
**Contenu**:
- Enregistrer des tâches
- Écouter les événements
- Récupérer les résultats
- Gestionnaire de tâches complet

**Quand l'utiliser**: Pour utiliser les tâches en arrière-plan depuis JS/TS

#### `EXAMPLE_TaskJSCommunication.ts`
**Utilité**: Communication entre tâches Java et JS/TS
**Contenu**:
- Écouter les événements
- Récupérer les résultats stockés
- Polling périodique
- Gestionnaire de communication

**Quand l'utiliser**: Pour communiquer entre vos tâches Java et votre code JS

---

### XML - Layouts de notification

#### `EXAMPLE_notification_online.xml`
**Utilité**: Exemple de layout de notification personnalisé
**Contenu**:
- Structure de base avec TextView
- IDs pour titre, sous-titre, timer
- Styles et dimensions

**Quand l'utiliser**: Comme modèle pour créer vos propres layouts

---

### Documentation

#### `EXAMPLE_README.md`
**Utilité**: Guide général des exemples
**Contenu**:
- Vue d'ensemble
- Exemples de base
- Notifications personnalisées

**Quand l'utiliser**: Pour une introduction générale

#### `EXAMPLE_BackgroundTasks_README.md`
**Utilité**: Guide complet des tâches en arrière-plan
**Contenu**:
- Création de tâches
- Enregistrement
- Communication avec JS
- Bonnes pratiques

**Quand l'utiliser**: Pour comprendre le système de tâches

#### `EXAMPLE_NativeUsage_README.md`
**Utilité**: Guide d'utilisation depuis Java natif
**Contenu**:
- Utilisation de `ThunderBgServiceHelper`
- Toutes les méthodes disponibles
- Exemples complets

**Quand l'utiliser**: Si vous utilisez le plugin depuis Java natif

#### `EXAMPLE_TS_JS_Tasks_README.md`
**Utilité**: Guide de communication JS/TS
**Contenu**:
- Limitations et solutions
- Événements vs stockage
- Exemples de code

**Quand l'utiliser**: Pour comprendre comment communiquer entre Java et JS

---

## 🎯 Guide de navigation rapide

### Je veux...

#### ... créer une tâche en arrière-plan
1. Lire: `EXAMPLE_BackgroundTasks_README.md`
2. Voir: `MyCustomBackgroundTask.java`
3. Utiliser: `EXAMPLE_BackgroundTask_usage.ts`

#### ... utiliser depuis Java natif
1. Lire: `EXAMPLE_NativeUsage_README.md`
2. Voir: `EXAMPLE_NativeJavaUsage.java`
3. Voir: `EXAMPLE_ActivityUsage.java`

#### ... créer une notification personnalisée
1. Lire: `EXAMPLE_README.md`
2. Voir: `EXAMPLE_notification_online.xml`
3. Utiliser: `EXAMPLE_usage.ts` (section layouts)

#### ... faire communiquer Java et JS
1. Lire: `EXAMPLE_TS_JS_Tasks_README.md`
2. Voir: `TaskWithJSCommunication.java`
3. Utiliser: `EXAMPLE_TaskJSCommunication.ts`

#### ... apprendre les bases
1. Lire: `README.md` (racine)
2. Voir: `EXAMPLE_usage.ts`
3. Lire: `docs/QUICK_START.md`

---

## 📝 Notes

- Tous les exemples Java utilisent `com.yourpackage` - changez-le selon votre package
- Tous les layouts XML doivent être dans `android/app/src/main/res/layout/`
- Les IDs des TextViews doivent correspondre entre le XML et votre code

---

## 🔗 Liens utiles

- [README principal](../README.md)
- [Guide de démarrage rapide](../docs/QUICK_START.md)
- [Cas d'usage](../docs/USE_CASES.md)
- [Documentation d'organisation](../docs/ORGANIZATION.md)

