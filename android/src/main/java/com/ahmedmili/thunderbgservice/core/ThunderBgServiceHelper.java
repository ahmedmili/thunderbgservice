package com.ahmedmili.thunderbgservice.core;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.ahmedmili.thunderbgservice.tasks.BackgroundTask;
import com.ahmedmili.thunderbgservice.tasks.BackgroundTaskManager;
import com.ahmedmili.thunderbgservice.tasks.TaskResultStorage;
import com.ahmedmili.thunderbgservice.tasks.TaskEventEmitter;
import com.ahmedmili.thunderbgservice.helpers.ResourceCache;

/**
 * Classe helper publique pour l'app hôte.
 * Permet d'utiliser les fonctionnalités du plugin depuis le code Java natif de l'app.
 * 
 * Usage dans votre app:
 * import com.ahmedmili.thunderbgservice.core.ThunderBgServiceHelper;
 * 
 * ThunderBgServiceHelper.startService(context, "Online", "Waiting", true);
 */
public class ThunderBgServiceHelper {
    private static final String TAG = "ThunderBgServiceHelper";

    /**
     * Démarre le service depuis le code natif Java de l'app
     */
    public static void startService(
            Context context,
            String title,
            String subtitle,
            boolean enableLocation) {
        startService(context, title, subtitle, enableLocation, false, null, null, null, null);
    }

    /**
     * Démarre le service avec un layout personnalisé
     */
    public static void startService(
            Context context,
            String title,
            String subtitle,
            boolean enableLocation,
            String customLayout,
            String titleViewId,
            String subtitleViewId,
            String timerViewId) {
        startService(context, title, subtitle, enableLocation, false, customLayout, titleViewId, subtitleViewId, timerViewId);
    }

    /**
     * Démarre le service avec tous les paramètres
     */
    public static void startService(
            Context context,
            String title,
            String subtitle,
            boolean enableLocation,
            boolean soundsEnabled,
            String customLayout,
            String titleViewId,
            String subtitleViewId,
            String timerViewId) {
        try {
            Intent extras = new Intent();
            extras.putExtra(FgConstants.EXTRA_TITLE, title != null ? title : "Online");
            extras.putExtra(FgConstants.EXTRA_SUBTITLE, subtitle != null ? subtitle : "Running");
            extras.putExtra(FgConstants.EXTRA_ENABLE_LOCATION, enableLocation);
            extras.putExtra(FgConstants.EXTRA_SOUNDS, soundsEnabled);
            
            if (customLayout != null && !customLayout.isEmpty()) {
                extras.putExtra(FgConstants.EXTRA_CUSTOM_LAYOUT, customLayout);
                if (titleViewId != null) extras.putExtra(FgConstants.EXTRA_TITLE_VIEW_ID, titleViewId);
                if (subtitleViewId != null) extras.putExtra(FgConstants.EXTRA_SUBTITLE_VIEW_ID, subtitleViewId);
                if (timerViewId != null) extras.putExtra(FgConstants.EXTRA_TIMER_VIEW_ID, timerViewId);
            }
            
            ForegroundTaskService.startAction(context, FgConstants.ACTION_START, extras);
            Log.i(TAG, "Service started from native code");
        } catch (Exception e) {
            Log.e(TAG, "Failed to start service", e);
        }
    }

    /**
     * Met à jour la notification depuis le code natif Java
     */
    public static void updateNotification(
            Context context,
            String title,
            String subtitle) {
        updateNotification(context, title, subtitle, null, null, null, null);
    }

    /**
     * Met à jour la notification et change le layout dynamiquement
     */
    public static void updateNotification(
            Context context,
            String title,
            String subtitle,
            String customLayout,
            String titleViewId,
            String subtitleViewId,
            String timerViewId) {
        try {
            Intent extras = new Intent();
            if (title != null) extras.putExtra(FgConstants.EXTRA_TITLE, title);
            if (subtitle != null) extras.putExtra(FgConstants.EXTRA_SUBTITLE, subtitle);
            
            if (customLayout != null && !customLayout.isEmpty()) {
                extras.putExtra(FgConstants.EXTRA_CUSTOM_LAYOUT, customLayout);
                if (titleViewId != null) extras.putExtra(FgConstants.EXTRA_TITLE_VIEW_ID, titleViewId);
                if (subtitleViewId != null) extras.putExtra(FgConstants.EXTRA_SUBTITLE_VIEW_ID, subtitleViewId);
                if (timerViewId != null) extras.putExtra(FgConstants.EXTRA_TIMER_VIEW_ID, timerViewId);
            }
            
            ForegroundTaskService.startAction(context, FgConstants.ACTION_UPDATE, extras);
            Log.i(TAG, "Notification updated from native code");
        } catch (Exception e) {
            Log.e(TAG, "Failed to update notification", e);
        }
    }

    /**
     * Arrête le service depuis le code natif Java
     */
    public static void stopService(Context context) {
        try {
            ForegroundTaskService.startAction(context, FgConstants.ACTION_STOP, null);
            Log.i(TAG, "Service stopped from native code");
        } catch (Exception e) {
            Log.e(TAG, "Failed to stop service", e);
        }
    }

    /**
     * Vérifie si un layout existe dans les ressources de l'app
     * Utilise ResourceCache pour améliorer les performances
     */
    public static boolean layoutExists(Context context, String layoutName) {
        try {
            int layoutId = ResourceCache.getResourceId(context, layoutName, "layout", context.getPackageName());
            return layoutId != 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking layout: " + layoutName, e);
            return false;
        }
    }

    /**
     * Vérifie si un ID de vue existe dans les ressources de l'app
     * Utilise ResourceCache pour améliorer les performances
     */
    public static boolean viewIdExists(Context context, String viewIdName) {
        try {
            int viewId = ResourceCache.getResourceId(context, viewIdName, "id", context.getPackageName());
            return viewId != 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking view ID: " + viewIdName, e);
            return false;
        }
    }

    /**
     * Enregistre une tâche en arrière-plan qui s'exécute périodiquement.
     * La tâche continue de s'exécuter même si l'app est fermée.
     * 
     * @param context Context
     * @param taskId ID unique de la tâche
     * @param task Instance de la tâche (doit implémenter BackgroundTask)
     * @param intervalMs Intervalle en millisecondes (minimum 1000ms)
     * @return true si enregistré avec succès
     */
    public static boolean registerTask(Context context, String taskId, BackgroundTask task, long intervalMs) {
        return BackgroundTaskManager.registerTask(context, taskId, task, intervalMs);
    }

    /**
     * Enregistre une tâche par nom de classe (instanciation automatique).
     * 
     * @param context Context
     * @param taskId ID unique de la tâche
     * @param taskClassName Nom complet de la classe (ex: "com.yourpackage.MyTask")
     * @param intervalMs Intervalle en millisecondes
     * @return true si enregistré avec succès
     */
    public static boolean registerTask(Context context, String taskId, String taskClassName, long intervalMs) {
        try {
            Intent extras = new Intent();
            extras.putExtra(FgConstants.EXTRA_TASK_ID, taskId);
            extras.putExtra(FgConstants.EXTRA_TASK_CLASS, taskClassName);
            extras.putExtra(FgConstants.EXTRA_TASK_INTERVAL, intervalMs);
            ForegroundTaskService.startAction(context, FgConstants.ACTION_REGISTER_TASK, extras);
            Log.i(TAG, "Task registration requested: " + taskId);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to register task", e);
            return false;
        }
    }

    /**
     * Désenregistre une tâche.
     */
    public static boolean unregisterTask(Context context, String taskId) {
        try {
            Intent extras = new Intent();
            extras.putExtra(FgConstants.EXTRA_TASK_ID, taskId);
            ForegroundTaskService.startAction(context, FgConstants.ACTION_UNREGISTER_TASK, extras);
            Log.i(TAG, "Task unregistration requested: " + taskId);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to unregister task", e);
            return false;
        }
    }

    /**
     * Vérifie si une tâche est enregistrée.
     */
    public static boolean isTaskRegistered(String taskId) {
        return BackgroundTaskManager.isTaskRegistered(taskId);
    }

    /**
     * Récupère le résultat stocké d'une tâche.
     * Utile pour récupérer les données stockées par TaskResultStorage.
     */
    public static org.json.JSONObject getTaskResult(Context context, String taskId) {
        return TaskResultStorage.getResult(context, taskId);
    }

    /**
     * Émet un événement vers JS (si l'app est active).
     * Si l'app est fermée, les données sont stockées dans TaskResultStorage.
     */
    public static void emitTaskEvent(Context context, String taskId, Object data) {
        TaskEventEmitter.emit(context, taskId, data);
    }
}

