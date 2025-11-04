package com.yourpackage;

import android.content.Context;
import android.util.Log;
import com.webify.thunderbgservice.tasks.BackgroundTask;
import com.webify.thunderbgservice.tasks.TaskResultStorage;
import com.webify.thunderbgservice.tasks.TaskEventEmitter;
import org.json.JSONObject;

/**
 * EXEMPLE: Tâche qui communique avec JS/TS
 * 
 * Cette tâche montre comment:
 * 1. Stocker des données que JS peut récupérer plus tard
 * 2. Émettre des événements vers JS (si l'app est active)
 */
public class TaskWithJSCommunication implements BackgroundTask {
    private static final String TAG = "TaskWithJS";
    private int counter = 0;
    
    @Override
    public void execute(Context context, String taskId) {
        counter++;
        
        // 1. Stocker des données pour que JS les récupère plus tard
        TaskResultStorage.saveResult(context, taskId, "counter", String.valueOf(counter));
        TaskResultStorage.saveResult(context, taskId, "status", "running");
        TaskResultStorage.saveResult(context, taskId, "lastUpdate", System.currentTimeMillis() + "");
        
        // 2. Émettre un événement vers JS (si l'app est active)
        // Si l'app est fermée, les données sont automatiquement stockées dans ResultStorage
        TaskEventEmitter.emit(context, taskId, "Counter: " + counter);
        
        // 3. Ou émettre un objet JSON complet
        try {
            JSONObject data = new JSONObject();
            data.put("counter", counter);
            data.put("timestamp", System.currentTimeMillis());
            data.put("status", "active");
            TaskEventEmitter.emit(context, taskId, data);
        } catch (Exception e) {
            Log.e(TAG, "Error creating JSON", e);
        }
        
        Log.i(TAG, "Task executed: " + taskId + " (count: " + counter + ")");
    }
    
    @Override
    public void onRegistered(Context context, String taskId) {
        Log.i(TAG, "Task registered: " + taskId);
        // Initialisation ici si nécessaire
    }
    
    @Override
    public void onUnregistered(Context context, String taskId) {
        Log.i(TAG, "Task unregistered: " + taskId);
        // Nettoyage ici si nécessaire
        TaskResultStorage.clearResult(context, taskId);
    }
}

