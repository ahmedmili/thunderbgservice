package com.yourpackage; // Changez selon votre package

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * Template de classe pour dynamiser la notification du plugin ThunderBgService.
 * Cette classe peut être utilisée dans votre app hôte pour construire dynamiquement
 * les paramètres de notification selon l'état de l'app.
 * 
 * Usage dans votre code JS/TS:
 * import { ThunderBgService } from '@webify/capacitor-thunder-bg-service';
 * 
 * // Exemple: état "Online"
 * await ThunderBgService.start({
 *   notificationTitle: NotificationDynamicHelper.getTitleForState("ONLINE"),
 *   notificationSubtitle: NotificationDynamicHelper.getSubtitleForState("ONLINE"),
 *   customLayout: NotificationDynamicHelper.getLayoutForState("ONLINE"),
 *   titleViewId: "txtTitle",
 *   subtitleViewId: "txtSubtitle",
 *   timerViewId: "txtTimer",
 *   enableLocation: true
 * });
 */
public class NotificationDynamicHelper {
    
    private static final String TAG = "NotificationDynamicHelper";
    
    // États possibles de l'application
    public enum AppState {
        ONLINE,           // En ligne, disponible
        OFFLINE,          // Hors ligne
        ON_RIDE,          // En cours de course
        WAITING_PICKUP,   // En attente du client
        DRIVING,          // En train de conduire
        ARRIVED,          // Arrivé à destination
        COMPLETED         // Course terminée
    }
    
    /**
     * Retourne le titre de notification selon l'état
     */
    public static String getTitleForState(AppState state) {
        switch (state) {
            case ONLINE:
                return "Disponible";
            case OFFLINE:
                return "Hors ligne";
            case ON_RIDE:
                return "En course";
            case WAITING_PICKUP:
                return "En attente";
            case DRIVING:
                return "Conduite";
            case ARRIVED:
                return "Arrivé";
            case COMPLETED:
                return "Course terminée";
            default:
                return "Online";
        }
    }
    
    /**
     * Retourne le sous-titre selon l'état
     */
    public static String getSubtitleForState(AppState state) {
        switch (state) {
            case ONLINE:
                return "En attente de courses";
            case OFFLINE:
                return "Service arrêté";
            case ON_RIDE:
                return "En route vers la destination";
            case WAITING_PICKUP:
                return "En attente du client";
            case DRIVING:
                return "En conduite";
            case ARRIVED:
                return "Client pris en charge";
            case COMPLETED:
                return "Course terminée avec succès";
            default:
                return "Running";
        }
    }
    
    /**
     * Retourne le nom du layout à utiliser selon l'état
     * Assurez-vous que ces layouts existent dans res/layout/
     */
    public static String getLayoutForState(AppState state) {
        switch (state) {
            case ONLINE:
                return "notification_online";      // res/layout/notification_online.xml
            case ON_RIDE:
            case DRIVING:
                return "notification_riding";     // res/layout/notification_riding.xml
            case WAITING_PICKUP:
                return "notification_waiting";    // res/layout/notification_waiting.xml
            case ARRIVED:
                return "notification_arrived";    // res/layout/notification_arrived.xml
            default:
                return "notification_default";     // res/layout/notification_default.xml
        }
    }
    
    /**
     * Retourne les IDs de vues selon le layout utilisé
     */
    public static NotificationViewIds getViewIdsForLayout(String layoutName) {
        NotificationViewIds ids = new NotificationViewIds();
        
        // Par défaut, utilisez ces IDs (ajustez selon vos layouts)
        if (layoutName.contains("online") || layoutName.contains("default")) {
            ids.titleId = "txtTitle";
            ids.subtitleId = "txtSubtitle";
            ids.timerId = "txtTimer";
            ids.statusId = "txtStatus";  // Optionnel
        } else if (layoutName.contains("riding") || layoutName.contains("driving")) {
            ids.titleId = "txtDriverStatus";
            ids.subtitleId = "txtDestination";
            ids.timerId = "txtElapsedTime";
            ids.distanceId = "txtDistance";  // Optionnel
        } else if (layoutName.contains("waiting")) {
            ids.titleId = "txtWaitingTitle";
            ids.subtitleId = "txtClientInfo";
            ids.timerId = "txtWaitTime";
        } else if (layoutName.contains("arrived")) {
            ids.titleId = "txtArrivalTitle";
            ids.subtitleId = "txtArrivalInfo";
            ids.timerId = "txtArrivalTime";
        }
        
        return ids;
    }
    
    /**
     * Classe helper pour stocker les IDs de vues
     */
    public static class NotificationViewIds {
        public String titleId = "txtTitle";
        public String subtitleId = "txtSubtitle";
        public String timerId = "txtTimer";
        public String statusId = null;      // Optionnel
        public String distanceId = null;    // Optionnel
        public String iconId = null;         // Optionnel
    }
    
    /**
     * Vérifie si un layout existe dans les ressources
     */
    public static boolean layoutExists(Context context, String layoutName) {
        try {
            Resources res = context.getResources();
            int layoutId = res.getIdentifier(layoutName, "layout", context.getPackageName());
            return layoutId != 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking layout: " + layoutName, e);
            return false;
        }
    }
    
    /**
     * Construit dynamiquement les options pour ThunderBgService.start()
     * Retourne un objet JSON-like (à convertir en JSObject côté JS)
     */
    public static NotificationOptions buildOptionsForState(AppState state, boolean enableLocation) {
        String layout = getLayoutForState(state);
        NotificationViewIds ids = getViewIdsForLayout(layout);
        
        NotificationOptions options = new NotificationOptions();
        options.notificationTitle = getTitleForState(state);
        options.notificationSubtitle = getSubtitleForState(state);
        options.customLayout = layout;
        options.titleViewId = ids.titleId;
        options.subtitleViewId = ids.subtitleId;
        options.timerViewId = ids.timerId;
        options.enableLocation = enableLocation;
        options.soundsEnabled = (state == AppState.ON_RIDE || state == AppState.ARRIVED);
        
        return options;
    }
    
    /**
     * Classe pour stocker les options de notification
     */
    public static class NotificationOptions {
        public String notificationTitle;
        public String notificationSubtitle;
        public String customLayout;
        public String titleViewId;
        public String subtitleViewId;
        public String timerViewId;
        public boolean enableLocation = true;
        public boolean soundsEnabled = false;
        
        // Convertir en objet JS (exemple d'utilisation)
        public String toJson() {
            return String.format(
                "{\"notificationTitle\":\"%s\",\"notificationSubtitle\":\"%s\"," +
                "\"customLayout\":\"%s\",\"titleViewId\":\"%s\"," +
                "\"subtitleViewId\":\"%s\",\"timerViewId\":\"%s\"," +
                "\"enableLocation\":%s,\"soundsEnabled\":%s}",
                notificationTitle, notificationSubtitle,
                customLayout, titleViewId, subtitleViewId, timerViewId,
                enableLocation, soundsEnabled
            );
        }
    }
    
    /**
     * Exemple d'utilisation depuis une classe Java native
     * (si vous voulez appeler le plugin depuis du code Java natif)
     */
    public static void updateNotificationForState(Context context, AppState newState) {
        NotificationOptions opts = buildOptionsForState(newState, true);
        
        // Log pour debug
        Log.i(TAG, "Updating notification for state: " + newState);
        Log.i(TAG, "Options: " + opts.toJson());
        
        // Note: Vous devrez appeler le plugin via Capacitor Bridge depuis JS/TS
        // Cette méthode sert juste à construire les options dynamiquement
    }
}

