# 🚀 Guide de démarrage rapide

Guide de démarrage rapide pour `@ahmed-mili/capacitor-thunder-bg-service` - Commencez en 5 minutes !

## ⚡ Installation rapide

### 1. Installer le package

```bash
npm install @ahmed-mili/capacitor-thunder-bg-service
```

### 2. Synchroniser avec Capacitor

```bash
npx cap sync android
```

### 3. Créer votre premier layout (REQUIS)

Le plugin n'a **aucune UI par défaut**. Vous devez créer votre propre layout.

Créez `android/app/src/main/res/layout/notification_online.xml` :

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="12dp">
    
    <TextView
        android:id="@+id/txtDriverStatus"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Online"
        android:textStyle="bold"
        android:textSize="16sp" />
    
    <TextView
        android:id="@+id/txtWaiting"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="En attente"
        android:textSize="14sp" />
    
    <TextView
        android:id="@+id/txtTimer"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="00:00:00"
        android:textSize="12sp" />
</LinearLayout>
```

### 4. Code minimal TypeScript

```typescript
import { ThunderBgService } from '@ahmed-mili/capacitor-thunder-bg-service';

// Démarrer le service
await ThunderBgService.start({
  customLayout: 'notification_online', // REQUIS
  titleViewId: 'txtDriverStatus',
  subtitleViewId: 'txtWaiting',
  timerViewId: 'txtTimer',
  enableLocation: true,
  viewData: {
    txtDriverStatus: 'Online',
    txtWaiting: 'En attente de courses',
    txtTimer: '00:00:00',
  },
});

// Mettre à jour la notification
await ThunderBgService.update({
  viewData: {
    txtDriverStatus: 'En cours',
    txtWaiting: 'Course en route',
  },
});

// Arrêter le service
await ThunderBgService.stop();
```

## ✅ Checklist de configuration

- [ ] Package installé (`npm install`)
- [ ] Capacitor synchronisé (`npx cap sync android`)
- [ ] Layout XML créé dans `res/layout/`
- [ ] IDs des TextViews définis dans le XML
- [ ] Code TypeScript avec `customLayout` fourni
- [ ] `viewData` configuré avec les IDs correspondants

## 🎯 Exemple complet

### 1. Layout XML (`notification_online.xml`)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="12dp">
    
    <TextView
        android:id="@+id/txtTitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Online"
        android:textStyle="bold"
        android:textSize="16sp" />
    
    <TextView
        android:id="@+id/txtSubtitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="En attente"
        android:textSize="14sp" />
    
    <TextView
        android:id="@+id/txtTimer"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="00:00:00"
        android:textSize="12sp" />
    
    <Button
        android:id="@+id/btnAction"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Action" />
</LinearLayout>
```

### 2. Code TypeScript

```typescript
import { ThunderBgService } from '@ahmed-mili/capacitor-thunder-bg-service';

async function startService() {
  await ThunderBgService.start({
    customLayout: 'notification_online', // REQUIS
    titleViewId: 'txtTitle',
    subtitleViewId: 'txtSubtitle',
    timerViewId: 'txtTimer',
    enableLocation: true,
    viewData: {
      txtTitle: 'Online',
      txtSubtitle: 'En attente de courses',
      txtTimer: '00:00:00',
    },
    buttons: [
      { 
        viewId: 'btnAction', 
        action: 'com.yourapp.ACTION_CLICK' 
      },
    ],
  });
}
```

### 3. BroadcastReceiver (pour les boutons)

Créez `NotifActionReceiver.java` dans votre app :

```java
package com.yourapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotifActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("com.yourapp.ACTION_CLICK".equals(action)) {
            Log.d("Receiver", "Bouton cliqué !");
            // Votre logique ici
        }
    }
}
```

### 4. AndroidManifest.xml

Ajoutez dans `<application>` :

```xml
<receiver
    android:name=".NotifActionReceiver"
    android:exported="true">
    <intent-filter>
        <action android:name="com.yourapp.ACTION_CLICK" />
    </intent-filter>
</receiver>
```

## 🔥 Fonctionnalités principales

### 1. UI Dynamique avec `viewData`

Injectez des textes dans n'importe quel `TextView` :

```typescript
await ThunderBgService.update({
  viewData: {
    txtTitle: 'Nouveau titre',
    txtSubtitle: 'Nouveau sous-titre',
    txtCustom: 'Texte personnalisé',
  },
});
```

### 2. Boutons cliquables avec `buttons`

Rendez vos boutons interactifs :

```typescript
await ThunderBgService.update({
  buttons: [
    { viewId: 'btnNext', action: 'com.yourapp.ACTION_NEXT' },
    { viewId: 'btnPrev', action: 'com.yourapp.ACTION_PREV' },
  ],
});
```

### 3. Persistance automatique

L'état est automatiquement sauvegardé. Si vous fermez/rouvrez l'app, tout est restauré !

## 📚 Prochaines étapes

- [📘 Référence API complète](./API_REFERENCE.md) - Toutes les méthodes détaillées
- [💡 Cas d'usage pratiques](./USE_CASES.md) - Exemples réels
- [📝 Exemples complets](../examples/EXAMPLE_usage.ts) - Code prêt à l'emploi
- [📖 README principal](../README.md) - Documentation complète

## ❓ Questions fréquentes

**Q: Pourquoi dois-je créer un layout ?**  
A: Le plugin n'a plus de UI par défaut. Vous contrôlez 100% de l'interface.

**Q: Comment persister l'état ?**  
A: C'est automatique ! Le plugin sauvegarde et restaure automatiquement l'état.

**Q: Les boutons ne fonctionnent pas ?**  
A: Vérifiez que le `BroadcastReceiver` est déclaré dans `AndroidManifest.xml` avec `android:exported="true"`.

---

**Prêt à continuer ?** Consultez les [exemples complets](../examples/) ou la [documentation complète](../README.md) !

