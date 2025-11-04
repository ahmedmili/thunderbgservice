package com.yourpackage;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import com.webify.thunderbgservice.core.ThunderBgServiceHelper;

/**
 * EXEMPLE: Utilisation dans une Activity Android native
 * 
 * Montre comment utiliser ThunderBgServiceHelper depuis une Activity
 * pour contrôler le service de notification en arrière-plan.
 */
public class MainActivity extends Activity {
    private MyNativeServiceManager serviceManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialiser le gestionnaire
        serviceManager = new MyNativeServiceManager(this);
        
        // Exemple: démarrer le service au démarrage de l'app
        // serviceManager.startOnline();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Mettre à jour la notification quand l'app revient au premier plan
        // ThunderBgServiceHelper.updateNotification(this, "App active", "En premier plan");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Optionnel: mettre à jour la notification quand l'app va en arrière-plan
        // ThunderBgServiceHelper.updateNotification(this, "App en arrière-plan", "Service actif");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Optionnel: arrêter le service quand l'app est fermée
        // serviceManager.stop();
    }
    
    /**
     * Exemple: Bouton pour démarrer le service
     */
    private void setupStartButton(Button startButton) {
        startButton.setOnClickListener(v -> {
            serviceManager.startOnline();
        });
    }
    
    /**
     * Exemple: Bouton pour changer d'état
     */
    private void setupSwitchStateButton(Button switchButton) {
        switchButton.setOnClickListener(v -> {
            serviceManager.switchToRideState("Client XYZ", "Destination ABC");
        });
    }
    
    /**
     * Exemple: Bouton pour arrêter le service
     */
    private void setupStopButton(Button stopButton) {
        stopButton.setOnClickListener(v -> {
            serviceManager.stop();
        });
    }
}

