package com.ahmedmili.thunderbgservice.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Gestionnaire de tâches en arrière-plan.
 * Permet d'enregistrer des tâches qui s'exécutent périodiquement même si l'app est fermée.
 */
public class BackgroundTaskManager {
    private static final String TAG = "BackgroundTaskManager";
    private static final String PREFS_NAME = "thunder_bg_tasks";
    private static final String PREF_TASK_PREFIX = "task_";
    
    private static final Map<String, ScheduledFuture<?>> activeTasks = new ConcurrentHashMap<>();
    private static final Map<String, BackgroundTask> taskInstances = new ConcurrentHashMap<>();
    private static ScheduledExecutorService scheduler = null;
    
    /**
     * Enregistre une tâche pour exécution périodique.
     */
    public static boolean registerTask(Context context, String taskId, BackgroundTask task, long intervalMs) {
        if (taskId == null || task == null || intervalMs < 1000) {
            Log.e(TAG, "Invalid task parameters");
            return false;
        }
        unregisterTask(context, taskId);
        if (scheduler == null) {
            scheduler = Executors.newScheduledThreadPool(2);
        }
        taskInstances.put(taskId, task);
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
            () -> {
                try {
                    task.execute(context.getApplicationContext(), taskId);
                    Log.d(TAG, "Task executed: " + taskId);
                } catch (Exception e) {
                    Log.e(TAG, "Error executing task: " + taskId, e);
                }
            },
            0,
            intervalMs,
            TimeUnit.MILLISECONDS
        );
        activeTasks.put(taskId, future);
        saveTaskConfig(context, taskId, task.getClass().getName(), intervalMs);
        try {
            task.onRegistered(context.getApplicationContext(), taskId);
        } catch (Exception e) {
            Log.w(TAG, "Error calling onRegistered for task: " + taskId, e);
        }
        Log.i(TAG, "Task registered: " + taskId + " (interval: " + intervalMs + "ms)");
        return true;
    }
    
    public static boolean unregisterTask(Context context, String taskId) {
        ScheduledFuture<?> future = activeTasks.remove(taskId);
        if (future != null) {
            future.cancel(false);
            Log.i(TAG, "Task cancelled: " + taskId);
        }
        BackgroundTask task = taskInstances.remove(taskId);
        if (task != null) {
            try {
                task.onUnregistered(context.getApplicationContext(), taskId);
            } catch (Exception e) {
                Log.w(TAG, "Error calling onUnregistered for task: " + taskId, e);
            }
        }
        removeTaskConfig(context, taskId);
        return true;
    }
    
    public static boolean isTaskRegistered(String taskId) {
        return activeTasks.containsKey(taskId);
    }
    
    public static void stopAll(Context context) {
        for (String taskId : new HashMap<>(activeTasks).keySet()) {
            unregisterTask(context, taskId);
        }
        if (scheduler != null) {
            scheduler.shutdown();
            scheduler = null;
        }
        Log.i(TAG, "All tasks stopped");
    }
    
    private static void saveTaskConfig(Context context, String taskId, String className, long intervalMs) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
            .putString(PREF_TASK_PREFIX + taskId + "_class", className)
            .putLong(PREF_TASK_PREFIX + taskId + "_interval", intervalMs)
            .apply();
    }
    
    private static void removeTaskConfig(Context context, String taskId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
            .remove(PREF_TASK_PREFIX + taskId + "_class")
            .remove(PREF_TASK_PREFIX + taskId + "_interval")
            .apply();
    }
    
    public static TaskConfig getTaskConfig(Context context, String taskId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String className = prefs.getString(PREF_TASK_PREFIX + taskId + "_class", null);
        long interval = prefs.getLong(PREF_TASK_PREFIX + taskId + "_interval", 0);
        if (className != null && interval > 0) {
            return new TaskConfig(className, interval);
        }
        return null;
    }
    
    public static class TaskConfig {
        public final String className;
        public final long intervalMs;
        
        public TaskConfig(String className, long intervalMs) {
            this.className = className;
            this.intervalMs = intervalMs;
        }
    }
}

