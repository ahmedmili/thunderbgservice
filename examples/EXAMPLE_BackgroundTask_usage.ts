/**
 * EXEMPLE: Utilisation des tâches en arrière-plan depuis TypeScript/JS
 * 
 * Note: Les tâches doivent être implémentées en Java natif dans votre app.
 * Ce fichier montre comment les enregistrer depuis JS/TS.
 */

import { ThunderBgService } from '@webify/capacitor-thunder-bg-service';

/**
 * Exemple 1: Enregistrer une tâche personnalisée
 * 
 * La classe Java doit exister dans votre app:
 * - Package: com.yourpackage
 * - Classe: MyCustomBackgroundTask
 * - Doit implémenter: com.webify.thunderbgservice.BackgroundTask
 */
export async function registerCustomTask() {
  await ThunderBgService.registerTask({
    taskId: 'myCustomTask',
    taskClass: 'com.yourpackage.MyCustomBackgroundTask', // Nom complet de la classe Java
    intervalMs: 5000, // Exécute toutes les 5 secondes
  });
  
  console.log('Tâche enregistrée: myCustomTask');
}

/**
 * Exemple 2: Enregistrer plusieurs tâches
 */
export async function registerMultipleTasks() {
  // Tâche de vérification réseau (toutes les 10 secondes)
  await ThunderBgService.registerTask({
    taskId: 'networkCheck',
    taskClass: 'com.yourpackage.NetworkCheckTask',
    intervalMs: 10000,
  });
  
  // Tâche de mise à jour de localisation (toutes les 30 secondes)
  await ThunderBgService.registerTask({
    taskId: 'locationUpdate',
    taskClass: 'com.yourpackage.LocationUpdateTask',
    intervalMs: 30000,
  });
  
  // Tâche de synchronisation (toutes les 60 secondes)
  await ThunderBgService.registerTask({
    taskId: 'dataSync',
    taskClass: 'com.yourpackage.DataSyncTask',
    intervalMs: 60000,
  });
}

/**
 * Exemple 3: Désenregistrer une tâche
 */
export async function unregisterTask() {
  await ThunderBgService.unregisterTask('myCustomTask');
  console.log('Tâche désenregistrée: myCustomTask');
}

/**
 * Exemple 4: Gestionnaire complet de tâches
 */
export class BackgroundTaskManager {
  private registeredTasks: Set<string> = new Set();
  
  /**
   * Enregistre une tâche et la garde en mémoire
   */
  async registerTask(taskId: string, taskClass: string, intervalMs: number) {
    await ThunderBgService.registerTask({
      taskId,
      taskClass,
      intervalMs,
    });
    this.registeredTasks.add(taskId);
    console.log(`Tâche enregistrée: ${taskId}`);
  }
  
  /**
   * Désenregistre une tâche
   */
  async unregisterTask(taskId: string) {
    await ThunderBgService.unregisterTask(taskId);
    this.registeredTasks.delete(taskId);
    console.log(`Tâche désenregistrée: ${taskId}`);
  }
  
  /**
   * Désenregistre toutes les tâches
   */
  async unregisterAll() {
    for (const taskId of this.registeredTasks) {
      await ThunderBgService.unregisterTask(taskId);
    }
    this.registeredTasks.clear();
    console.log('Toutes les tâches désenregistrées');
  }
  
  /**
   * Liste les tâches enregistrées
   */
  getRegisteredTasks(): string[] {
    return Array.from(this.registeredTasks);
  }
}

/**
 * Exemple 5: Utilisation dans un service Angular
 */
export class MyService {
  private taskManager = new BackgroundTaskManager();
  
  async initialize() {
    // Démarrer le service de notification
    await ThunderBgService.start({
      notificationTitle: 'Online',
      notificationSubtitle: 'Service actif',
      enableLocation: true,
    });
    
    // Enregistrer les tâches en arrière-plan
    await this.taskManager.registerTask(
      'heartbeat',
      'com.yourpackage.HeartbeatTask',
      5000
    );
    
    await this.taskManager.registerTask(
      'sync',
      'com.yourpackage.SyncTask',
      60000
    );
  }
  
  async onDestroy() {
    // Arrêter toutes les tâches
    await this.taskManager.unregisterAll();
    
    // Arrêter le service
    await ThunderBgService.stop();
  }
}

/**
 * Exemple 6: Enregistrer une tâche conditionnellement
 */
export async function registerTaskIfNeeded(condition: boolean) {
  if (condition) {
    await ThunderBgService.registerTask({
      taskId: 'conditionalTask',
      taskClass: 'com.yourpackage.ConditionalTask',
      intervalMs: 10000,
    });
  }
}

