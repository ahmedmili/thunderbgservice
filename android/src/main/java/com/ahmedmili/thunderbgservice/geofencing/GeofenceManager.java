package com.ahmedmili.thunderbgservice.geofencing;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestionnaire de géofencing pour Android
 * Permet de créer des zones géographiques avec callbacks automatiques
 */
public class GeofenceManager {
    private static final String TAG = "GeofenceManager";
    
    private final Context context;
    private final GeofencingClient geofencingClient;
    private final Map<String, GeofenceConfig> activeGeofences;
    
    /**
     * Configuration d'une géofence
     */
    public static class GeofenceConfig {
        public String id;
        public double latitude;
        public double longitude;
        public float radius; // en mètres
        public String onEnterAction; // Action broadcast lors de l'entrée
        public String onExitAction; // Action broadcast lors de la sortie
        public Map<String, String> extras; // Données supplémentaires
        
        public GeofenceConfig(String id, double lat, double lng, float radius) {
            this.id = id;
            this.latitude = lat;
            this.longitude = lng;
            this.radius = radius;
            this.extras = new HashMap<>();
        }
    }
    
    public GeofenceManager(Context context) {
        this.context = context.getApplicationContext();
        this.geofencingClient = LocationServices.getGeofencingClient(this.context);
        this.activeGeofences = new HashMap<>();
    }
    
    /**
     * Ajoute une géofence
     */
    public void addGeofence(GeofenceConfig config) {
        if (activeGeofences.containsKey(config.id)) {
            Log.w(TAG, "Geofence already exists: " + config.id);
            removeGeofence(config.id);
        }
        
        activeGeofences.put(config.id, config);
        
        Geofence geofence = new Geofence.Builder()
            .setRequestId(config.id)
            .setCircularRegion(config.latitude, config.longitude, config.radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
            .build();
        
        GeofencingRequest request = new GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build();
        
        try {
            geofencingClient.addGeofences(request, getGeofencePendingIntent())
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "Geofence added: " + config.id);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding geofence: " + config.id, e);
                });
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception adding geofence", e);
        }
    }
    
    /**
     * Supprime une géofence
     */
    public void removeGeofence(String geofenceId) {
        if (!activeGeofences.containsKey(geofenceId)) {
            Log.w(TAG, "Geofence not found: " + geofenceId);
            return;
        }
        
        List<String> ids = new ArrayList<>();
        ids.add(geofenceId);
        
        geofencingClient.removeGeofences(ids)
            .addOnSuccessListener(aVoid -> {
                activeGeofences.remove(geofenceId);
                Log.i(TAG, "Geofence removed: " + geofenceId);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error removing geofence: " + geofenceId, e);
            });
    }
    
    /**
     * Supprime toutes les géofences
     */
    public void removeAllGeofences() {
        if (activeGeofences.isEmpty()) {
            return;
        }
        
        List<String> ids = new ArrayList<>(activeGeofences.keySet());
        
        geofencingClient.removeGeofences(ids)
            .addOnSuccessListener(aVoid -> {
                activeGeofences.clear();
                Log.i(TAG, "All geofences removed");
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error removing all geofences", e);
            });
    }
    
    /**
     * Obtient la configuration d'une géofence
     */
    public GeofenceConfig getGeofenceConfig(String geofenceId) {
        return activeGeofences.get(geofenceId);
    }
    
    /**
     * Obtient toutes les géofences actives
     */
    public List<GeofenceConfig> getAllGeofences() {
        return new ArrayList<>(activeGeofences.values());
    }
    
    /**
     * Crée le PendingIntent pour les événements de géofence
     */
    private android.app.PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        return android.app.PendingIntent.getBroadcast(
            context,
            0,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
        );
    }
}

