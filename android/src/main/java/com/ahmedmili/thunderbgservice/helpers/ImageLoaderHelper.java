package com.ahmedmili.thunderbgservice.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Helper pour charger et appliquer des images dynamiques dans les notifications.
 * Supporte Base64 et URLs HTTP/HTTPS.
 */
public class ImageLoaderHelper {
    private static final String TAG = "ImageLoaderHelper";
    
    private static final int MAX_CACHE_SIZE = 50; // Nombre max d'images en cache
    private static final ConcurrentHashMap<String, Bitmap> imageCache = new ConcurrentHashMap<>();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);
    
    /**
     * Charge une image depuis Base64 ou URL et l'applique à un ImageView dans RemoteViews
     * 
     * @param context Le contexte
     * @param views Les RemoteViews de la notification
     * @param imageViewId L'ID de l'ImageView dans le layout
     * @param imageSource L'image source (Base64 ou URL)
     */
    public static void loadImage(Context context, RemoteViews views, int imageViewId, String imageSource) {
        if (imageSource == null || imageSource.isEmpty()) {
            Log.w(TAG, "Image source is null or empty");
            return;
        }
        
        // Vérifier le cache d'abord
        Bitmap cached = imageCache.get(imageSource);
        if (cached != null && !cached.isRecycled()) {
            views.setImageViewBitmap(imageViewId, cached);
            Log.d(TAG, "Image loaded from cache for viewId: " + imageViewId);
            return;
        }
        
        // Charger l'image
        if (imageSource.startsWith("data:image") || imageSource.startsWith("base64,")) {
            // Base64
            loadFromBase64(context, views, imageViewId, imageSource);
        } else if (imageSource.startsWith("http://") || imageSource.startsWith("https://")) {
            // URL
            loadFromUrl(context, views, imageViewId, imageSource);
        } else {
            // Essayer de le traiter comme un nom de ressource drawable
            loadFromResource(context, views, imageViewId, imageSource);
        }
    }
    
    /**
     * Charge une image depuis Base64
     */
    private static void loadFromBase64(Context context, RemoteViews views, int imageViewId, String base64Data) {
        executorService.submit(() -> {
            try {
                // Extraire les données Base64
                String base64 = base64Data;
                if (base64Data.contains(",")) {
                    base64 = base64Data.substring(base64Data.indexOf(",") + 1);
                }
                
                byte[] decodedBytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                
                if (bitmap != null) {
                    // Mettre en cache
                    cacheImage(base64Data, bitmap);
                    
                    // Appliquer à la vue (sur le thread principal)
                    views.setImageViewBitmap(imageViewId, bitmap);
                    Log.d(TAG, "Base64 image loaded and applied to viewId: " + imageViewId);
                } else {
                    Log.e(TAG, "Failed to decode Base64 image");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading Base64 image", e);
            }
        });
    }
    
    /**
     * Charge une image depuis une URL
     */
    private static void loadFromUrl(Context context, RemoteViews views, int imageViewId, String imageUrl) {
        executorService.submit(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();
                connection.disconnect();
                
                if (bitmap != null) {
                    // Mettre en cache
                    cacheImage(imageUrl, bitmap);
                    
                    // Appliquer à la vue
                    views.setImageViewBitmap(imageViewId, bitmap);
                    Log.d(TAG, "URL image loaded and applied to viewId: " + imageViewId);
                } else {
                    Log.e(TAG, "Failed to decode image from URL: " + imageUrl);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading image from URL: " + imageUrl, e);
            }
        });
    }
    
    /**
     * Charge une image depuis les ressources drawable
     */
    private static void loadFromResource(Context context, RemoteViews views, int imageViewId, String resourceName) {
        try {
            String pkg = context.getPackageName();
            int resourceId = ResourceCache.getResourceId(context, resourceName, "drawable", pkg);
            
            if (resourceId != 0) {
                views.setImageViewResource(imageViewId, resourceId);
                Log.d(TAG, "Resource image loaded: " + resourceName + " -> viewId: " + imageViewId);
            } else {
                Log.w(TAG, "Resource not found: " + resourceName);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading resource image: " + resourceName, e);
        }
    }
    
    /**
     * Met une image en cache
     */
    private static void cacheImage(String key, Bitmap bitmap) {
        // Limiter la taille du cache
        if (imageCache.size() >= MAX_CACHE_SIZE) {
            // Retirer la première entrée (FIFO)
            String firstKey = null;
            for (String k : imageCache.keySet()) {
                firstKey = k;
                break;
            }
            if (firstKey != null) {
                Bitmap oldBitmap = imageCache.remove(firstKey);
                if (oldBitmap != null && !oldBitmap.isRecycled()) {
                    oldBitmap.recycle();
                }
            }
        }
        
        imageCache.put(key, bitmap);
    }
    
    /**
     * Vide le cache d'images
     */
    public static void clearCache() {
        for (Bitmap bitmap : imageCache.values()) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        imageCache.clear();
        Log.i(TAG, "Image cache cleared");
    }
    
    /**
     * Obtient la taille du cache
     */
    public static int getCacheSize() {
        return imageCache.size();
    }
    
    /**
     * Charge une image de façon synchrone (pour les cas spéciaux)
     */
    public static Bitmap loadImageSync(String imageSource) {
        if (imageSource == null || imageSource.isEmpty()) {
            return null;
        }
        
        // Vérifier le cache
        Bitmap cached = imageCache.get(imageSource);
        if (cached != null && !cached.isRecycled()) {
            return cached;
        }
        
        try {
            if (imageSource.startsWith("data:image") || imageSource.startsWith("base64,")) {
                String base64 = imageSource.contains(",") ? 
                    imageSource.substring(imageSource.indexOf(",") + 1) : imageSource;
                byte[] decodedBytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                if (bitmap != null) {
                    cacheImage(imageSource, bitmap);
                }
                return bitmap;
            } else if (imageSource.startsWith("http://") || imageSource.startsWith("https://")) {
                URL url = new URL(imageSource);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();
                connection.disconnect();
                
                if (bitmap != null) {
                    cacheImage(imageSource, bitmap);
                }
                return bitmap;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading image synchronously: " + imageSource, e);
        }
        
        return null;
    }
}

