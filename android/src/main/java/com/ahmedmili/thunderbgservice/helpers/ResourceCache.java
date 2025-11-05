package com.ahmedmili.thunderbgservice.helpers;

import android.content.Context;
import android.util.Log;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Cache intelligent pour les IDs de ressources Android.
 * Améliore les performances en évitant de résoudre les IDs à chaque appel.
 * 
 * Thread-safe: Utilise ConcurrentHashMap pour la sécurité en environnement multi-thread.
 */
public class ResourceCache {
    private static final String TAG = "ResourceCache";
    
    // Cache global thread-safe pour toutes les instances
    private static final Map<String, Integer> resourceCache = new ConcurrentHashMap<>();
    
    // Compteur de hits/misses pour statistiques
    private static int cacheHits = 0;
    private static int cacheMisses = 0;
    
    /**
     * Obtient un ID de ressource depuis le cache ou le résout si absent.
     * 
     * @param context Le contexte Android
     * @param resourceName Le nom de la ressource (ex: "notification_online")
     * @param resourceType Le type de ressource (ex: "layout", "id", "drawable")
     * @param packageName Le nom du package (app hôte ou plugin)
     * @return L'ID de la ressource, ou 0 si non trouvé
     */
    public static int getResourceId(Context context, String resourceName, String resourceType, String packageName) {
        if (resourceName == null || resourceName.isEmpty()) {
            return 0;
        }
        
        // Clé unique pour le cache: type:name:package
        String cacheKey = resourceType + ":" + resourceName + ":" + packageName;
        
        // Vérifier le cache d'abord
        Integer cachedId = resourceCache.get(cacheKey);
        if (cachedId != null && cachedId != 0) {
            cacheHits++;
            Log.d(TAG, "Cache HIT: " + cacheKey + " -> " + cachedId);
            return cachedId;
        }
        
        // Cache miss: résoudre l'ID
        cacheMisses++;
        int resourceId = context.getResources().getIdentifier(resourceName, resourceType, packageName);
        
        // Mettre en cache même si 0 (pour éviter de chercher à nouveau des ressources inexistantes)
        resourceCache.put(cacheKey, resourceId);
        
        if (resourceId != 0) {
            Log.d(TAG, "Cache MISS (resolved): " + cacheKey + " -> " + resourceId);
        } else {
            Log.d(TAG, "Cache MISS (not found): " + cacheKey);
        }
        
        return resourceId;
    }
    
    /**
     * Obtient un ID de ressource avec fallback sur plusieurs packages.
     * Cherche d'abord dans le plugin, puis dans l'app hôte.
     * 
     * @param context Le contexte Android
     * @param resourceName Le nom de la ressource
     * @param resourceType Le type de ressource
     * @param pluginPackage Le package du plugin
     * @param appPackage Le package de l'app hôte
     * @return L'ID de la ressource, ou 0 si non trouvé
     */
    public static int getResourceIdWithFallback(Context context, String resourceName, String resourceType, 
                                                String pluginPackage, String appPackage) {
        // Essayer d'abord dans le plugin
        int id = getResourceId(context, resourceName, resourceType, pluginPackage);
        if (id != 0) {
            return id;
        }
        
        // Fallback sur l'app hôte
        return getResourceId(context, resourceName, resourceType, appPackage);
    }
    
    /**
     * Invalide le cache pour une ressource spécifique.
     * Utile si une ressource est ajoutée dynamiquement.
     * 
     * @param resourceName Le nom de la ressource
     * @param resourceType Le type de ressource
     * @param packageName Le package
     */
    public static void invalidate(String resourceName, String resourceType, String packageName) {
        String cacheKey = resourceType + ":" + resourceName + ":" + packageName;
        resourceCache.remove(cacheKey);
        Log.d(TAG, "Cache invalidated: " + cacheKey);
    }
    
    /**
     * Vide complètement le cache.
     * Utile lors de changements majeurs de ressources.
     */
    public static void clear() {
        int size = resourceCache.size();
        resourceCache.clear();
        cacheHits = 0;
        cacheMisses = 0;
        Log.i(TAG, "Cache cleared: " + size + " entries removed");
    }
    
    /**
     * Obtient les statistiques du cache.
     * 
     * @return Un objet avec les statistiques (hit rate, etc.)
     */
    public static CacheStats getStats() {
        int total = cacheHits + cacheMisses;
        double hitRate = total > 0 ? (double) cacheHits / total * 100 : 0.0;
        return new CacheStats(cacheHits, cacheMisses, resourceCache.size(), hitRate);
    }
    
    /**
     * Classe pour les statistiques du cache.
     */
    public static class CacheStats {
        public final int hits;
        public final int misses;
        public final int size;
        public final double hitRate;
        
        public CacheStats(int hits, int misses, int size, double hitRate) {
            this.hits = hits;
            this.misses = misses;
            this.size = size;
            this.hitRate = hitRate;
        }
        
        @Override
        public String toString() {
            return String.format("CacheStats{hits=%d, misses=%d, size=%d, hitRate=%.2f%%}", 
                hits, misses, size, hitRate);
        }
    }
}

