package com.ahmedmili.thunderbgservice.state;

import android.util.Log;
import java.util.*;

/**
 * Configuration de la machine à états.
 * Définit les transitions autorisées entre les états.
 */
public class StateConfiguration {
    private static final String TAG = "StateConfiguration";
    
    // Map des transitions autorisées: fromState -> Set<toStates>
    private final Map<AppState, Set<AppState>> allowedTransitions;
    
    // États initiaux valides (depuis OFFLINE)
    private final Set<AppState> initialStates;
    
    // États finaux (depuis lesquels on peut seulement aller vers OFFLINE)
    private final Set<AppState> finalStates;
    
    public StateConfiguration() {
        this.allowedTransitions = new HashMap<>();
        this.initialStates = new HashSet<>();
        this.finalStates = new HashSet<>();
        initializeDefaultConfiguration();
    }
    
    /**
     * Initialise la configuration par défaut avec des transitions logiques
     */
    private void initializeDefaultConfiguration() {
        // États initiaux: on peut démarrer depuis OFFLINE vers ces états
        initialStates.add(AppState.ONLINE);
        
        // États finaux: depuis ces états, on peut seulement aller vers COMPLETED ou OFFLINE
        finalStates.add(AppState.COMPLETED);
        finalStates.add(AppState.ARRIVED);
        
        // OFFLINE -> ONLINE (démarrage)
        addTransition(AppState.OFFLINE, AppState.ONLINE);
        
        // ONLINE -> ON_RIDE (début de mission)
        addTransition(AppState.ONLINE, AppState.ON_RIDE);
        addTransition(AppState.ONLINE, AppState.OFFLINE); // Déconnexion
        
        // ON_RIDE -> WAITING_PICKUP (arrivée au point de collecte)
        addTransition(AppState.ON_RIDE, AppState.WAITING_PICKUP);
        addTransition(AppState.ON_RIDE, AppState.DRIVING);
        addTransition(AppState.ON_RIDE, AppState.OFFLINE); // Annulation
        
        // WAITING_PICKUP -> DRIVING (client pris en charge)
        addTransition(AppState.WAITING_PICKUP, AppState.DRIVING);
        addTransition(AppState.WAITING_PICKUP, AppState.ON_RIDE); // Retour en arrière
        addTransition(AppState.WAITING_PICKUP, AppState.OFFLINE); // Annulation
        
        // DRIVING -> ARRIVED (arrivée à destination)
        addTransition(AppState.DRIVING, AppState.ARRIVED);
        addTransition(AppState.DRIVING, AppState.ON_RIDE); // Retour en arrière
        
        // ARRIVED -> COMPLETED (mission terminée)
        addTransition(AppState.ARRIVED, AppState.COMPLETED);
        addTransition(AppState.ARRIVED, AppState.ONLINE); // Nouvelle mission
        
        // COMPLETED -> ONLINE (nouvelle mission disponible)
        addTransition(AppState.COMPLETED, AppState.ONLINE);
        addTransition(AppState.COMPLETED, AppState.OFFLINE); // Arrêt
        
        // Tous les états peuvent retourner à OFFLINE (arrêt d'urgence)
        for (AppState state : AppState.values()) {
            if (state != AppState.OFFLINE) {
                addTransition(state, AppState.OFFLINE);
            }
        }
    }
    
    /**
     * Ajoute une transition autorisée
     */
    public void addTransition(AppState from, AppState to) {
        allowedTransitions.computeIfAbsent(from, k -> new HashSet<>()).add(to);
        Log.d(TAG, "Transition added: " + from + " -> " + to);
    }
    
    /**
     * Supprime une transition
     */
    public void removeTransition(AppState from, AppState to) {
        Set<AppState> toStates = allowedTransitions.get(from);
        if (toStates != null) {
            toStates.remove(to);
        }
    }
    
    /**
     * Vérifie si une transition est autorisée
     */
    public boolean isTransitionAllowed(AppState from, AppState to) {
        // Transition vers le même état (pas de changement)
        if (from == to) {
            return true;
        }
        
        // Vérifier si la transition est dans la liste autorisée
        Set<AppState> allowed = allowedTransitions.get(from);
        if (allowed == null) {
            return false;
        }
        
        return allowed.contains(to);
    }
    
    /**
     * Obtient tous les états vers lesquels on peut transitionner depuis un état donné
     */
    public Set<AppState> getAllowedTransitions(AppState from) {
        Set<AppState> allowed = allowedTransitions.get(from);
        return allowed != null ? new HashSet<>(allowed) : new HashSet<>();
    }
    
    /**
     * Vérifie si un état est un état initial valide
     */
    public boolean isInitialState(AppState state) {
        return initialStates.contains(state);
    }
    
    /**
     * Vérifie si un état est un état final
     */
    public boolean isFinalState(AppState state) {
        return finalStates.contains(state);
    }
    
    /**
     * Obtient la configuration par défaut (singleton)
     */
    public static StateConfiguration getDefault() {
        return new StateConfiguration();
    }
    
    /**
     * Crée une configuration personnalisée vide
     */
    public static StateConfiguration createCustom() {
        StateConfiguration config = new StateConfiguration();
        // Vider toutes les transitions
        config.allowedTransitions.clear();
        config.initialStates.clear();
        config.finalStates.clear();
        return config;
    }
}

