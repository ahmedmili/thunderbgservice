package com.ahmedmili.thunderbgservice.state;

/**
 * Énumération des états possibles de l'application.
 * Utilisée par le StateManager pour valider les transitions.
 */
public enum AppState {
    /**
     * Service arrêté / Hors ligne
     */
    OFFLINE("offline"),
    
    /**
     * Service démarré, en ligne et disponible
     */
    ONLINE("online"),
    
    /**
     * En cours de course / mission
     */
    ON_RIDE("on_ride"),
    
    /**
     * En attente du client / pickup
     */
    WAITING_PICKUP("waiting_pickup"),
    
    /**
     * En train de conduire / se déplacer
     */
    DRIVING("driving"),
    
    /**
     * Arrivé à destination
     */
    ARRIVED("arrived"),
    
    /**
     * Mission terminée
     */
    COMPLETED("completed"),
    
    /**
     * État personnalisé (pour les apps qui ont besoin de plus d'états)
     */
    CUSTOM("custom");
    
    private final String value;
    
    AppState(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Convertit une chaîne en AppState
     */
    public static AppState fromString(String value) {
        if (value == null) return OFFLINE;
        for (AppState state : AppState.values()) {
            if (state.value.equalsIgnoreCase(value) || state.name().equalsIgnoreCase(value)) {
                return state;
            }
        }
        return CUSTOM;
    }
}

