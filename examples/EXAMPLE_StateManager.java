package com.yourpackage;

import android.content.Context;
import android.util.Log;
import com.ahmedmili.thunderbgservice.state.*;
import com.ahmedmili.thunderbgservice.state.ThunderBgStateHelper.StateConfig;

/**
 * EXEMPLE: Utilisation du StateManager avec validation des transitions
 * 
 * Le StateManager permet de gérer les états de l'application avec validation
 * automatique des transitions. Cela évite les erreurs de logique métier.
 */
public class StateManagerExample {
    private static final String TAG = "StateManagerExample";
    private ThunderBgStateHelper stateHelper;
    
    public StateManagerExample(Context context) {
        // Créer le helper avec configuration par défaut
        stateHelper = new ThunderBgStateHelper(context);
        
        // Ou avec configuration personnalisée
        // StateConfiguration customConfig = StateConfiguration.createCustom();
        // customConfig.addTransition(AppState.OFFLINE, AppState.ONLINE);
        // customConfig.addTransition(AppState.ONLINE, AppState.ON_RIDE);
        // stateHelper = new ThunderBgStateHelper(context, customConfig);
    }
    
    /**
     * Exemple 1: Transition simple avec validation
     */
    public void goOnline() {
        boolean success = stateHelper.transitionTo(AppState.ONLINE);
        if (success) {
            Log.i(TAG, "Transitioned to ONLINE");
        } else {
            Log.e(TAG, "Failed to transition to ONLINE");
        }
    }
    
    /**
     * Exemple 2: Transition avec données personnalisées
     */
    public void startRide(String clientName, String destination) {
        // Vérifier d'abord si la transition est possible
        if (stateHelper.getAvailableTransitions().contains(AppState.ON_RIDE)) {
            // Configurer l'état avec des données personnalisées
            StateConfig config = new StateConfig()
                .setLayout("notification_riding")
                .setTitleViewId("txtDriverStatus")
                .setSubtitleViewId("txtDestination")
                .setTitle("En course")
                .setSubtitle(clientName + " → " + destination);
            
            stateHelper.configureState(AppState.ON_RIDE, config);
            
            // Effectuer la transition
            stateHelper.transitionTo(AppState.ON_RIDE);
        } else {
            Log.w(TAG, "Cannot transition to ON_RIDE from current state");
        }
    }
    
    /**
     * Exemple 3: Vérifier les transitions disponibles
     */
    public void showAvailableTransitions() {
        java.util.Set<AppState> available = stateHelper.getAvailableTransitions();
        Log.i(TAG, "Available transitions from " + stateHelper.getCurrentState() + ":");
        for (AppState state : available) {
            Log.i(TAG, "  - " + state);
        }
    }
    
    /**
     * Exemple 4: Gestionnaire d'état complet avec listener
     */
    public class RideStateManager {
        private ThunderBgStateHelper helper;
        
        public RideStateManager(Context context) {
            helper = new ThunderBgStateHelper(context);
            
            // Configurer un listener personnalisé
            helper.getStateManager().setListener(new StateManager.StateTransitionListener() {
                @Override
                public boolean onBeforeTransition(AppState from, AppState to) {
                    // Vérifier des conditions métier avant la transition
                    if (to == AppState.ONLINE && !hasActiveRide()) {
                        Log.w(TAG, "Cannot go online: active ride exists");
                        return false; // Bloquer la transition
                    }
                    return true;
                }
                
                @Override
                public void onAfterTransition(AppState from, AppState to) {
                    // Actions après la transition
                    Log.i(TAG, "Transition completed: " + from + " -> " + to);
                    
                    if (to == AppState.COMPLETED) {
                        // Sauvegarder les données de la course
                        saveRideData();
                    }
                }
                
                @Override
                public void onTransitionDenied(AppState from, AppState to, String reason) {
                    // Gérer les transitions refusées
                    Log.e(TAG, "Transition denied: " + reason);
                    showErrorToUser("Cannot change state: " + reason);
                }
            });
        }
        
        public void goOnline() {
            helper.transitionTo(AppState.ONLINE);
        }
        
        public void startRide() {
            helper.transitionTo(AppState.ON_RIDE);
        }
        
        public void waitForPickup() {
            helper.transitionTo(AppState.WAITING_PICKUP);
        }
        
        public void startDriving() {
            helper.transitionTo(AppState.DRIVING);
        }
        
        public void arrive() {
            helper.transitionTo(AppState.ARRIVED);
        }
        
        public void completeRide() {
            helper.transitionTo(AppState.COMPLETED);
        }
        
        public void goOffline() {
            helper.transitionTo(AppState.OFFLINE);
        }
        
        // Helpers privés
        private boolean hasActiveRide() {
            // Votre logique métier
            return false;
        }
        
        private void saveRideData() {
            // Sauvegarder les données
        }
        
        private void showErrorToUser(String message) {
            // Afficher l'erreur à l'utilisateur
        }
    }
    
    /**
     * Exemple 5: Configuration personnalisée d'états
     */
    public void configureCustomStates() {
        // Configuration pour un état ONLINE personnalisé
        StateConfig onlineConfig = new StateConfig()
            .setLayout("notification_online_custom")
            .setTitleViewId("txtCustomTitle")
            .setSubtitleViewId("txtCustomSubtitle")
            .setTitle("Mon Titre Personnalisé")
            .setSubtitle("Mon sous-titre")
            .setEnableLocation(true)
            .setSoundsEnabled(false);
        
        stateHelper.configureState(AppState.ONLINE, onlineConfig);
    }
    
    /**
     * Exemple 6: Machine à états avec validation stricte
     */
    public void createStrictStateMachine(Context context) {
        // Créer une configuration personnalisée avec règles strictes
        StateConfiguration strictConfig = StateConfiguration.createCustom();
        
        // Seulement certaines transitions autorisées
        strictConfig.addTransition(AppState.OFFLINE, AppState.ONLINE);
        strictConfig.addTransition(AppState.ONLINE, AppState.ON_RIDE);
        strictConfig.addTransition(AppState.ON_RIDE, AppState.COMPLETED);
        strictConfig.addTransition(AppState.COMPLETED, AppState.OFFLINE);
        // Toute autre transition sera refusée
        
        ThunderBgStateHelper strictHelper = new ThunderBgStateHelper(context, strictConfig);
        
        // Essayer une transition non autorisée
        boolean success = strictHelper.transitionTo(AppState.ONLINE);
        if (!success) {
            Log.w(TAG, "Transition refused by strict configuration");
        }
    }
}

