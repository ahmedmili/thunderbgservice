package com.ahmedmili.thunderbgservice.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * Stockage des résultats de tâches pour récupération par JS.
 * Permet aux tâches Java de stocker des données que JS peut récupérer plus tard.
 */
public class TaskResultStorage {
    private static final String TAG = "TaskResultStorage";
    private static final String PREFS_NAME = "thunder_bg_task_results";
    
    public static void saveResult(Context context, String taskId, String key, String value) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String existing = prefs.getString(taskId, "{}");
            JSONObject json = new JSONObject(existing);
            json.put(key, value);
            json.put("timestamp", System.currentTimeMillis());
            prefs.edit().putString(taskId, json.toString()).apply();
        } catch (JSONException e) {
            Log.e(TAG, "Error saving result", e);
        }
    }
    
    public static void saveResult(Context context, String taskId, JSONObject data) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            data.put("timestamp", System.currentTimeMillis());
            prefs.edit().putString(taskId, data.toString()).apply();
        } catch (JSONException e) {
            Log.e(TAG, "Error saving result", e);
        }
    }
    
    public static JSONObject getResult(Context context, String taskId) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String jsonStr = prefs.getString(taskId, null);
            if (jsonStr != null) {
                return new JSONObject(jsonStr);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error getting result", e);
        }
        return null;
    }
    
    public static void clearResult(Context context, String taskId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(taskId).apply();
    }
    
    public static void clearAll(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}

