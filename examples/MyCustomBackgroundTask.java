package com.yourpackage; // Changez selon votre package

import android.content.Context;
import android.util.Log;
import com.webify.thunderbgservice.tasks.BackgroundTask;
import com.webify.thunderbgservice.core.ThunderBgServiceHelper;

/**
 * EXEMPLE: Créer une tâche personnalisée qui s'exécute en arrière-plan
 * 
 * Cette tâche s'exécutera périodiquement même si l'app est fermée,
 * car elle tourne dans le service foreground.
 */
public class MyCustomBackgroundTask implements BackgroundTask {
    private static final String TAG = "MyCustomTask";
    
    @Override
    public void execute(Context context, String taskId) {
        // Votre code ici - s'exécute même si l'app est fermée !
        Log.i(TAG, "Tâche exécutée: " + taskId);
        
        // Exemple: Vérifier l'état du réseau
        // NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        // boolean isConnected = info != null && info.isConnected();
        
        // Exemple: Mettre à jour la notification
        // ThunderBgServiceHelper.updateNotification(context, null, "Last check: " + new Date());
        
        // Exemple: Envoyer une requête HTTP
        // sendHttpRequest(context);
        
        // Exemple: Vérifier la localisation
        // checkLocation(context);
    }
    
    // Méthodes privées pour votre logique
    private void sendHttpRequest(Context context) {
        // Votre code HTTP ici
    }
    
    private void checkLocation(Context context) {
        // Votre code de localisation ici
    }
}

/**
 * EXEMPLE: Utilisation dans votre code
 */
class ExampleUsage {
    void registerTask(Context context) {
        // Méthode 1: Enregistrer directement une instance
        MyCustomBackgroundTask task = new MyCustomBackgroundTask();
        ThunderBgServiceHelper.registerTask(context, "myTask1", task, 5000); // Toutes les 5 secondes
        
        // Méthode 2: Enregistrer par nom de classe (instanciation automatique)
        ThunderBgServiceHelper.registerTask(
            context,
            "myTask2",
            "com.yourpackage.MyCustomBackgroundTask", // Nom complet de la classe
            10000 // Toutes les 10 secondes
        );
    }
    
    void unregisterTask(Context context) {
        ThunderBgServiceHelper.unregisterTask(context, "myTask1");
    }
    
    void checkTaskStatus() {
        boolean isRegistered = ThunderBgServiceHelper.isTaskRegistered("myTask1");
        Log.i("Example", "Task registered: " + isRegistered);
    }
}

/**
 * EXEMPLE: Tâche qui met à jour la notification périodiquement
 */
class NotificationUpdateTask implements BackgroundTask {
    private int counter = 0;
    
    @Override
    public void execute(Context context, String taskId) {
        counter++;
        ThunderBgServiceHelper.updateNotification(
            context,
            null, // Garder le titre actuel
            "Updates: " + counter + " (background)" // Mettre à jour le sous-titre
        );
    }
}

/**
 * EXEMPLE: Tâche qui vérifie l'état de l'app périodiquement
 */
class AppStateCheckTask implements BackgroundTask {
    @Override
    public void execute(Context context, String taskId) {
        // Votre logique de vérification ici
        // Par exemple: vérifier si l'utilisateur est toujours connecté,
        // vérifier l'état du serveur, etc.
        
        Log.d("AppStateCheck", "Vérification de l'état de l'app");
        
        // Si quelque chose change, mettez à jour la notification
        // ThunderBgServiceHelper.updateNotification(context, "État changé", "Nouveau statut");
    }
}

