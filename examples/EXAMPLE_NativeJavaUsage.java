package com.yourpackage; // Changez selon votre package

import android.content.Context;
import android.util.Log;
import com.webify.thunderbgservice.core.ThunderBgServiceHelper;

/**
 * EXEMPLE: Utilisation du plugin ThunderBgService depuis le code Java natif de l'app
 * 
 * Cette classe montre comment utiliser les classes publiques du plugin
 * sans passer par le bridge Capacitor/JS.
 * 
 * Placez ce fichier dans: android/app/src/main/java/com/votrepackage/
 */
public class MyNativeServiceManager {
    private static final String TAG = "MyNativeServiceManager";
    private Context context;
    
    public MyNativeServiceManager(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Exemple 1: Démarrer le service depuis Java natif
     */
    public void startOnline() {
        ThunderBgServiceHelper.startService(
            context,
            "Disponible",
            "En attente de courses",
            true  // enableLocation
        );
        Log.i(TAG, "Service démarré depuis Java natif");
    }

    /**
     * Exemple 2: Démarrer avec un layout personnalisé
     */
    public void startWithCustomLayout() {
        // Vérifier que le layout existe
        if (ThunderBgServiceHelper.layoutExists(context, "notification_driver")) {
            ThunderBgServiceHelper.startService(
                context,
                "Driver Online",
                "Waiting for rides",
                true,  // enableLocation
                "notification_driver",  // customLayout
                "txtTitle",            // titleViewId
                "txtSubtitle",         // subtitleViewId
                "txtTimer"             // timerViewId
            );
        } else {
            Log.w(TAG, "Layout notification_driver n'existe pas, utilisation du layout par défaut");
            ThunderBgServiceHelper.startService(context, "Driver Online", "Waiting", true);
        }
    }

    /**
     * Exemple 3: Changer d'état dynamiquement
     */
    public void switchToRideState(String clientName, String destination) {
        ThunderBgServiceHelper.updateNotification(
            context,
            "En course",
            clientName + " → " + destination,
            "notification_riding",  // Nouveau layout
            "txtDriverStatus",      // IDs du nouveau layout
            "txtDestination",
            "txtElapsedTime"
        );
        Log.i(TAG, "État changé vers: ON_RIDE");
    }

    /**
     * Exemple 4: Mettre à jour juste le texte (sans changer le layout)
     */
    public void updateStatus(String status) {
        ThunderBgServiceHelper.updateNotification(
            context,
            null,  // Garder le titre actuel
            status // Mettre à jour le sous-titre
        );
    }

    /**
     * Exemple 5: Changer de layout dynamiquement selon l'état
     */
    public void switchLayout(String state) {
        String layout;
        String titleViewId, subtitleViewId, timerViewId;
        
        switch (state) {
            case "ONLINE":
                layout = "notification_online";
                titleViewId = "txtTitle";
                subtitleViewId = "txtSubtitle";
                timerViewId = "txtTimer";
                break;
            case "RIDING":
                layout = "notification_riding";
                titleViewId = "txtDriverStatus";
                subtitleViewId = "txtDestination";
                timerViewId = "txtElapsedTime";
                break;
            case "WAITING":
                layout = "notification_waiting";
                titleViewId = "txtWaitingTitle";
                subtitleViewId = "txtClientInfo";
                timerViewId = "txtWaitTime";
                break;
            default:
                layout = "notification_default";
                titleViewId = "txtTitle";
                subtitleViewId = "txtSubtitle";
                timerViewId = "txtTimer";
        }
        
        // Vérifier que le layout et les IDs existent
        if (ThunderBgServiceHelper.layoutExists(context, layout) &&
            ThunderBgServiceHelper.viewIdExists(context, titleViewId) &&
            ThunderBgServiceHelper.viewIdExists(context, subtitleViewId) &&
            ThunderBgServiceHelper.viewIdExists(context, timerViewId)) {
            
            ThunderBgServiceHelper.updateNotification(
                context,
                getTitleForState(state),
                getSubtitleForState(state),
                layout,
                titleViewId,
                subtitleViewId,
                timerViewId
            );
        } else {
            Log.w(TAG, "Layout ou IDs manquants pour l'état: " + state);
        }
    }

    /**
     * Exemple 6: Gestionnaire d'état complet
     */
    public class StateManager {
        private String currentState = "OFFLINE";
        
        public void goOnline() {
            currentState = "ONLINE";
            switchLayout(currentState);
        }
        
        public void startRide(String clientName, String destination) {
            currentState = "RIDING";
            ThunderBgServiceHelper.updateNotification(
                context,
                "En course",
                clientName + " → " + destination,
                "notification_riding",
                "txtDriverStatus",
                "txtDestination",
                "txtElapsedTime"
            );
        }
        
        public void stop() {
            ThunderBgServiceHelper.stopService(context);
            currentState = "OFFLINE";
        }
    }

    /**
     * Helpers privés
     */
    private String getTitleForState(String state) {
        switch (state) {
            case "ONLINE": return "Disponible";
            case "RIDING": return "En course";
            case "WAITING": return "En attente";
            default: return "Online";
        }
    }

    private String getSubtitleForState(String state) {
        switch (state) {
            case "ONLINE": return "En attente de courses";
            case "RIDING": return "En route vers la destination";
            case "WAITING": return "En attente du client";
            default: return "Running";
        }
    }

    /**
     * Arrêter le service
     */
    public void stop() {
        ThunderBgServiceHelper.stopService(context);
    }
}

