/**
 * EXEMPLE: Utilisation des thèmes dynamiques
 * 
 * Les thèmes permettent de personnaliser l'apparence des notifications :
 * - Couleurs (fond, texte, accent)
 * - Styles (polices, tailles)
 * - Changement en temps réel
 */
import { ThunderBgService } from '@ahmed-mili/capacitor-thunder-bg-service';

/**
 * Exemple 1: Utiliser un thème prédéfini
 */
export async function usePredefinedTheme() {
  // Thèmes disponibles : default, dark, blue, green, orange, red
  await ThunderBgService.setTheme('dark');
  
  // Mettre à jour la notification pour appliquer le thème
  await ThunderBgService.update({
    notificationTitle: 'Service Actif',
    notificationSubtitle: 'Thème sombre appliqué',
  });
}

/**
 * Exemple 2: Créer un thème personnalisé
 */
export async function createCustomTheme() {
  await ThunderBgService.createTheme('my_custom_theme', {
    name: 'my_custom_theme',
    backgroundColor: '#F5F5F5',
    titleColor: '#1A1A1A',
    subtitleColor: '#666666',
    accentColor: '#FF6B6B',
    iconTintColor: '#FF6B6B',
    timerColor: '#FF6B6B',
  });
  
  // Utiliser le nouveau thème
  await ThunderBgService.setTheme('my_custom_theme');
}

/**
 * Exemple 3: Thèmes selon l'état de l'application
 */
export class ThemeStateManager {
  /**
   * Applique un thème selon l'état
   */
  async applyThemeForState(state: string) {
    switch (state) {
      case 'online':
        await ThunderBgService.setTheme('green');
        break;
      case 'waiting':
        await ThunderBgService.setTheme('orange');
        break;
      case 'urgent':
        await ThunderBgService.setTheme('red');
        break;
      case 'driving':
        await ThunderBgService.setTheme('blue');
        break;
      default:
        await ThunderBgService.setTheme('default');
    }
    
    // Mettre à jour la notification
    await ThunderBgService.update({
      notificationTitle: `État: ${state}`,
    });
  }
}

/**
 * Exemple 4: Thème adaptatif selon l'heure de la journée
 */
export async function applyTimeBasedTheme() {
  const hour = new Date().getHours();
  
  if (hour >= 6 && hour < 18) {
    // Jour : thème clair
    await ThunderBgService.setTheme('default');
  } else {
    // Nuit : thème sombre
    await ThunderBgService.setTheme('dark');
  }
  
  await ThunderBgService.update({
    notificationSubtitle: `Thème adaptatif (${hour}h)`,
  });
}

/**
 * Exemple 5: Gestionnaire de thèmes avec persistance
 */
export class ThemeManager {
  private currentTheme: string = 'default';
  
  /**
   * Initialise le gestionnaire de thèmes
   */
  async init() {
    // Charger le thème sauvegardé depuis le stockage local
    const savedTheme = localStorage.getItem('thunder_bg_theme') || 'default';
    await this.setTheme(savedTheme);
  }
  
  /**
   * Définit un thème et le sauvegarde
   */
  async setTheme(themeName: string) {
    const result = await ThunderBgService.setTheme(themeName);
    if (result.success) {
      this.currentTheme = themeName;
      localStorage.setItem('thunder_bg_theme', themeName);
    }
    return result;
  }
  
  /**
   * Obtient le thème actuel
   */
  async getCurrentTheme() {
    return await ThunderBgService.getCurrentTheme();
  }
  
  /**
   * Crée un thème personnalisé
   */
  async createTheme(themeName: string, config: any) {
    return await ThunderBgService.createTheme(themeName, config);
  }
  
  /**
   * Supprime un thème personnalisé
   */
  async removeTheme(themeName: string) {
    return await ThunderBgService.removeTheme(themeName);
  }
  
  /**
   * Obtient le nom du thème actuel
   */
  getCurrentThemeName(): string {
    return this.currentTheme;
  }
}

/**
 * Exemple 6: Thèmes pour application de livraison
 */
export class DeliveryThemeManager {
  private themeManager = new ThemeManager();
  
  /**
   * Thème pour l'état "disponible"
   */
  async setAvailableTheme() {
    await this.themeManager.setTheme('green');
    await ThunderBgService.update({
      notificationTitle: 'Disponible',
      notificationSubtitle: 'En attente de commande',
    });
  }
  
  /**
   * Thème pour l'état "en route"
   */
  async setEnRouteTheme() {
    await this.themeManager.setTheme('blue');
    await ThunderBgService.update({
      notificationTitle: 'En route',
      notificationSubtitle: 'Livraison en cours',
    });
  }
  
  /**
   * Thème pour l'état "arrivé"
   */
  async setArrivedTheme() {
    await this.themeManager.setTheme('orange');
    await ThunderBgService.update({
      notificationTitle: 'Arrivé',
      notificationSubtitle: 'Prêt pour la livraison',
    });
  }
  
  /**
   * Thème pour l'état "urgent"
   */
  async setUrgentTheme() {
    await this.themeManager.setTheme('red');
    await ThunderBgService.update({
      notificationTitle: '⚠️ Urgent',
      notificationSubtitle: 'Action requise',
    });
  }
}

/**
 * Exemple 7: Créer des thèmes personnalisés pour différents contextes
 */
export async function setupCustomThemes() {
  // Thème entreprise
  await ThunderBgService.createTheme('corporate', {
    name: 'corporate',
    backgroundColor: '#FFFFFF',
    titleColor: '#1E3A8A',
    subtitleColor: '#4B5563',
    accentColor: '#3B82F6',
    iconTintColor: '#3B82F6',
  });
  
  // Thème premium
  await ThunderBgService.createTheme('premium', {
    name: 'premium',
    backgroundColor: '#F9FAFB',
    titleColor: '#111827',
    subtitleColor: '#6B7280',
    accentColor: '#9333EA',
    iconTintColor: '#9333EA',
    timerColor: '#9333EA',
  });
  
  // Thème minimaliste
  await ThunderBgService.createTheme('minimal', {
    name: 'minimal',
    backgroundColor: '#FFFFFF',
    titleColor: '#000000',
    subtitleColor: '#808080',
    accentColor: '#000000',
    iconTintColor: '#000000',
  });
}

/**
 * Exemple 8: Animation de changement de thème
 */
export async function animateThemeChange(newTheme: string) {
  // Obtenir le thème actuel
  const currentTheme = await ThunderBgService.getCurrentTheme();
  
  // Changer le thème
  await ThunderBgService.setTheme(newTheme);
  
  // Mettre à jour la notification avec animation (dans votre app)
  await ThunderBgService.update({
    notificationTitle: 'Thème changé',
    notificationSubtitle: `De ${currentTheme.name} à ${newTheme}`,
  });
}

