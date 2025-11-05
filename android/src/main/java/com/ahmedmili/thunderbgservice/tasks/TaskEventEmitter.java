package com.ahmedmili.thunderbgservice.tasks;

import android.content.Context;
import android.util.Log;
import com.getcapacitor.JSObject;
import com.ahmedmili.thunderbgservice.core.ThunderBgServicePlugin;
import org.json.JSONObject;

/**
 * Émetteur d'événements vers JS (si l'app est active).
 * Permet aux tâches Java d'émettre des événements vers le code JS/TS.
 * 
 * Note: Les événements ne sont émis que si l'app est active (process JS vivant).
 * Si l'app est fermée, utilisez TaskResultStorage pour stocker les données.
 */
public class TaskEventEmitter {
    private static final String TAG = "TaskEventEmitter";
    
    public static void emit(Context context, String taskId, Object data) {
        try {
            ThunderBgServicePlugin plugin = ThunderBgServicePlugin.getInstance();
            if (plugin != null) {
                JSObject payload = new JSObject();
                payload.put("taskId", taskId);
                payload.put("data", data);
                payload.put("timestamp", System.currentTimeMillis());
                plugin.emitTaskEvent("taskEvent", payload);
                Log.d(TAG, "Event emitted to JS: " + taskId);
            } else {
                // Plugin non disponible (app fermée), stocker dans ResultStorage
                Log.d(TAG, "Plugin not available, storing in ResultStorage");
                if (data instanceof String) {
                    TaskResultStorage.saveResult(context, taskId, "lastEvent", (String) data);
                } else {
                    JSONObject json = new JSONObject();
                    json.put("data", data.toString());
                    TaskResultStorage.saveResult(context, taskId, json);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error emitting event", e);
            // En cas d'erreur, stocker dans ResultStorage
            try {
                if (data instanceof String) {
                    TaskResultStorage.saveResult(context, taskId, "lastEvent", (String) data);
                } else {
                    JSONObject json = new JSONObject();
                    json.put("data", data.toString());
                    TaskResultStorage.saveResult(context, taskId, json);
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error storing fallback", ex);
            }
        }
    }
    
    public static void emit(Context context, String taskId, JSONObject data) {
        try {
            ThunderBgServicePlugin plugin = ThunderBgServicePlugin.getInstance();
            if (plugin != null) {
                JSObject payload = new JSObject();
                payload.put("taskId", taskId);
                payload.put("data", data);
                payload.put("timestamp", System.currentTimeMillis());
                plugin.emitTaskEvent("taskEvent", payload);
                Log.d(TAG, "Event emitted to JS: " + taskId);
            } else {
                // Plugin non disponible, stocker dans ResultStorage
                TaskResultStorage.saveResult(context, taskId, data);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error emitting event", e);
            try {
                TaskResultStorage.saveResult(context, taskId, data);
            } catch (Exception ex) {
                Log.e(TAG, "Error storing fallback", ex);
            }
        }
    }
}

