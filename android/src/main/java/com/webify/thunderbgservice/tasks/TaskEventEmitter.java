package com.webify.thunderbgservice.tasks;

import android.content.Context;
import android.util.Log;
import com.getcapacitor.Bridge;
import com.getcapacitor.Plugin;
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
            Bridge bridge = Bridge.getInstance();
            if (bridge == null) {
                if (data instanceof String) {
                    TaskResultStorage.saveResult(context, taskId, "lastEvent", (String) data);
                } else {
                    JSONObject json = new JSONObject();
                    json.put("data", data.toString());
                    TaskResultStorage.saveResult(context, taskId, json);
                }
                return;
            }
            Plugin plugin = bridge.getPlugin("ThunderBgService");
            if (plugin != null) {
                JSONObject payload = new JSONObject();
                payload.put("taskId", taskId);
                payload.put("data", data);
                payload.put("timestamp", System.currentTimeMillis());
                plugin.notifyListeners("taskEvent", payload);
                Log.d(TAG, "Event emitted to JS: " + taskId);
            } else {
                Log.d(TAG, "Plugin not available, storing in ResultStorage");
                if (data instanceof String) {
                    TaskResultStorage.saveResult(context, taskId, "lastEvent", (String) data);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error emitting event", e);
            try {
                if (data instanceof String) {
                    TaskResultStorage.saveResult(context, taskId, "lastEvent", (String) data);
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error storing fallback", ex);
            }
        }
    }
    
    public static void emit(Context context, String taskId, JSONObject data) {
        try {
            Bridge bridge = Bridge.getInstance();
            if (bridge == null) {
                TaskResultStorage.saveResult(context, taskId, data);
                return;
            }
            Plugin plugin = bridge.getPlugin("ThunderBgService");
            if (plugin != null) {
                JSONObject payload = new JSONObject();
                payload.put("taskId", taskId);
                payload.put("data", data);
                payload.put("timestamp", System.currentTimeMillis());
                plugin.notifyListeners("taskEvent", payload);
            } else {
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

