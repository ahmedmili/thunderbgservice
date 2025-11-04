/**
 * EXEMPLE D'UTILISATION - Notification dynamique avec ThunderBgService
 * 
 * Ce fichier montre comment utiliser le plugin avec dynamisation
 * selon l'état de l'application.
 */

import { ThunderBgService } from '@webify/capacitor-thunder-bg-service';

// État de l'application (synchro avec NotificationDynamicHelper.java)
enum AppState {
  ONLINE = 'ONLINE',
  OFFLINE = 'OFFLINE',
  ON_RIDE = 'ON_RIDE',
  WAITING_PICKUP = 'WAITING_PICKUP',
  DRIVING = 'DRIVING',
  ARRIVED = 'ARRIVED',
  COMPLETED = 'COMPLETED'
}

// Helper TypeScript pour mapper les états aux options
class NotificationDynamicHelper {
  static getTitleForState(state: AppState): string {
    const titles: Record<AppState, string> = {
      [AppState.ONLINE]: 'Disponible',
      [AppState.OFFLINE]: 'Hors ligne',
      [AppState.ON_RIDE]: 'En course',
      [AppState.WAITING_PICKUP]: 'En attente',
      [AppState.DRIVING]: 'Conduite',
      [AppState.ARRIVED]: 'Arrivé',
      [AppState.COMPLETED]: 'Course terminée',
    };
    return titles[state] || 'Online';
  }

  static getSubtitleForState(state: AppState): string {
    const subtitles: Record<AppState, string> = {
      [AppState.ONLINE]: 'En attente de courses',
      [AppState.OFFLINE]: 'Service arrêté',
      [AppState.ON_RIDE]: 'En route vers la destination',
      [AppState.WAITING_PICKUP]: 'En attente du client',
      [AppState.DRIVING]: 'En conduite',
      [AppState.ARRIVED]: 'Client pris en charge',
      [AppState.COMPLETED]: 'Course terminée avec succès',
    };
    return subtitles[state] || 'Running';
  }

  static getLayoutForState(state: AppState): string {
    const layouts: Record<AppState, string> = {
      [AppState.ONLINE]: 'notification_online',
      [AppState.OFFLINE]: 'notification_default',
      [AppState.ON_RIDE]: 'notification_riding',
      [AppState.WAITING_PICKUP]: 'notification_waiting',
      [AppState.DRIVING]: 'notification_riding',
      [AppState.ARRIVED]: 'notification_arrived',
      [AppState.COMPLETED]: 'notification_default',
    };
    return layouts[state] || 'notification_default';
  }

  static getViewIdsForLayout(layoutName: string) {
    if (layoutName.includes('online') || layoutName.includes('default')) {
      return {
        titleId: 'txtTitle',
        subtitleId: 'txtSubtitle',
        timerId: 'txtTimer',
      };
    } else if (layoutName.includes('riding') || layoutName.includes('driving')) {
      return {
        titleId: 'txtDriverStatus',
        subtitleId: 'txtDestination',
        timerId: 'txtElapsedTime',
      };
    } else if (layoutName.includes('waiting')) {
      return {
        titleId: 'txtWaitingTitle',
        subtitleId: 'txtClientInfo',
        timerId: 'txtWaitTime',
      };
    } else if (layoutName.includes('arrived')) {
      return {
        titleId: 'txtArrivalTitle',
        subtitleId: 'txtArrivalInfo',
        timerId: 'txtArrivalTime',
      };
    }
    // Par défaut
    return {
      titleId: 'txtTitle',
      subtitleId: 'txtSubtitle',
      timerId: 'txtTimer',
    };
  }

  static buildOptionsForState(state: AppState, enableLocation = true) {
    const layout = this.getLayoutForState(state);
    const viewIds = this.getViewIdsForLayout(layout);
    
    return {
      notificationTitle: this.getTitleForState(state),
      notificationSubtitle: this.getSubtitleForState(state),
      customLayout: layout,
      titleViewId: viewIds.titleId,
      subtitleViewId: viewIds.subtitleId,
      timerViewId: viewIds.timerId,
      enableLocation,
      soundsEnabled: state === AppState.ON_RIDE || state === AppState.ARRIVED,
    };
  }
}

// ==========================================
// EXEMPLES D'UTILISATION
// ==========================================

/**
 * Exemple 1: Démarrer le service avec état ONLINE
 */
export async function startServiceOnline() {
  const options = NotificationDynamicHelper.buildOptionsForState(AppState.ONLINE, true);
  
  await ThunderBgService.start(options);
  console.log('Service démarré avec état ONLINE');
}

/**
 * Exemple 2: Changer dynamiquement l'état selon les événements
 */
export class NotificationStateManager {
  private currentState: AppState = AppState.OFFLINE;

  async goOnline() {
    this.currentState = AppState.ONLINE;
    const options = NotificationDynamicHelper.buildOptionsForState(this.currentState);
    await ThunderBgService.start(options);
  }

  async startRide() {
    this.currentState = AppState.ON_RIDE;
    const options = NotificationDynamicHelper.buildOptionsForState(this.currentState);
    // Changer le layout ET le contenu dynamiquement
    await ThunderBgService.update({
      notificationTitle: options.notificationTitle,
      notificationSubtitle: options.notificationSubtitle,
      customLayout: options.customLayout,        // NOUVEAU: changer le layout
      titleViewId: options.titleViewId,          // NOUVEAU: IDs pour le nouveau layout
      subtitleViewId: options.subtitleViewId,
      timerViewId: options.timerViewId,
    });
  }

  async waitingForPickup() {
    this.currentState = AppState.WAITING_PICKUP;
    const options = NotificationDynamicHelper.buildOptionsForState(this.currentState);
    await ThunderBgService.update({
      notificationTitle: options.notificationTitle,
      notificationSubtitle: options.notificationSubtitle,
      customLayout: options.customLayout,        // Changer vers layout "waiting"
      titleViewId: options.titleViewId,
      subtitleViewId: options.subtitleViewId,
      timerViewId: options.timerViewId,
    });
  }

  async arrived() {
    this.currentState = AppState.ARRIVED;
    const options = NotificationDynamicHelper.buildOptionsForState(this.currentState);
    await ThunderBgService.update({
      notificationTitle: options.notificationTitle,
      notificationSubtitle: options.notificationSubtitle,
      customLayout: options.customLayout,        // Changer vers layout "arrived"
      titleViewId: options.titleViewId,
      subtitleViewId: options.subtitleViewId,
      timerViewId: options.timerViewId,
    });
  }

  async completeRide() {
    this.currentState = AppState.COMPLETED;
    await ThunderBgService.update({
      notificationTitle: NotificationDynamicHelper.getTitleForState(this.currentState),
      notificationSubtitle: NotificationDynamicHelper.getSubtitleForState(this.currentState),
    });
    
    // Après quelques secondes, arrêter le service
    setTimeout(async () => {
      await ThunderBgService.stop();
      this.currentState = AppState.OFFLINE;
    }, 3000);
  }

  async stopService() {
    await ThunderBgService.stop();
    this.currentState = AppState.OFFLINE;
  }
}

/**
 * Exemple 3: Utilisation dans un service Angular
 */
export class ThunderBgServiceManager {
  private stateManager = new NotificationStateManager();

  async initialize() {
    // Demander les permissions
    // await this.requestPermissions();
    
    // Démarrer avec état ONLINE
    await this.stateManager.goOnline();
  }

  onRideAssigned() {
    this.stateManager.startRide();
  }

  onArrivedAtPickup() {
    this.stateManager.waitingForPickup();
  }

  onClientPickedUp() {
    this.stateManager.arrived();
  }

  onRideCompleted() {
    this.stateManager.completeRide();
  }

  async onLogout() {
    await this.stateManager.stopService();
  }
}

/**
 * Exemple 4: Utilisation avec des données dynamiques (ex: nom du client)
 */
export async function updateNotificationWithClientInfo(clientName: string, destination: string) {
  await ThunderBgService.update({
    notificationTitle: `En route vers ${destination}`,
    notificationSubtitle: `Client: ${clientName}`,
  });
}

/**
 * Exemple 5: Utilisation avec un layout personnalisé selon l'état
 */
export async function startWithCustomLayout(state: AppState) {
  const layout = NotificationDynamicHelper.getLayoutForState(state);
  const viewIds = NotificationDynamicHelper.getViewIdsForLayout(layout);
  
  await ThunderBgService.start({
    notificationTitle: NotificationDynamicHelper.getTitleForState(state),
    notificationSubtitle: NotificationDynamicHelper.getSubtitleForState(state),
    customLayout: layout,
    titleViewId: viewIds.titleId,
    subtitleViewId: viewIds.subtitleId,
    timerViewId: viewIds.timerId,
    enableLocation: true,
    soundsEnabled: state === AppState.ON_RIDE || state === AppState.ARRIVED,
  });
}

/**
 * Exemple 6: Changer de layout dynamiquement (NOUVEAU!)
 * 
 * Vous pouvez changer de layout/écran de notification à tout moment
 * sans redémarrer le service, simplement en appelant update() avec
 * les nouveaux paramètres de layout.
 */
export async function switchLayoutDynamically(newState: AppState) {
  const layout = NotificationDynamicHelper.getLayoutForState(newState);
  const viewIds = NotificationDynamicHelper.getViewIdsForLayout(layout);
  
  await ThunderBgService.update({
    notificationTitle: NotificationDynamicHelper.getTitleForState(newState),
    notificationSubtitle: NotificationDynamicHelper.getSubtitleForState(newState),
    // Changer le layout ET les IDs dynamiquement
    customLayout: layout,                    // Nouveau layout
    titleViewId: viewIds.titleId,           // Nouveaux IDs
    subtitleViewId: viewIds.subtitleId,
    timerViewId: viewIds.timerId,
  });
  
  console.log(`Layout changé vers: ${layout}`);
}

/**
 * Exemple 7: Changer de layout avec des données dynamiques
 */
export async function switchToRideLayoutWithData(
  clientName: string,
  destination: string,
  distance: string
) {
  // Changer vers le layout "riding" avec des données spécifiques
  await ThunderBgService.update({
    notificationTitle: `Course en cours`,
    notificationSubtitle: `${clientName} → ${destination}`,
    customLayout: 'notification_riding',      // Changer le layout
    titleViewId: 'txtDriverStatus',          // IDs pour le layout "riding"
    subtitleViewId: 'txtDestination',
    timerViewId: 'txtElapsedTime',
    // Note: Vous pouvez aussi mettre à jour d'autres vues si votre layout
    // a des IDs supplémentaires (distance, etc.)
  });
}

/**
 * Exemple 8: Gestionnaire complet avec changement de layout automatique
 */
export class DynamicLayoutManager {
  private currentLayout: string = 'notification_default';

  /**
   * Change de layout selon l'état et met à jour automatiquement
   */
  async switchToState(state: AppState) {
    const layout = NotificationDynamicHelper.getLayoutForState(state);
    const viewIds = NotificationDynamicHelper.getViewIdsForLayout(layout);
    
    if (this.currentLayout !== layout) {
      // Le layout change, on doit le mettre à jour
      await ThunderBgService.update({
        notificationTitle: NotificationDynamicHelper.getTitleForState(state),
        notificationSubtitle: NotificationDynamicHelper.getSubtitleForState(state),
        customLayout: layout,
        titleViewId: viewIds.titleId,
        subtitleViewId: viewIds.subtitleId,
        timerViewId: viewIds.timerId,
      });
      this.currentLayout = layout;
      console.log(`Layout changé: ${layout}`);
    } else {
      // Même layout, juste mettre à jour le contenu
      await ThunderBgService.update({
        notificationTitle: NotificationDynamicHelper.getTitleForState(state),
        notificationSubtitle: NotificationDynamicHelper.getSubtitleForState(state),
      });
    }
  }
}

