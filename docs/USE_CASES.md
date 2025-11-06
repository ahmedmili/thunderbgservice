# 💡 Cas d'usage pratiques

Exemples réels d'utilisation de `@ahmed-mili/capacitor-thunder-bg-service` pour différents scénarios.

## 🚗 Application de transport (Uber-like)

### Scénario
Une application de transport qui affiche l'état du conducteur, la destination actuelle, et permet d'interagir avec la notification.

### Layout XML (`notification_riding.xml`)

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
        android:text="En cours"
        android:textStyle="bold"
        android:textSize="16sp" />
    
    <TextView
        android:id="@+id/txtDestination"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Destination"
        android:textSize="14sp" />
    
    <TextView
        android:id="@+id/txtElapsedTime"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="00:00:00"
        android:textSize="12sp" />
    
    <Button
        android:id="@+id/btnComplete"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Terminer la course" />
</LinearLayout>
```

### Code TypeScript

```typescript
import { ThunderBgService } from '@ahmed-mili/capacitor-thunder-bg-service';

// Démarrer avec état "En ligne"
async function goOnline() {
  await ThunderBgService.start({
    customLayout: 'notification_online',
    titleViewId: 'txtDriverStatus',
    subtitleViewId: 'txtWaiting',
    timerViewId: 'txtTimer',
    enableLocation: true,
    viewData: {
      txtDriverStatus: 'Disponible',
      txtWaiting: 'En attente de courses',
      txtTimer: '00:00:00',
    },
    buttons: [
      { viewId: 'btnGoOffline', action: 'com.yourapp.ACTION_OFFLINE' },
    ],
  });
}

// Changer vers "En cours"
async function startRide(clientName: string, destination: string) {
  await ThunderBgService.update({
    customLayout: 'notification_riding',
    titleViewId: 'txtDriverStatus',
    subtitleViewId: 'txtDestination',
    timerViewId: 'txtElapsedTime',
    viewData: {
      txtDriverStatus: 'En cours',
      txtDestination: `${clientName} → ${destination}`,
      txtElapsedTime: '00:00:00',
    },
    buttons: [
      { viewId: 'btnComplete', action: 'com.yourapp.ACTION_COMPLETE_RIDE' },
    ],
  });
}

// Mettre à jour la destination dynamiquement
async function updateDestination(newDestination: string) {
  await ThunderBgService.update({
    viewData: {
      txtDestination: newDestination,
    },
  });
}
```

---

## 📦 Application de livraison

### Scénario
Une application de livraison qui affiche le nombre de colis restants et permet de marquer comme livré.

### Layout XML (`notification_delivery.xml`)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="12dp">
    
    <TextView
        android:id="@+id/txtDeliveryStatus"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="En livraison"
        android:textStyle="bold"
        android:textSize="16sp" />
    
    <TextView
        android:id="@+id/txtPackagesRemaining"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="3 colis restants"
        android:textSize="14sp" />
    
    <TextView
        android:id="@+id/txtCurrentAddress"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Adresse actuelle"
        android:textSize="12sp" />
    
    <Button
        android:id="@+id/btnDelivered"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Marquer comme livré" />
</LinearLayout>
```

### Code TypeScript

```typescript
async function startDelivery(packagesCount: number) {
  await ThunderBgService.start({
    customLayout: 'notification_delivery',
    titleViewId: 'txtDeliveryStatus',
    subtitleViewId: 'txtPackagesRemaining',
    enableLocation: true,
    viewData: {
      txtDeliveryStatus: 'En livraison',
      txtPackagesRemaining: `${packagesCount} colis restants`,
      txtCurrentAddress: 'Chargement...',
    },
    buttons: [
      { viewId: 'btnDelivered', action: 'com.yourapp.ACTION_DELIVERED' },
    ],
  });
}

async function updatePackageCount(remaining: number) {
  await ThunderBgService.update({
    viewData: {
      txtPackagesRemaining: `${remaining} colis restants`,
    },
  });
}

async function updateCurrentAddress(address: string) {
  await ThunderBgService.update({
    viewData: {
      txtCurrentAddress: address,
    },
  });
}
```

---

## 🏃 Application de fitness

### Scénario
Une application de fitness qui suit une course en temps réel avec distance, temps, et vitesse.

### Layout XML (`notification_fitness.xml`)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="12dp">
    
    <TextView
        android:id="@+id/txtWorkoutStatus"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Course en cours"
        android:textStyle="bold"
        android:textSize="16sp" />
    
    <TextView
        android:id="@+id/txtDistance"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="0.00 km"
        android:textSize="18sp"
        android:textStyle="bold" />
    
    <TextView
        android:id="@+id/txtTime"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="00:00:00"
        android:textSize="14sp" />
    
    <TextView
        android:id="@+id/txtSpeed"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="0 km/h"
        android:textSize="12sp" />
    
    <Button
        android:id="@+id/btnStop"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Arrêter" />
</LinearLayout>
```

### Code TypeScript

```typescript
async function startWorkout() {
  await ThunderBgService.start({
    customLayout: 'notification_fitness',
    titleViewId: 'txtWorkoutStatus',
    timerViewId: 'txtTime',
    enableLocation: true,
    viewData: {
      txtWorkoutStatus: 'Course en cours',
      txtDistance: '0.00 km',
      txtTime: '00:00:00',
      txtSpeed: '0 km/h',
    },
    buttons: [
      { viewId: 'btnStop', action: 'com.yourapp.ACTION_STOP_WORKOUT' },
    ],
  });
}

async function updateWorkoutStats(distance: number, speed: number) {
  await ThunderBgService.update({
    viewData: {
      txtDistance: `${distance.toFixed(2)} km`,
      txtSpeed: `${speed.toFixed(1)} km/h`,
    },
  });
}
```

---

## 🔢 Stepper multi-pages

### Scénario
Une notification avec plusieurs pages (étapes) et des boutons pour naviguer entre elles.

### Layout XML (`notification_stepper.xml`)

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="12dp">
    
    <TextView
        android:id="@+id/txtStepperTitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Étape 1/4"
        android:textStyle="bold"
        android:textSize="16sp" />
    
    <TextView
        android:id="@+id/txtStepperStep"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Étape 1"
        android:textSize="14sp" />
    
    <TextView
        android:id="@+id/txtStepperDescription"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Description"
        android:textSize="12sp" />
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">
        
        <Button
            android:id="@+id/btnStepperPrev"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Précédent" />
        
        <Button
            android:id="@+id/btnStepperNext"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Suivant" />
    </LinearLayout>
</LinearLayout>
```

### Code TypeScript

```typescript
let currentStep = 1;
const totalSteps = 4;

async function startStepper() {
  await ThunderBgService.start({
    customLayout: 'notification_stepper',
    titleViewId: 'txtStepperTitle',
    enableLocation: false,
    viewData: {
      txtStepperTitle: `Étape ${currentStep}/${totalSteps}`,
      txtStepperStep: `Étape ${currentStep}`,
      txtStepperDescription: getStepDescription(currentStep),
    },
    buttons: [
      { viewId: 'btnStepperNext', action: 'com.yourapp.ACTION_STEPPER_NEXT' },
    ],
  });
}

async function goToNextStep() {
  if (currentStep >= totalSteps) return;
  
  currentStep++;
  await ThunderBgService.update({
    viewData: {
      txtStepperTitle: `Étape ${currentStep}/${totalSteps}`,
      txtStepperStep: `Étape ${currentStep}`,
      txtStepperDescription: getStepDescription(currentStep),
    },
    buttons: currentStep === totalSteps
      ? [
          { viewId: 'btnStepperPrev', action: 'com.yourapp.ACTION_STEPPER_PREV' },
          { viewId: 'btnStepperComplete', action: 'com.yourapp.ACTION_STEPPER_COMPLETE' },
        ]
      : [
          { viewId: 'btnStepperPrev', action: 'com.yourapp.ACTION_STEPPER_PREV' },
          { viewId: 'btnStepperNext', action: 'com.yourapp.ACTION_STEPPER_NEXT' },
        ],
  });
}

async function goToPrevStep() {
  if (currentStep <= 1) return;
  
  currentStep--;
  await ThunderBgService.update({
    viewData: {
      txtStepperTitle: `Étape ${currentStep}/${totalSteps}`,
      txtStepperStep: `Étape ${currentStep}`,
      txtStepperDescription: getStepDescription(currentStep),
    },
    buttons: currentStep === 1
      ? [
          { viewId: 'btnStepperNext', action: 'com.yourapp.ACTION_STEPPER_NEXT' },
        ]
      : [
          { viewId: 'btnStepperPrev', action: 'com.yourapp.ACTION_STEPPER_PREV' },
          { viewId: 'btnStepperNext', action: 'com.yourapp.ACTION_STEPPER_NEXT' },
        ],
  });
}

function getStepDescription(step: number): string {
  const descriptions = [
    'Étape 1: Description',
    'Étape 2: Description',
    'Étape 3: Description',
    'Étape 4: Description',
  ];
  return descriptions[step - 1] || '';
}
```

**Voir l'exemple complet:** [EXAMPLE_Stepper.ts](../examples/EXAMPLE_Stepper.ts)

---

## 📡 Monitoring système

### Scénario
Une application qui surveille l'état du système et affiche des métriques en temps réel.

### Code TypeScript avec tâches en arrière-plan

```typescript
import { ThunderBgService } from '@ahmed-mili/capacitor-thunder-bg-service';

// Démarrer le monitoring
async function startMonitoring() {
  await ThunderBgService.start({
    customLayout: 'notification_monitoring',
    titleViewId: 'txtStatus',
    subtitleViewId: 'txtMetrics',
    enableLocation: false,
    viewData: {
      txtStatus: 'Monitoring actif',
      txtMetrics: 'Chargement...',
    },
  });
  
  // Enregistrer une tâche qui collecte les métriques
  await ThunderBgService.registerTask({
    taskId: 'systemMonitor',
    taskClass: 'com.yourapp.SystemMonitorTask',
    intervalMs: 5000, // Toutes les 5 secondes
  });
  
  // Écouter les événements
  await ThunderBgService.addListener('taskEvent', (data) => {
    if (data.taskId === 'systemMonitor') {
      updateMetrics(data.data);
    }
  });
}

async function updateMetrics(metrics: any) {
  await ThunderBgService.update({
    viewData: {
      txtMetrics: `CPU: ${metrics.cpu}% | RAM: ${metrics.ram}%`,
    },
  });
}
```

---

## 📚 Ressources

- [📘 Guide de démarrage rapide](./QUICK_START.md)
- [📚 Référence API complète](./API_REFERENCE.md)
- [📝 Exemples complets](../examples/)
- [📖 README principal](../README.md)



