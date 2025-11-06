package com.ahmedmili.thunderbgservice.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire de thèmes dynamiques pour les notifications
 * Permet de changer les couleurs et styles des notifications en temps réel
 */
public class ThemeManager {
    private static final String TAG = "ThemeManager";
    private static final String PREFS_NAME = "thunder_bg_themes";
    private static final String KEY_CURRENT_THEME = "current_theme";
    
    private static ThemeManager instance;
    private final Context context;
    private final SharedPreferences prefs;
    private final Map<String, ThemeConfig> themeCache = new HashMap<>();
    private ThemeConfig currentTheme;
    
    private ThemeManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        initializeDefaultThemes();
        loadCurrentTheme();
    }
    
    public static synchronized ThemeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThemeManager(context);
        }
        return instance;
    }
    
    /**
     * Initialise les thèmes par défaut
     */
    private void initializeDefaultThemes() {
        // Thème par défaut
        ThemeConfig defaultTheme = new ThemeConfig("default")
            .setBackgroundColor("#FFFFFF")
            .setTitleColor("#000000")
            .setSubtitleColor("#666666")
            .setAccentColor("#2196F3")
            .setIconTintColor("#2196F3");
        themeCache.put("default", defaultTheme);
        
        // Thème sombre
        ThemeConfig darkTheme = new ThemeConfig("dark")
            .setBackgroundColor("#212121")
            .setTitleColor("#FFFFFF")
            .setSubtitleColor("#B0B0B0")
            .setAccentColor("#64B5F6")
            .setIconTintColor("#64B5F6");
        themeCache.put("dark", darkTheme);
        
        // Thème bleu
        ThemeConfig blueTheme = new ThemeConfig("blue")
            .setBackgroundColor("#E3F2FD")
            .setTitleColor("#1565C0")
            .setSubtitleColor("#424242")
            .setAccentColor("#1976D2")
            .setIconTintColor("#1976D2");
        themeCache.put("blue", blueTheme);
        
        // Thème vert (pour état "en ligne")
        ThemeConfig greenTheme = new ThemeConfig("green")
            .setBackgroundColor("#E8F5E9")
            .setTitleColor("#2E7D32")
            .setSubtitleColor("#424242")
            .setAccentColor("#4CAF50")
            .setIconTintColor("#4CAF50");
        themeCache.put("green", greenTheme);
        
        // Thème orange (pour état "en attente")
        ThemeConfig orangeTheme = new ThemeConfig("orange")
            .setBackgroundColor("#FFF3E0")
            .setTitleColor("#E65100")
            .setSubtitleColor("#424242")
            .setAccentColor("#FF9800")
            .setIconTintColor("#FF9800");
        themeCache.put("orange", orangeTheme);
        
        // Thème rouge (pour état "urgent")
        ThemeConfig redTheme = new ThemeConfig("red")
            .setBackgroundColor("#FFEBEE")
            .setTitleColor("#C62828")
            .setSubtitleColor("#424242")
            .setAccentColor("#F44336")
            .setIconTintColor("#F44336");
        themeCache.put("red", redTheme);
    }
    
    /**
     * Charge le thème actuel depuis les préférences
     */
    private void loadCurrentTheme() {
        String themeName = prefs.getString(KEY_CURRENT_THEME, "default");
        currentTheme = themeCache.get(themeName);
        if (currentTheme == null) {
            currentTheme = themeCache.get("default");
        }
        Log.i(TAG, "Current theme loaded: " + (currentTheme != null ? currentTheme.getName() : "null"));
    }
    
    /**
     * Définit le thème actuel
     */
    public boolean setTheme(String themeName) {
        ThemeConfig theme = themeCache.get(themeName);
        if (theme == null) {
            Log.w(TAG, "Theme not found: " + themeName);
            return false;
        }
        
        currentTheme = theme;
        prefs.edit().putString(KEY_CURRENT_THEME, themeName).apply();
        Log.i(TAG, "Theme changed to: " + themeName);
        return true;
    }
    
    /**
     * Crée un nouveau thème personnalisé
     */
    public boolean createTheme(String themeName, ThemeConfig theme) {
        if (themeName == null || themeName.isEmpty()) {
            Log.e(TAG, "Theme name cannot be empty");
            return false;
        }
        
        theme.setName(themeName);
        themeCache.put(themeName, theme);
        
        // Sauvegarder dans les préférences
        String themeJson = theme.toJson();
        prefs.edit().putString("theme_" + themeName, themeJson).apply();
        
        Log.i(TAG, "Custom theme created: " + themeName);
        return true;
    }
    
    /**
     * Obtient le thème actuel
     */
    public ThemeConfig getCurrentTheme() {
        return currentTheme != null ? currentTheme : themeCache.get("default");
    }
    
    /**
     * Obtient un thème par nom
     */
    public ThemeConfig getTheme(String themeName) {
        return themeCache.get(themeName);
    }
    
    /**
     * Obtient tous les thèmes disponibles
     */
    public Map<String, ThemeConfig> getAllThemes() {
        return new HashMap<>(themeCache);
    }
    
    /**
     * Supprime un thème personnalisé (ne peut pas supprimer les thèmes par défaut)
     */
    public boolean removeTheme(String themeName) {
        if (themeName.equals("default") || themeName.equals("dark") || 
            themeName.equals("blue") || themeName.equals("green") || 
            themeName.equals("orange") || themeName.equals("red")) {
            Log.w(TAG, "Cannot remove default theme: " + themeName);
            return false;
        }
        
        ThemeConfig removed = themeCache.remove(themeName);
        if (removed != null) {
            prefs.edit().remove("theme_" + themeName).apply();
            Log.i(TAG, "Theme removed: " + themeName);
            return true;
        }
        return false;
    }
    
    /**
     * Convertit une couleur hexadécimale en int ARGB
     */
    public static int parseColor(String colorHex) {
        try {
            return Color.parseColor(colorHex);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Invalid color: " + colorHex + ", using default");
            return Color.parseColor("#000000");
        }
    }
}

