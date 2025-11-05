/**
 * EXEMPLE: Utilisation des images dynamiques dans les notifications
 * 
 * Le plugin supporte maintenant les images dans viewData :
 * - Base64 (data:image/png;base64,...)
 * - URLs HTTP/HTTPS
 * - Ressources drawable (noms)
 */
import { ThunderBgService } from '@ahmed-mili/capacitor-thunder-bg-service';

/**
 * Exemple 1: Image Base64
 */
export async function startWithBase64Image() {
  // Convertir une image en Base64 (exemple)
  const base64Image = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==';
  
  await ThunderBgService.start({
    customLayout: 'notification_with_avatar',
    titleViewId: 'txtDriverName',
    subtitleViewId: 'txtStatus',
    viewData: {
      txtDriverName: 'John Doe',
      txtStatus: 'En ligne',
      imgAvatar: base64Image, // Image Base64
    },
  });
}

/**
 * Exemple 2: Image depuis URL
 */
export async function updateWithUrlImage() {
  await ThunderBgService.update({
    viewData: {
      imgClientPhoto: 'https://api.example.com/users/123/avatar.jpg', // URL
      imgStatusIcon: 'https://cdn.example.com/icons/online.png',
    },
  });
}

/**
 * Exemple 3: Mélange texte et images
 */
export async function updateMixedContent() {
  await ThunderBgService.update({
    viewData: {
      // Texte
      txtDriverName: 'John Doe',
      txtDestination: '123 Main St',
      txtDistance: '2.5 km',
      
      // Images
      imgAvatar: 'data:image/png;base64,...', // Base64
      imgMap: 'https://maps.example.com/route.png', // URL
      imgStatus: 'ic_online', // Ressource drawable (optionnel)
    },
  });
}

/**
 * Exemple 4: Notification avec avatar et photo du client
 */
export async function startRideWithImages(clientName: string, avatarUrl: string, clientPhotoUrl: string) {
  await ThunderBgService.start({
    customLayout: 'notification_ride_with_images',
    titleViewId: 'txtDriverName',
    subtitleViewId: 'txtClientName',
    viewData: {
      txtDriverName: 'Votre chauffeur',
      txtClientName: clientName,
      imgDriverAvatar: avatarUrl, // URL du serveur
      imgClientPhoto: clientPhotoUrl, // URL du serveur
    },
  });
}

/**
 * Exemple 5: Mise à jour dynamique d'image selon l'état
 */
export async function updateStatusImage(isOnline: boolean) {
  const statusIcon = isOnline 
    ? 'https://cdn.example.com/icons/online.png'
    : 'https://cdn.example.com/icons/offline.png';
    
  await ThunderBgService.update({
    viewData: {
      imgStatus: statusIcon,
      txtStatus: isOnline ? 'En ligne' : 'Hors ligne',
    },
  });
}

/**
 * Exemple 6: Image depuis ressource locale (si supporté)
 * Note: Pour les ressources drawable, utilisez le nom de la ressource
 */
export async function useLocalResource() {
  // Note: Les ressources drawable doivent être vérifiées manuellement
  // Le plugin détecte automatiquement les URLs et Base64
  await ThunderBgService.update({
    viewData: {
      imgIcon: 'ic_notification', // Nom de la ressource drawable
    },
  });
}

