package com.ahmedmili.thunderbgservice.state;

import android.content.Context;
import android.util.Log;
import com.ahmedmili.thunderbgservice.core.ThunderBgServiceHelper;
import java.util.HashSet;
import java.util.Set;

/**
 * Gestionnaire d'état robuste avec validation des transitions.
 * 
 * Usage:
 * StateManager manager = new StateManager(context);
 * manager.transitionTo(AppState.ONLINE); // Valide et applique la transition
 */
public class StateManager {
    private static final String TAG = "StateManager";
    
    private final Context context;
    private final StateConfiguration config;
    private AppState currentState;
    private StateTransitionListener listener;
    
    /**
     * Interface pour écouter les transitions d'état
     */
    public interface StateTransitionListener {
        /**
         * Appelé avant une transition
         * @return true pour autoriser la transition, false pour la bloquer
         */
        boolean onBeforeTransition(AppState from, AppState to);
        
        /**
         * Appelé après une transition réussie
         */
        void onAfterTransition(AppState from, AppState to);
        
        /**
         * Appelé quand une transition est refusée
         */
        void onTransitionDenied(AppState from, AppState to, String reason);
    }
    
    public StateManager(Context context) {
        this(context, StateConfiguration.getDefault());
    }
    
    public StateManager(Context context, StateConfiguration config) {
        this.context = context.getApplicationContext();
        this.config = config;
        this.currentState = AppState.OFFLINE;
    }
    
    /**
     * Obtient l'état actuel
     */
    public AppState getCurrentState() {
        return currentState;
    }
    
    /**
     * Définit un listener pour les transitions
     */
    public void setListener(StateTransitionListener listener) {
        this.listener = listener;
    }
    
    /**
     * Transitionne vers un nouvel état avec validation
     * @param newState Le nouvel état
     * @return true si la transition a réussi, false sinon
     */
    public boolean transitionTo(AppState newState) {
        return transitionTo(newState, null);
    }
    
    /**
     * Transitionne vers un nouvel état avec validation et données personnalisées
     * @param newState Le nouvel état
     * @param stateData Données personnalisées pour l'état (peut être null)
     * @return true si la transition a réussi, false sinon
     */
    public boolean transitionTo(AppState newState, StateData stateData) {
        if (newState == null) {
            Log.e(TAG, "Cannot transition to null state");
            return false;
        }
        
        // Même état = pas de changement
        if (currentState == newState) {
            Log.d(TAG, "Already in state: " + newState);
            return true;
        }
        
        // Vérifier si la transition est autorisée
        if (!config.isTransitionAllowed(currentState, newState)) {
            String reason = "Transition not allowed: " + currentState + " -> " + newState;
            Log.w(TAG, reason);
            
            if (listener != null) {
                listener.onTransitionDenied(currentState, newState, reason);
            }
            
            return false;
        }
        
        // Appeler le listener avant la transition
        if (listener != null && !listener.onBeforeTransition(currentState, newState)) {
            String reason = "Transition blocked by listener";
            Log.w(TAG, reason);
            if (listener != null) {
                listener.onTransitionDenied(currentState, newState, reason);
            }
            return false;
        }
        
        // Effectuer la transition
        AppState previousState = currentState;
        currentState = newState;
        
        Log.i(TAG, "State transition: " + previousState + " -> " + newState);
        
        // Appeler le listener après la transition
        if (listener != null) {
            listener.onAfterTransition(previousState, newState);
        }
        
        return true;
    }
    
    /**
     * Force une transition sans validation (usage interne uniquement)
     */
    public void forceTransition(AppState newState) {
        AppState previousState = currentState;
        currentState = newState;
        Log.w(TAG, "Force transition: " + previousState + " -> " + newState);
        
        if (listener != null) {
            listener.onAfterTransition(previousState, newState);
        }
    }
    
    /**
     * Obtient tous les états vers lesquels on peut transitionner depuis l'état actuel
     */
    public Set<AppState> getAvailableTransitions() {
        return config.getAllowedTransitions(currentState);
    }
    
    /**
     * Vérifie si une transition vers un état est possible
     */
    public boolean canTransitionTo(AppState state) {
        return config.isTransitionAllowed(currentState, state);
    }
    
    /**
     * Réinitialise l'état à OFFLINE
     */
    public void reset() {
        forceTransition(AppState.OFFLINE);
    }
    
    /**
     * Classe pour stocker des données personnalisées avec un état
     */
    public static class StateData {
        private String layout;
        private String titleViewId;
        private String subtitleViewId;
        private String timerViewId;
        private String title;
        private String subtitle;
        private boolean enableLocation;
        private boolean soundsEnabled;
        
        // Getters et setters
        public String getLayout() { return layout; }
        public void setLayout(String layout) { this.layout = layout; }
        
        public String getTitleViewId() { return titleViewId; }
        public void setTitleViewId(String titleViewId) { this.titleViewId = titleViewId; }
        
        public String getSubtitleViewId() { return subtitleViewId; }
        public void setSubtitleViewId(String subtitleViewId) { this.subtitleViewId = subtitleViewId; }
        
        public String getTimerViewId() { return timerViewId; }
        public void setTimerViewId(String timerViewId) { this.timerViewId = timerViewId; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getSubtitle() { return subtitle; }
        public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
        
        public boolean isEnableLocation() { return enableLocation; }
        public void setEnableLocation(boolean enableLocation) { this.enableLocation = enableLocation; }
        
        public boolean isSoundsEnabled() { return soundsEnabled; }
        public void setSoundsEnabled(boolean soundsEnabled) { this.soundsEnabled = soundsEnabled; }
    }
}

