/**
 * EXEMPLE: Utilisation du géofencing avec ThunderBgService
 * 
 * Le géofencing permet de créer des zones géographiques avec callbacks automatiques
 * quand l'utilisateur entre ou sort de la zone.
 */
import { ThunderBgService } from '@ahmed-mili/capacitor-thunder-bg-service';

/**
 * Exemple 1: Ajouter une géofence simple
 */
export async function addSimpleGeofence() {
  await ThunderBgService.addGeofence({
    id: 'home_zone',
    latitude: 48.8566, // Paris
    longitude: 2.3522,
    radius: 100, // 100 mètres
    onEnter: 'com.yourapp.ACTION_ENTER_HOME',
    onExit: 'com.yourapp.ACTION_EXIT_HOME',
  });
}

/**
 * Exemple 2: Géofence avec données personnalisées
 */
export async function addGeofenceWithExtras() {
  await ThunderBgService.addGeofence({
    id: 'client_location',
    latitude: 48.8566,
    longitude: 2.3522,
    radius: 50, // 50 mètres
    onEnter: 'com.yourapp.ACTION_ARRIVED_AT_CLIENT',
    onExit: 'com.yourapp.ACTION_LEFT_CLIENT',
    extras: {
      clientId: '123',
      clientName: 'John Doe',
      address: '123 Main St',
    },
  });
}

/**
 * Exemple 3: Application de livraison - Zone de livraison
 */
export async function setupDeliveryZone(deliveryAddress: { lat: number; lng: number; address: string }) {
  await ThunderBgService.addGeofence({
    id: 'delivery_zone',
    latitude: deliveryAddress.lat,
    longitude: deliveryAddress.lng,
    radius: 200, // 200 mètres de rayon
    onEnter: 'com.yourapp.ACTION_ARRIVED_AT_DELIVERY',
    onExit: 'com.yourapp.ACTION_LEFT_DELIVERY_ZONE',
    extras: {
      address: deliveryAddress.address,
      timestamp: new Date().toISOString(),
    },
  });
}

/**
 * Exemple 4: Multiples zones géographiques
 */
export async function setupMultipleZones() {
  // Zone 1: Domicile
  await ThunderBgService.addGeofence({
    id: 'zone_home',
    latitude: 48.8566,
    longitude: 2.3522,
    radius: 100,
    onEnter: 'com.yourapp.ACTION_ENTER_HOME',
    onExit: 'com.yourapp.ACTION_EXIT_HOME',
  });
  
  // Zone 2: Bureau
  await ThunderBgService.addGeofence({
    id: 'zone_office',
    latitude: 48.8606,
    longitude: 2.3376,
    radius: 150,
    onEnter: 'com.yourapp.ACTION_ENTER_OFFICE',
    onExit: 'com.yourapp.ACTION_EXIT_OFFICE',
  });
  
  // Zone 3: Zone de service
  await ThunderBgService.addGeofence({
    id: 'zone_service',
    latitude: 48.8506,
    longitude: 2.3476,
    radius: 500,
    onEnter: 'com.yourapp.ACTION_ENTER_SERVICE_ZONE',
    onExit: 'com.yourapp.ACTION_EXIT_SERVICE_ZONE',
  });
}

/**
 * Exemple 5: Gestionnaire de géofences
 */
export class GeofenceManager {
  private activeGeofences: Set<string> = new Set();
  
  /**
   * Ajoute une géofence et la garde en mémoire
   */
  async addGeofence(id: string, lat: number, lng: number, radius: number, 
                     onEnter?: string, onExit?: string) {
    await ThunderBgService.addGeofence({
      id,
      latitude: lat,
      longitude: lng,
      radius,
      onEnter,
      onExit,
    });
    
    this.activeGeofences.add(id);
    console.log(`Geofence added: ${id}`);
  }
  
  /**
   * Supprime une géofence
   */
  async removeGeofence(id: string) {
    await ThunderBgService.removeGeofence(id);
    this.activeGeofences.delete(id);
    console.log(`Geofence removed: ${id}`);
  }
  
  /**
   * Supprime toutes les géofences
   */
  async removeAll() {
    await ThunderBgService.removeAllGeofences();
    this.activeGeofences.clear();
    console.log('All geofences removed');
  }
  
  /**
   * Liste les géofences actives
   */
  getActiveGeofences(): string[] {
    return Array.from(this.activeGeofences);
  }
}

/**
 * Exemple 6: Configuration BroadcastReceiver (Android)
 * 
 * Dans votre AndroidManifest.xml:
 * <receiver 
 *     android:name=".GeofenceActionReceiver"
 *     android:exported="true"
 *     android:enabled="true">
 *     <intent-filter>
 *         <action android:name="com.yourapp.ACTION_ENTER_HOME"/>
 *         <action android:name="com.yourapp.ACTION_EXIT_HOME"/>
 *         <action android:name="com.yourapp.ACTION_ARRIVED_AT_CLIENT"/>
 *     </intent-filter>
 * </receiver>
 * 
 * Dans GeofenceActionReceiver.java:
 * public class GeofenceActionReceiver extends BroadcastReceiver {
 *     @Override
 *     public void onReceive(Context context, Intent intent) {
 *         String action = intent.getAction();
 *         String geofenceId = intent.getStringExtra("geofenceId");
 *         String eventType = intent.getStringExtra("eventType"); // "ENTER" ou "EXIT"
 *         
 *         if ("com.yourapp.ACTION_ENTER_HOME".equals(action)) {
 *             // Votre logique ici
 *             Log.i("Geofence", "Entered home zone");
 *         }
 *     }
 * }
 */

/**
 * Exemple 7: Utilisation dans une application de livraison
 */
export class DeliveryGeofenceService {
  private geofenceManager = new GeofenceManager();
  
  /**
   * Configure une zone de livraison
   */
  async setupDeliveryZone(lat: number, lng: number, address: string, orderId: string) {
    await this.geofenceManager.addGeofence(
      `delivery_${orderId}`,
      lat,
      lng,
      100, // 100 mètres
      'com.yourapp.ACTION_ARRIVED_AT_DELIVERY',
      'com.yourapp.ACTION_LEFT_DELIVERY_ZONE'
    );
    
    // Mettre à jour la notification
    await ThunderBgService.update({
      notificationTitle: 'En route',
      notificationSubtitle: `Livraison vers ${address}`,
    });
  }
  
  /**
   * Nettoie après la livraison
   */
  async cleanupDeliveryZone(orderId: string) {
    await this.geofenceManager.removeGeofence(`delivery_${orderId}`);
  }
}

