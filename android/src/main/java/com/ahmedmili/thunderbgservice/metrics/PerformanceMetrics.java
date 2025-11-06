package com.ahmedmili.thunderbgservice.metrics;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.util.Log;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Collecteur de métriques de performance pour le plugin
 * Suit l'utilisation de la batterie, les temps d'exécution, etc.
 */
public class PerformanceMetrics {
    private static final String TAG = "PerformanceMetrics";
    private static final String PREFS_NAME = "thunder_bg_metrics";
    
    private static PerformanceMetrics instance;
    private final Context context;
    private final SharedPreferences prefs;
    
    // Compteurs
    private final AtomicLong taskExecutionCount = new AtomicLong(0);
    private final AtomicLong taskExecutionTime = new AtomicLong(0); // en millisecondes
    private final AtomicLong notificationUpdateCount = new AtomicLong(0);
    private final AtomicLong locationUpdateCount = new AtomicLong(0);
    private final AtomicLong geofenceTriggerCount = new AtomicLong(0);
    
    // Timestamps
    private long serviceStartTime = 0;
    private long lastBatteryCheckTime = 0;
    private int initialBatteryLevel = -1;
    
    private PerformanceMetrics(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Restaurer les compteurs depuis les préférences
        taskExecutionCount.set(prefs.getLong("task_execution_count", 0));
        taskExecutionTime.set(prefs.getLong("task_execution_time", 0));
        notificationUpdateCount.set(prefs.getLong("notification_update_count", 0));
        locationUpdateCount.set(prefs.getLong("location_update_count", 0));
        geofenceTriggerCount.set(prefs.getLong("geofence_trigger_count", 0));
    }
    
    public static synchronized PerformanceMetrics getInstance(Context context) {
        if (instance == null) {
            instance = new PerformanceMetrics(context);
        }
        return instance;
    }
    
    /**
     * Démarre le tracking du service
     */
    public void startServiceTracking() {
        serviceStartTime = SystemClock.elapsedRealtime();
        initialBatteryLevel = getCurrentBatteryLevel();
        lastBatteryCheckTime = System.currentTimeMillis();
        Log.i(TAG, "Service tracking started");
    }
    
    /**
     * Arrête le tracking du service
     */
    public void stopServiceTracking() {
        long serviceDuration = SystemClock.elapsedRealtime() - serviceStartTime;
        saveMetric("last_service_duration", serviceDuration);
        serviceStartTime = 0;
        Log.i(TAG, "Service tracking stopped");
    }
    
    /**
     * Enregistre l'exécution d'une tâche
     */
    public void recordTaskExecution(long executionTimeMs) {
        taskExecutionCount.incrementAndGet();
        taskExecutionTime.addAndGet(executionTimeMs);
        
        // Sauvegarder périodiquement
        if (taskExecutionCount.get() % 10 == 0) {
            saveMetrics();
        }
    }
    
    /**
     * Enregistre l'exécution d'une tâche avec ID (pour tracking par tâche)
     */
    public void recordTaskExecution(String taskId, long executionTimeMs) {
        recordTaskExecution(executionTimeMs);
        // Sauvegarder aussi par taskId pour statistiques détaillées
        saveMetric("task_" + taskId + "_count", 
            prefs.getLong("task_" + taskId + "_count", 0) + 1);
        saveMetric("task_" + taskId + "_time", 
            prefs.getLong("task_" + taskId + "_time", 0) + executionTimeMs);
    }
    
    /**
     * Enregistre une mise à jour de notification
     */
    public void recordNotificationUpdate() {
        notificationUpdateCount.incrementAndGet();
    }
    
    /**
     * Enregistre une mise à jour de localisation
     */
    public void recordLocationUpdate() {
        locationUpdateCount.incrementAndGet();
    }
    
    /**
     * Enregistre un déclenchement de géofence
     */
    public void recordGeofenceTrigger() {
        geofenceTriggerCount.incrementAndGet();
    }
    
    /**
     * Enregistre un déclenchement de géofence avec type d'événement
     */
    public void recordGeofenceTrigger(String eventType) {
        recordGeofenceTrigger();
        // Sauvegarder aussi par type d'événement
        saveMetric("geofence_" + eventType + "_count",
            prefs.getLong("geofence_" + eventType + "_count", 0) + 1);
    }
    
    /**
     * Obtient toutes les métriques
     */
    public MetricsData getMetrics() {
        MetricsData data = new MetricsData();
        
        data.taskExecutionCount = taskExecutionCount.get();
        data.totalTaskExecutionTime = taskExecutionTime.get();
        data.avgTaskExecutionTime = data.taskExecutionCount > 0 
            ? (double) data.totalTaskExecutionTime / data.taskExecutionCount 
            : 0.0;
        
        data.notificationUpdateCount = notificationUpdateCount.get();
        data.locationUpdateCount = locationUpdateCount.get();
        data.geofenceTriggerCount = geofenceTriggerCount.get();
        
        // Service duration
        if (serviceStartTime > 0) {
            data.serviceUptime = SystemClock.elapsedRealtime() - serviceStartTime;
        } else {
            data.serviceUptime = prefs.getLong("last_service_duration", 0);
        }
        
        // Battery metrics
        data.currentBatteryLevel = getCurrentBatteryLevel();
        if (initialBatteryLevel > 0 && data.currentBatteryLevel > 0) {
            data.batteryDrain = initialBatteryLevel - data.currentBatteryLevel;
        }
        
        // Resource cache stats
        data.resourceCacheStats = com.ahmedmili.thunderbgservice.helpers.ResourceCache.getStats();
        
        return data;
    }
    
    /**
     * Convertit MetricsData en JSONObject pour retour au JS
     */
    public org.json.JSONObject getMetricsAsJson() {
        try {
            MetricsData data = getMetrics();
            org.json.JSONObject json = new org.json.JSONObject();
            
            json.put("taskExecutionCount", data.taskExecutionCount);
            json.put("totalTaskExecutionTime", data.totalTaskExecutionTime);
            json.put("avgTaskExecutionTime", data.avgTaskExecutionTime);
            json.put("notificationUpdateCount", data.notificationUpdateCount);
            json.put("locationUpdateCount", data.locationUpdateCount);
            json.put("geofenceTriggerCount", data.geofenceTriggerCount);
            json.put("serviceUptime", data.serviceUptime);
            json.put("serviceUptimeHours", data.serviceUptime / 3600000.0);
            json.put("currentBatteryLevel", data.currentBatteryLevel);
            json.put("batteryDrain", data.batteryDrain);
            
            // Resource cache stats
            if (data.resourceCacheStats != null) {
                org.json.JSONObject cacheStats = new org.json.JSONObject();
                cacheStats.put("hits", data.resourceCacheStats.hits);
                cacheStats.put("misses", data.resourceCacheStats.misses);
                cacheStats.put("size", data.resourceCacheStats.size);
                cacheStats.put("hitRate", data.resourceCacheStats.hitRate);
                json.put("resourceCache", cacheStats);
            }
            
            return json;
        } catch (Exception e) {
            Log.e(TAG, "Error converting metrics to JSON", e);
            return new org.json.JSONObject();
        }
    }
    
    /**
     * Obtient le niveau de batterie actuel
     */
    private int getCurrentBatteryLevel() {
        try {
            BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            if (batteryManager != null) {
                return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            }
        } catch (Exception e) {
            Log.w(TAG, "Error getting battery level", e);
        }
        return -1;
    }
    
    /**
     * Sauvegarde les métriques dans SharedPreferences
     */
    private void saveMetrics() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("task_execution_count", taskExecutionCount.get());
        editor.putLong("task_execution_time", taskExecutionTime.get());
        editor.putLong("notification_update_count", notificationUpdateCount.get());
        editor.putLong("location_update_count", locationUpdateCount.get());
        editor.putLong("geofence_trigger_count", geofenceTriggerCount.get());
        editor.apply();
    }
    
    /**
     * Sauvegarde une métrique spécifique
     */
    private void saveMetric(String key, long value) {
        prefs.edit().putLong(key, value).apply();
    }
    
    /**
     * Réinitialise toutes les métriques
     */
    public void reset() {
        taskExecutionCount.set(0);
        taskExecutionTime.set(0);
        notificationUpdateCount.set(0);
        locationUpdateCount.set(0);
        geofenceTriggerCount.set(0);
        
        prefs.edit().clear().apply();
        Log.i(TAG, "Metrics reset");
    }
    
    /**
     * Classe pour stocker les données de métriques
     */
    public static class MetricsData {
        public long taskExecutionCount;
        public long totalTaskExecutionTime;
        public double avgTaskExecutionTime;
        public long notificationUpdateCount;
        public long locationUpdateCount;
        public long geofenceTriggerCount;
        public long serviceUptime; // en millisecondes
        public int currentBatteryLevel;
        public int batteryDrain;
        public com.ahmedmili.thunderbgservice.helpers.ResourceCache.CacheStats resourceCacheStats;
        
        @Override
        public String toString() {
            return String.format(
                "MetricsData{taskExecutions=%d, avgTime=%.2fms, notifications=%d, locations=%d, geofences=%d, uptime=%dms, battery=%d%%, drain=%d%%}",
                taskExecutionCount, avgTaskExecutionTime, notificationUpdateCount, 
                locationUpdateCount, geofenceTriggerCount, serviceUptime, 
                currentBatteryLevel, batteryDrain
            );
        }
    }
}
