package com.webify.thunderbgservice.tasks;

import android.content.Context;
import android.util.Log;

/**
 * Interface pour les tâches exécutables en arrière-plan.
 * Implémentez cette interface dans votre app pour créer des tâches personnalisées.
 * 
 * Usage dans votre app:
 * 
 * public class MyCustomTask implements BackgroundTask {
 *     @Override
 *     public void execute(Context context, String taskId) {
 *         // Votre code ici - s'exécute même si l'app est fermée
 *         Log.i("MyTask", "Tâche exécutée: " + taskId);
 *         
 *         // Optionnel: Stocker des données pour que JS les récupère
 *         TaskResultStorage.saveResult(context, taskId, "data", "value");
 *         
 *         // Optionnel: Émettre un événement vers JS (si l'app est active)
 *         TaskEventEmitter.emit(context, taskId, "data");
 *     }
 * }
 * 
 * Puis enregistrez-la:
 * BackgroundTaskManager.registerTask(context, "myTask", new MyCustomTask(), 5000);
 */
public interface BackgroundTask {
    /**
     * Méthode appelée périodiquement par le service.
     * S'exécute même si l'app est fermée.
     * 
     * @param context Context de l'application
     * @param taskId ID unique de la tâche
     */
    void execute(Context context, String taskId);
    
    /**
     * Optionnel: Méthode appelée quand la tâche est enregistrée.
     * Peut être utilisée pour l'initialisation.
     */
    default void onRegistered(Context context, String taskId) {
        // Par défaut, rien à faire
    }
    
    /**
     * Optionnel: Méthode appelée quand la tâche est désenregistrée.
     * Peut être utilisée pour le nettoyage.
     */
    default void onUnregistered(Context context, String taskId) {
        // Par défaut, rien à faire
    }
}

