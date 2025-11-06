package com.yourpackage; // Changez selon votre package

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * EXEMPLE: BroadcastReceiver pour les événements de géofencing
 * 
 * Placez ce fichier dans: android/app/src/main/java/com/votrepackage/
 * 
 * Dans AndroidManifest.xml:
 * <receiver 
 *     android:name=".GeofenceActionReceiver"
 *     android:exported="true"
 *     android:enabled="true">
 *     <intent-filter>
 *         <action android:name="com.yourapp.ACTION_ENTER_HOME"/>
 *         <action android:name="com.yourapp.ACTION_EXIT_HOME"/>
 *         <action android:name="com.yourapp.ACTION_ARRIVED_AT_CLIENT"/>
 *         <!-- Ajoutez toutes vos actions de géofence -->
 *     </intent-filter>
 * </receiver>
 */
public class GeofenceActionReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        
        // Extraire les données de la géofence
        String geofenceId = intent.getStringExtra("geofenceId");
        String eventType = intent.getStringExtra("eventType"); // "ENTER" ou "EXIT"
        double latitude = intent.getDoubleExtra("latitude", 0.0);
        double longitude = intent.getDoubleExtra("longitude", 0.0);
        
        Log.i(TAG, "Geofence event: " + action + " (id: " + geofenceId + ", type: " + eventType + ")");
        
        // Gérer les événements selon l'action
        switch (action) {
            case "com.yourapp.ACTION_ENTER_HOME":
                handleEnterHome(context, geofenceId, latitude, longitude);
                break;
                
            case "com.yourapp.ACTION_EXIT_HOME":
                handleExitHome(context, geofenceId, latitude, longitude);
                break;
                
            case "com.yourapp.ACTION_ARRIVED_AT_CLIENT":
                handleArrivedAtClient(context, geofenceId, latitude, longitude, intent);
                break;
                
            case "com.yourapp.ACTION_LEFT_CLIENT":
                handleLeftClient(context, geofenceId, latitude, longitude);
                break;
                
            default:
                Log.w(TAG, "Unknown geofence action: " + action);
                break;
        }
    }
    
    /**
     * Gère l'entrée dans la zone "home"
     */
    private void handleEnterHome(Context context, String geofenceId, double lat, double lng) {
        Log.i(TAG, "Entered home zone");
        
        // Exemple: Mettre à jour la notification
        // ThunderBgServiceHelper.updateNotification(context, "À domicile", "Vous êtes rentré");
        
        // Exemple: Émettre un événement vers JS (si l'app est active)
        // Vous pouvez utiliser Capacitor pour émettre un événement
    }
    
    /**
     * Gère la sortie de la zone "home"
     */
    private void handleExitHome(Context context, String geofenceId, double lat, double lng) {
        Log.i(TAG, "Exited home zone");
        
        // Votre logique ici
    }
    
    /**
     * Gère l'arrivée chez un client
     */
    private void handleArrivedAtClient(Context context, String geofenceId, double lat, double lng, Intent intent) {
        Log.i(TAG, "Arrived at client location");
        
        // Extraire les extras personnalisés
        String clientId = intent.getStringExtra("clientId");
        String clientName = intent.getStringExtra("clientName");
        
        if (clientId != null) {
            Log.i(TAG, "Client ID: " + clientId);
        }
        if (clientName != null) {
            Log.i(TAG, "Client Name: " + clientName);
        }
        
        // Exemple: Mettre à jour la notification
        // ThunderBgServiceHelper.updateNotification(
        //     context, 
        //     "Arrivé chez le client", 
        //     clientName != null ? clientName : "Client"
        // );
    }
    
    /**
     * Gère le départ d'un client
     */
    private void handleLeftClient(Context context, String geofenceId, double lat, double lng) {
        Log.i(TAG, "Left client location");
        
        // Votre logique ici
    }
}

