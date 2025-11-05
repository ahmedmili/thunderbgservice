/**
 * EXEMPLE: Communication entre tâches Java et JS/TS
 * 
 * Ce fichier montre comment:
 * 1. Écouter les événements émis par les tâches Java (si l'app est active)
 * 2. Récupérer les résultats stockés par les tâches Java
 */

import { ThunderBgService } from '@ahmed-mili/capacitor-thunder-bg-service';

/**
 * Exemple 1: Écouter les événements des tâches
 * 
 * Les événements sont émis uniquement si l'app est active.
 * Si l'app est fermée, les données sont stockées et peuvent être récupérées plus tard.
 */
export function setupTaskEventListener() {
  ThunderBgService.addListener('taskEvent', (data) => {
    console.log('Événement reçu de la tâche:', data.taskId);
    console.log('Données:', data.data);
    console.log('Timestamp:', data.timestamp);
    
    // Traiter les données selon votre logique
    handleTaskEvent(data.taskId, data.data);
  });
}

function handleTaskEvent(taskId: string, data: any) {
  switch (taskId) {
    case 'myTask':
      // Traiter les données de myTask
      console.log('Données de myTask:', data);
      break;
    default:
      console.log('Événement inconnu:', taskId);
  }
}

/**
 * Exemple 2: Récupérer les résultats stockés par une tâche
 * 
 * Cette méthode fonctionne même si l'app était fermée quand la tâche s'est exécutée.
 * Les données sont stockées dans SharedPreferences et peuvent être récupérées plus tard.
 */
export async function getTaskResults() {
  try {
    const { result } = await ThunderBgService.getTaskResult('myTask');
    
    if (result) {
      console.log('Résultats de la tâche:', result);
      
      // Accéder aux données spécifiques
      const counter = result.counter;
      const status = result.status;
      const lastUpdate = result.timestamp;
      
      console.log(`Compteur: ${counter}, Statut: ${status}, Dernière mise à jour: ${lastUpdate}`);
      
      return result;
    } else {
      console.log('Aucun résultat disponible pour cette tâche');
      return null;
    }
  } catch (error) {
    console.error('Erreur lors de la récupération des résultats:', error);
    return null;
  }
}

/**
 * Exemple 3: Polling périodique des résultats
 * 
 * Si vous voulez récupérer les résultats périodiquement,
 * même si l'app était fermée quand les tâches s'exécutaient.
 */
export class TaskResultPoller {
  private intervalId: any = null;
  private taskId: string;
  
  constructor(taskId: string) {
    this.taskId = taskId;
  }
  
  start(intervalMs: number = 5000) {
    this.stop(); // Arrêter si déjà en cours
    
    this.intervalId = setInterval(async () => {
      const result = await ThunderBgService.getTaskResult(this.taskId);
      if (result) {
        this.onResult(result);
      }
    }, intervalMs);
  }
  
  stop() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
      this.intervalId = null;
    }
  }
  
  private onResult(result: any) {
    console.log('Résultat récupéré:', result);
    // Traiter le résultat selon votre logique
  }
}

/**
 * Exemple 4: Gestionnaire complet avec événements et polling
 */
export class TaskCommunicationManager {
  private pollers: Map<string, TaskResultPoller> = new Map();
  
  /**
   * Enregistre une tâche et configure la communication
   */
  async registerTaskWithCommunication(
    taskId: string,
    taskClass: string,
    intervalMs: number,
    enablePolling: boolean = true
  ) {
    // Enregistrer la tâche
    await ThunderBgService.registerTask({
      taskId,
      taskClass,
      intervalMs,
    });
    
    // Configurer le polling si demandé
    if (enablePolling) {
      const poller = new TaskResultPoller(taskId);
      poller.start(intervalMs * 2); // Polling toutes les 2x l'intervalle de la tâche
      this.pollers.set(taskId, poller);
    }
    
    console.log(`Tâche ${taskId} enregistrée avec communication`);
  }
  
  /**
   * Récupère les résultats d'une tâche
   */
  async getTaskResults(taskId: string) {
    return await ThunderBgService.getTaskResult(taskId);
  }
  
  /**
   * Arrête une tâche et son polling
   */
  async unregisterTask(taskId: string) {
    // Arrêter le polling
    const poller = this.pollers.get(taskId);
    if (poller) {
      poller.stop();
      this.pollers.delete(taskId);
    }
    
    // Désenregistrer la tâche
    await ThunderBgService.unregisterTask(taskId);
  }
  
  /**
   * Arrête toutes les tâches
   */
  async stopAll() {
    for (const [taskId, poller] of this.pollers) {
      poller.stop();
    }
    this.pollers.clear();
  }
}

/**
 * Exemple 5: Utilisation dans un service Angular
 */
export class MyService {
  private communicationManager = new TaskCommunicationManager();
  
  async initialize() {
    // Démarrer le service de notification (REQUIS: customLayout)
    await ThunderBgService.start({
      customLayout: 'notification_online',
      titleViewId: 'txtDriverStatus',
      subtitleViewId: 'txtWaiting',
      timerViewId: 'txtTimer',
      enableLocation: true,
      viewData: {
        txtDriverStatus: 'Online',
        txtWaiting: 'Service actif',
        txtTimer: '00:00:00',
      },
    });
    
    // Configurer l'écoute des événements
    setupTaskEventListener();
    
    // Enregistrer une tâche avec communication
    await this.communicationManager.registerTaskWithCommunication(
      'myTask',
      'com.yourpackage.TaskWithJSCommunication',
      5000,
      true // Activer le polling
    );
  }
  
  async checkTaskResults() {
    const results = await this.communicationManager.getTaskResults('myTask');
    if (results) {
      console.log('Résultats actuels:', results);
    }
  }
  
  async onDestroy() {
    await this.communicationManager.stopAll();
    await ThunderBgService.stop();
  }
}

