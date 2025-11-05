package com.ahmedmili.thunderbgservice.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.List;
import java.util.Map;

/**
 * BroadcastReceiver pour les événements de géofencing
 */
public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        
        if (event.hasError()) {
            Log.e(TAG, "Geofencing error: " + event.getErrorCode());
            return;
        }
        
        int transition = event.getGeofenceTransition();
        List<Geofence> triggeringGeofences = event.getTriggeringGeofences();
        
        GeofenceManager manager = new GeofenceManager(context);
        
        for (Geofence geofence : triggeringGeofences) {
            GeofenceManager.GeofenceConfig config = manager.getGeofenceConfig(geofence.getRequestId());
            
            if (config == null) {
                continue;
            }
            
            String action = null;
            String eventType = null;
            
            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                action = config.onEnterAction;
                eventType = "ENTER";
            } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                action = config.onExitAction;
                eventType = "EXIT";
            }
            
            if (action != null && !action.isEmpty()) {
                Intent broadcastIntent = new Intent(action);
                broadcastIntent.putExtra("geofenceId", geofence.getRequestId());
                broadcastIntent.putExtra("eventType", eventType);
                broadcastIntent.putExtra("latitude", config.latitude);
                broadcastIntent.putExtra("longitude", config.longitude);
                
                // Ajouter les extras
                if (config.extras != null) {
                    for (Map.Entry<String, String> entry : config.extras.entrySet()) {
                        broadcastIntent.putExtra(entry.getKey(), entry.getValue());
                    }
                }
                
                context.sendBroadcast(broadcastIntent);
                
                // Enregistrer le déclenchement dans les métriques
                com.ahmedmili.thunderbgservice.metrics.PerformanceMetrics.getInstance(context)
                    .recordGeofenceTrigger(eventType);
                
                Log.i(TAG, "Geofence event: " + eventType + " -> " + action + " (id: " + geofence.getRequestId() + ")");
            }
        }
    }
}

