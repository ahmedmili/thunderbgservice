# Guide de débogage des boutons de notification

## Vérifications à faire dans votre APP (pas le plugin)

### 1. Vérifier les logs Logcat

Filtrez par tag `ThunderBG` et cherchez:

```
✅ "Processing X buttons" - Les boutons sont parsés
✅ "Button bound: viewId=btnToStepper -> action=com.yourapp.ACTION_TO_STEPPER" - Le binding fonctionne
❌ "Button view ID NOT FOUND: btnToStepper" - L'ID n'existe pas dans votre XML
```

### 2. Checklist XML Layout

Dans votre `notification_online.xml` (ou autre), vérifiez:

```xml
<!-- ✅ CORRECT: Button -->
<Button
    android:id="@+id/btnToStepper"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:layout_weight="1"
    android:text="Stepper" />

<!-- ✅ CORRECT: TextView cliquable -->
<TextView
    android:id="@+id/btnOffline"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:layout_weight="1"
    android:text="Offline"
    android:clickable="true"
    android:focusable="true" />

<!-- ❌ INCORRECT: TextView non-cliquable -->
<TextView
    android:id="@+id/btnSomething"
    android:text="Click me" />
```

### 3. Checklist Manifest

Dans `android/app/src/main/AndroidManifest.xml`:

```xml
<application>
    <!-- ✅ Exporté (requis Android 12+) -->
    <receiver 
        android:name=".NotifActionReceiver" 
        android:exported="true">
        <intent-filter>
            <!-- ✅ Actions exactement identiques à celles dans buttons JSON -->
            <action android:name="com.yourapp.ACTION_TO_STEPPER"/>
            <action android:name="com.yourapp.ACTION_OFFLINE"/>
            <action android:name="com.yourapp.ACTION_ONLINE"/>
        </intent-filter>
    </receiver>
</application>
```

### 4. Checklist Receiver

Dans `NotifActionReceiver.java`:

```java
@Override
public void onReceive(Context context, Intent intent) {
    // ✅ Log pour vérifier réception
    android.util.Log.i("NotifActionReceiver", "Received: " + intent.getAction());
    
    String action = intent.getAction();
    if ("com.yourapp.ACTION_TO_STEPPER".equals(action)) {
        // Votre logique
    }
}
```

### 5. Vérifier le JSON buttons

Quand vous appelez `ThunderBgService.update()` ou via Java:

```typescript
// ✅ CORRECT: viewId sans @+id/
buttons: [
  { viewId: 'btnToStepper', action: 'com.yourapp.ACTION_TO_STEPPER' },
  { viewId: 'btnOffline',   action: 'com.yourapp.ACTION_OFFLINE'  },
]

// ❌ INCORRECT: avec @+id/
buttons: [
  { viewId: '@+id/btnToStepper', action: 'com.yourapp.ACTION_TO_STEPPER' },
]
```

### 6. Test minimal

1. **Démarrer le service une fois:**
```typescript
await ThunderBgService.start({
  notificationTitle: 'Test',
  notificationSubtitle: 'Test',
  customLayout: 'notification_online',
  titleViewId: 'txtDriverStatus',
  subtitleViewId: 'txtWaiting',
});
```

2. **Update avec UN SEUL bouton pour tester:**
```typescript
await ThunderBgService.update({
  customLayout: 'notification_online',
  viewData: { txtDriverStatus: 'Test', txtWaiting: 'Click button' },
  buttons: [
    { viewId: 'btnToStepper', action: 'com.yourapp.ACTION_TO_STEPPER' }
  ],
});
```

3. **Vérifier dans Logcat:**
   - Cherchez `"Button bound: viewId=btnToStepper"`
   - Si absent, l'ID n'existe pas dans votre XML
   - Cliquez le bouton et cherchez `"NotifActionReceiver: Received: com.yourapp.ACTION_TO_STEPPER"`

### 7. Problèmes courants

| Problème | Solution |
|----------|----------|
| ID introuvable dans logs | Vérifier que l'ID existe dans XML (`@+id/btnToStepper`) |
| Receiver non appelé | Vérifier Manifest + `android:exported="true"` |
| Action mismatch | Vérifier que l'action dans JSON = action dans Manifest |
| TextView non cliquable | Ajouter `android:clickable="true"` ou utiliser `Button` |

### 8. Test de diagnostic

Créez un Receiver minimal qui log TOUT:

```java
@Override
public void onReceive(Context context, Intent intent) {
    android.util.Log.e("NotifActionReceiver", "=== RECEIVED ===");
    android.util.Log.e("NotifActionReceiver", "Action: " + intent.getAction());
    android.util.Log.e("NotifActionReceiver", "Package: " + intent.getPackage());
    android.util.Log.e("NotifActionReceiver", "Extras: " + intent.getExtras());
}
```

Si ce Receiver ne log rien au clic, le problème est dans le binding PendingIntent ou le Manifest.

