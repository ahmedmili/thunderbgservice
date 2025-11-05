package com.ahmedmili.thunderbgservice.state;

import android.content.Context;
import android.util.Log;
import com.ahmedmili.thunderbgservice.core.ThunderBgServiceHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper pour intégrer StateManager avec ThunderBgService.
 * Applique automatiquement les changements de notification lors des transitions d'état.
 */
public class ThunderBgStateHelper {
    private static final String TAG = "ThunderBgStateHelper";
    
    private final Context context;
    private final StateManager stateManager;
    private final Map<AppState, StateConfig> stateConfigs;
    
    /**
     * Configuration pour un état spécifique
     */
    public static class StateConfig {
        private String layout;
        private String titleViewId;
        private String subtitleViewId;
        private String timerViewId;
        private String title;
        private String subtitle;
        private boolean enableLocation;
        private boolean soundsEnabled;
        private Map<String, String> viewData;
        private JSONArray buttons;
        
        // Getters et setters
        public String getLayout() { return layout; }
        public StateConfig setLayout(String layout) { this.layout = layout; return this; }
        
        public String getTitleViewId() { return titleViewId; }
        public StateConfig setTitleViewId(String titleViewId) { this.titleViewId = titleViewId; return this; }
        
        public String getSubtitleViewId() { return subtitleViewId; }
        public StateConfig setSubtitleViewId(String subtitleViewId) { this.subtitleViewId = subtitleViewId; return this; }
        
        public String getTimerViewId() { return timerViewId; }
        public StateConfig setTimerViewId(String timerViewId) { this.timerViewId = timerViewId; return this; }
        
        public String getTitle() { return title; }
        public StateConfig setTitle(String title) { this.title = title; return this; }
        
        public String getSubtitle() { return subtitle; }
        public StateConfig setSubtitle(String subtitle) { this.subtitle = subtitle; return this; }
        
        public boolean isEnableLocation() { return enableLocation; }
        public StateConfig setEnableLocation(boolean enableLocation) { this.enableLocation = enableLocation; return this; }
        
        public boolean isSoundsEnabled() { return soundsEnabled; }
        public StateConfig setSoundsEnabled(boolean soundsEnabled) { this.soundsEnabled = soundsEnabled; return this; }
        
        public Map<String, String> getViewData() { return viewData; }
        public StateConfig setViewData(Map<String, String> viewData) { this.viewData = viewData; return this; }
        
        public JSONArray getButtons() { return buttons; }
        public StateConfig setButtons(JSONArray buttons) { this.buttons = buttons; return this; }
    }
    
    public ThunderBgStateHelper(Context context) {
        this(context, StateConfiguration.getDefault());
    }
    
    public ThunderBgStateHelper(Context context, StateConfiguration config) {
        this.context = context.getApplicationContext();
        this.stateManager = new StateManager(context, config);
        this.stateConfigs = new HashMap<>();
        
        // Configurer le listener pour appliquer automatiquement les changements
        stateManager.setListener(new StateManager.StateTransitionListener() {
            @Override
            public boolean onBeforeTransition(AppState from, AppState to) {
                Log.d(TAG, "Before transition: " + from + " -> " + to);
                return true;
            }
            
            @Override
            public void onAfterTransition(AppState from, AppState to) {
                Log.i(TAG, "After transition: " + from + " -> " + to);
                applyStateConfig(to);
            }
            
            @Override
            public void onTransitionDenied(AppState from, AppState to, String reason) {
                Log.w(TAG, "Transition denied: " + from + " -> " + to + " (" + reason + ")");
            }
        });
        
        // Initialiser les configurations par défaut
        initializeDefaultConfigs();
    }
    
    /**
     * Initialise les configurations par défaut pour chaque état
     */
    private void initializeDefaultConfigs() {
        // État ONLINE
        StateConfig onlineConfig = new StateConfig()
            .setLayout("notification_online")
            .setTitleViewId("txtDriverStatus")
            .setSubtitleViewId("txtWaiting")
            .setTimerViewId("txtTimer")
            .setTitle("Disponible")
            .setSubtitle("En attente de courses")
            .setEnableLocation(true)
            .setSoundsEnabled(false);
        stateConfigs.put(AppState.ONLINE, onlineConfig);
        
        // État ON_RIDE
        StateConfig onRideConfig = new StateConfig()
            .setLayout("notification_riding")
            .setTitleViewId("txtDriverStatus")
            .setSubtitleViewId("txtDestination")
            .setTimerViewId("txtElapsedTime")
            .setTitle("En course")
            .setSubtitle("En route vers la destination")
            .setEnableLocation(true)
            .setSoundsEnabled(false);
        stateConfigs.put(AppState.ON_RIDE, onRideConfig);
        
        // État WAITING_PICKUP
        StateConfig waitingConfig = new StateConfig()
            .setLayout("notification_waiting")
            .setTitleViewId("txtWaitingTitle")
            .setSubtitleViewId("txtClientInfo")
            .setTimerViewId("txtWaitTime")
            .setTitle("En attente")
            .setSubtitle("En attente du client")
            .setEnableLocation(true)
            .setSoundsEnabled(true);
        stateConfigs.put(AppState.WAITING_PICKUP, waitingConfig);
        
        // État DRIVING
        StateConfig drivingConfig = new StateConfig()
            .setLayout("notification_riding")
            .setTitleViewId("txtDriverStatus")
            .setSubtitleViewId("txtDestination")
            .setTimerViewId("txtElapsedTime")
            .setTitle("Conduite")
            .setSubtitle("En conduite")
            .setEnableLocation(true)
            .setSoundsEnabled(false);
        stateConfigs.put(AppState.DRIVING, drivingConfig);
        
        // État ARRIVED
        StateConfig arrivedConfig = new StateConfig()
            .setLayout("notification_arrived")
            .setTitleViewId("txtArrivalTitle")
            .setSubtitleViewId("txtArrivalInfo")
            .setTimerViewId("txtArrivalTime")
            .setTitle("Arrivé")
            .setSubtitle("Client pris en charge")
            .setEnableLocation(true)
            .setSoundsEnabled(true);
        stateConfigs.put(AppState.ARRIVED, arrivedConfig);
        
        // État COMPLETED
        StateConfig completedConfig = new StateConfig()
            .setLayout("notification_default")
            .setTitleViewId("txtTitle")
            .setSubtitleViewId("txtSubtitle")
            .setTimerViewId("txtTimer")
            .setTitle("Course terminée")
            .setSubtitle("Mission terminée avec succès")
            .setEnableLocation(false)
            .setSoundsEnabled(true);
        stateConfigs.put(AppState.COMPLETED, completedConfig);
    }
    
    /**
     * Configure un état personnalisé
     */
    public void configureState(AppState state, StateConfig config) {
        stateConfigs.put(state, config);
        Log.i(TAG, "State configured: " + state);
    }
    
    /**
     * Obtient la configuration d'un état
     */
    public StateConfig getStateConfig(AppState state) {
        return stateConfigs.get(state);
    }
    
    /**
     * Applique la configuration d'un état au service
     */
    private void applyStateConfig(AppState state) {
        StateConfig config = stateConfigs.get(state);
        if (config == null) {
            Log.w(TAG, "No configuration found for state: " + state);
            return;
        }
        
        // Vérifier que le layout existe
        if (config.getLayout() != null && !ThunderBgServiceHelper.layoutExists(context, config.getLayout())) {
            Log.e(TAG, "Layout does not exist: " + config.getLayout() + " for state: " + state);
            return;
        }
        
        // Si le service n'est pas démarré et qu'on passe à ONLINE, démarrer le service
        if (state == AppState.ONLINE && !isServiceRunning()) {
            ThunderBgServiceHelper.startService(
                context,
                config.getTitle(),
                config.getSubtitle(),
                config.isEnableLocation(),
                config.isSoundsEnabled(),
                config.getLayout(),
                config.getTitleViewId(),
                config.getSubtitleViewId(),
                config.getTimerViewId()
            );
        } else if (state == AppState.OFFLINE) {
            // Arrêter le service
            ThunderBgServiceHelper.stopService(context);
        } else {
            // Mettre à jour la notification
            ThunderBgServiceHelper.updateNotification(
                context,
                config.getTitle(),
                config.getSubtitle(),
                config.getLayout(),
                config.getTitleViewId(),
                config.getSubtitleViewId(),
                config.getTimerViewId()
            );
        }
        
        Log.i(TAG, "State config applied: " + state);
    }
    
    /**
     * Vérifie si le service est en cours d'exécution
     */
    private boolean isServiceRunning() {
        // TODO: Implémenter une vérification réelle du service
        // Pour l'instant, on suppose qu'il est démarré si on n'est pas en OFFLINE
        return stateManager.getCurrentState() != AppState.OFFLINE;
    }
    
    /**
     * Transitionne vers un nouvel état
     */
    public boolean transitionTo(AppState state) {
        return stateManager.transitionTo(state);
    }
    
    /**
     * Obtient l'état actuel
     */
    public AppState getCurrentState() {
        return stateManager.getCurrentState();
    }
    
    /**
     * Obtient le StateManager sous-jacent
     */
    public StateManager getStateManager() {
        return stateManager;
    }
    
    /**
     * Obtient les transitions disponibles depuis l'état actuel
     */
    public java.util.Set<AppState> getAvailableTransitions() {
        return stateManager.getAvailableTransitions();
    }
}

