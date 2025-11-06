package com.ahmedmili.thunderbgservice.theme;

import android.util.Log;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * Configuration d'un thème pour les notifications
 */
public class ThemeConfig {
    private String name;
    private String backgroundColor;
    private String titleColor;
    private String subtitleColor;
    private String accentColor;
    private String iconTintColor;
    private String timerColor;
    private String buttonBackgroundColor;
    private String buttonTextColor;
    private Integer fontSize;
    private String fontFamily;
    
    public ThemeConfig(String name) {
        this.name = name;
    }
    
    // Getters
    public String getName() { return name; }
    public String getBackgroundColor() { return backgroundColor; }
    public String getTitleColor() { return titleColor; }
    public String getSubtitleColor() { return subtitleColor; }
    public String getAccentColor() { return accentColor; }
    public String getIconTintColor() { return iconTintColor; }
    public String getTimerColor() { return timerColor; }
    public String getButtonBackgroundColor() { return buttonBackgroundColor; }
    public String getButtonTextColor() { return buttonTextColor; }
    public Integer getFontSize() { return fontSize; }
    public String getFontFamily() { return fontFamily; }
    
    // Setters avec chaining
    public ThemeConfig setName(String name) { 
        this.name = name; 
        return this; 
    }
    
    public ThemeConfig setBackgroundColor(String color) { 
        this.backgroundColor = color; 
        return this; 
    }
    
    public ThemeConfig setTitleColor(String color) { 
        this.titleColor = color; 
        return this; 
    }
    
    public ThemeConfig setSubtitleColor(String color) { 
        this.subtitleColor = color; 
        return this; 
    }
    
    public ThemeConfig setAccentColor(String color) { 
        this.accentColor = color; 
        return this; 
    }
    
    public ThemeConfig setIconTintColor(String color) { 
        this.iconTintColor = color; 
        return this; 
    }
    
    public ThemeConfig setTimerColor(String color) { 
        this.timerColor = color; 
        return this; 
    }
    
    public ThemeConfig setButtonBackgroundColor(String color) { 
        this.buttonBackgroundColor = color; 
        return this; 
    }
    
    public ThemeConfig setButtonTextColor(String color) { 
        this.buttonTextColor = color; 
        return this; 
    }
    
    public ThemeConfig setFontSize(Integer size) { 
        this.fontSize = size; 
        return this; 
    }
    
    public ThemeConfig setFontFamily(String family) { 
        this.fontFamily = family; 
        return this; 
    }
    
    /**
     * Convertit la configuration en JSON
     */
    public String toJson() {
        try {
            JSONObject json = new JSONObject();
            json.put("name", name);
            if (backgroundColor != null) json.put("backgroundColor", backgroundColor);
            if (titleColor != null) json.put("titleColor", titleColor);
            if (subtitleColor != null) json.put("subtitleColor", subtitleColor);
            if (accentColor != null) json.put("accentColor", accentColor);
            if (iconTintColor != null) json.put("iconTintColor", iconTintColor);
            if (timerColor != null) json.put("timerColor", timerColor);
            if (buttonBackgroundColor != null) json.put("buttonBackgroundColor", buttonBackgroundColor);
            if (buttonTextColor != null) json.put("buttonTextColor", buttonTextColor);
            if (fontSize != null) json.put("fontSize", fontSize);
            if (fontFamily != null) json.put("fontFamily", fontFamily);
            return json.toString();
        } catch (JSONException e) {
            Log.e("ThemeConfig", "Error converting to JSON", e);
            return "{}";
        }
    }
    
    /**
     * Crée une configuration depuis JSON
     */
    public static ThemeConfig fromJson(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            ThemeConfig config = new ThemeConfig(json.getString("name"));
            
            if (json.has("backgroundColor")) config.setBackgroundColor(json.getString("backgroundColor"));
            if (json.has("titleColor")) config.setTitleColor(json.getString("titleColor"));
            if (json.has("subtitleColor")) config.setSubtitleColor(json.getString("subtitleColor"));
            if (json.has("accentColor")) config.setAccentColor(json.getString("accentColor"));
            if (json.has("iconTintColor")) config.setIconTintColor(json.getString("iconTintColor"));
            if (json.has("timerColor")) config.setTimerColor(json.getString("timerColor"));
            if (json.has("buttonBackgroundColor")) config.setButtonBackgroundColor(json.getString("buttonBackgroundColor"));
            if (json.has("buttonTextColor")) config.setButtonTextColor(json.getString("buttonTextColor"));
            if (json.has("fontSize")) config.setFontSize(json.getInt("fontSize"));
            if (json.has("fontFamily")) config.setFontFamily(json.getString("fontFamily"));
            
            return config;
        } catch (JSONException e) {
            Log.e("ThemeConfig", "Error parsing JSON", e);
            return new ThemeConfig("default");
        }
    }
}

