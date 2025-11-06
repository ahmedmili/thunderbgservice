/**
 * EXEMPLE: Utilisation des métriques de performance
 * 
 * Le plugin collecte automatiquement des métriques sur :
 * - Les tâches en arrière-plan (nombre d'exécutions, temps moyen)
 * - Les notifications (nombre de mises à jour)
 * - La localisation (nombre de mises à jour)
 * - Les géofences (nombre de déclenchements)
 * - Le service (temps d'activité)
 * - La batterie (niveau actuel, consommation)
 */
import { ThunderBgService } from '@ahmed-mili/capacitor-thunder-bg-service';

/**
 * Exemple 1: Récupérer toutes les métriques
 */
export async function getAllMetrics() {
  const { metrics } = await ThunderBgService.getMetrics();
  
  console.log('=== Métriques de Performance ===');
  console.log('Tâches exécutées:', metrics.taskExecutionCount);
  console.log('Temps moyen d\'exécution:', metrics.avgTaskExecutionTime, 'ms');
  console.log('Notifications mises à jour:', metrics.notificationUpdateCount);
  console.log('Mises à jour de localisation:', metrics.locationUpdateCount);
  console.log('Géofences déclenchées:', metrics.geofenceTriggerCount);
  console.log('Temps d\'activité:', metrics.serviceUptimeHours, 'heures');
  console.log('Niveau de batterie:', metrics.currentBatteryLevel, '%');
  console.log('Consommation de batterie:', metrics.batteryDrain, '%');
  
  if (metrics.resourceCache) {
    console.log('Cache de ressources - Hit rate:', metrics.resourceCache.hitRate, '%');
  }
  
  return metrics;
}

/**
 * Exemple 2: Monitoring périodique des métriques
 */
export class MetricsMonitor {
  private intervalId?: number;
  
  /**
   * Démarrer le monitoring périodique
   */
  startMonitoring(intervalMs: number = 30000) {
    this.intervalId = window.setInterval(async () => {
      const { metrics } = await ThunderBgService.getMetrics();
      
      // Vérifier les alertes
      this.checkAlerts(metrics);
      
      // Afficher les métriques
      this.displayMetrics(metrics);
    }, intervalMs);
  }
  
  /**
   * Arrêter le monitoring
   */
  stopMonitoring() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
      this.intervalId = undefined;
    }
  }
  
  /**
   * Vérifier les alertes de performance
   */
  private checkAlerts(metrics: any) {
    // Alerte si consommation de batterie élevée
    if (metrics.batteryDrain > 10) {
      console.warn('⚠️ Consommation de batterie élevée:', metrics.batteryDrain, '%');
    }
    
    // Alerte si temps d'exécution moyen élevé
    if (metrics.avgTaskExecutionTime > 1000) {
      console.warn('⚠️ Temps d\'exécution moyen élevé:', metrics.avgTaskExecutionTime, 'ms');
    }
    
    // Alerte si beaucoup de tâches exécutées
    if (metrics.taskExecutionCount > 1000) {
      console.warn('⚠️ Nombre élevé de tâches exécutées:', metrics.taskExecutionCount);
    }
  }
  
  /**
   * Afficher les métriques de façon formatée
   */
  private displayMetrics(metrics: any) {
    console.log(`
📊 Métriques ThunderBG:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📋 Tâches: ${metrics.taskExecutionCount} exécutions
   ⏱️  Temps moyen: ${metrics.avgTaskExecutionTime.toFixed(2)}ms
   ⏱️  Temps total: ${(metrics.totalTaskExecutionTime / 1000).toFixed(2)}s

🔔 Notifications: ${metrics.notificationUpdateCount} mises à jour

📍 Localisation: ${metrics.locationUpdateCount} mises à jour

🗺️  Géofences: ${metrics.geofenceTriggerCount} déclenchements

⏰ Service: ${metrics.serviceUptimeHours.toFixed(2)} heures d'activité

🔋 Batterie: ${metrics.currentBatteryLevel}% (consommation: ${metrics.batteryDrain}%)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    `);
  }
}

/**
 * Exemple 3: Dashboard de métriques
 */
export class MetricsDashboard {
  /**
   * Afficher un dashboard complet des métriques
   */
  async displayDashboard() {
    const { metrics } = await ThunderBgService.getMetrics();
    
    return {
      tasks: {
        total: metrics.taskExecutionCount,
        averageTime: `${metrics.avgTaskExecutionTime.toFixed(2)}ms`,
        totalTime: `${(metrics.totalTaskExecutionTime / 1000).toFixed(2)}s`,
      },
      notifications: {
        updates: metrics.notificationUpdateCount,
      },
      location: {
        updates: metrics.locationUpdateCount,
      },
      geofences: {
        triggers: metrics.geofenceTriggerCount,
      },
      service: {
        uptime: `${metrics.serviceUptimeHours.toFixed(2)}h`,
        uptimeMs: metrics.serviceUptime,
      },
      battery: {
        level: `${metrics.currentBatteryLevel}%`,
        drain: `${metrics.batteryDrain}%`,
      },
      cache: metrics.resourceCache ? {
        hits: metrics.resourceCache.hits,
        misses: metrics.resourceCache.misses,
        hitRate: `${metrics.resourceCache.hitRate.toFixed(2)}%`,
        size: metrics.resourceCache.size,
      } : null,
    };
  }
}

/**
 * Exemple 4: Réinitialiser les métriques
 */
export async function resetAllMetrics() {
  await ThunderBgService.resetMetrics();
  console.log('✅ Toutes les métriques ont été réinitialisées');
}

/**
 * Exemple 5: Utilisation dans un service Angular
 */
export class MetricsService {
  private monitor = new MetricsMonitor();
  
  /**
   * Initialiser le service de métriques
   */
  init() {
    // Démarrer le monitoring toutes les 30 secondes
    this.monitor.startMonitoring(30000);
  }
  
  /**
   * Obtenir les métriques actuelles
   */
  async getCurrentMetrics() {
    return await ThunderBgService.getMetrics();
  }
  
  /**
   * Vérifier la santé du service
   */
  async checkHealth() {
    const { metrics } = await ThunderBgService.getMetrics();
    
    const health = {
      isHealthy: true,
      issues: [] as string[],
    };
    
    // Vérifier la batterie
    if (metrics.currentBatteryLevel < 20) {
      health.issues.push('Batterie faible');
    }
    
    // Vérifier la consommation
    if (metrics.batteryDrain > 15) {
      health.issues.push('Consommation de batterie élevée');
    }
    
    // Vérifier les performances
    if (metrics.avgTaskExecutionTime > 2000) {
      health.issues.push('Temps d\'exécution élevé');
    }
    
    if (health.issues.length > 0) {
      health.isHealthy = false;
    }
    
    return health;
  }
  
  /**
   * Nettoyer
   */
  destroy() {
    this.monitor.stopMonitoring();
  }
}

