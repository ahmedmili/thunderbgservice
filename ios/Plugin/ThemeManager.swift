import Foundation
import UIKit

/**
 * Gestionnaire de thèmes dynamiques pour iOS
 * Permet de changer les couleurs et styles des notifications en temps réel
 */
class ThemeManager {
    static let shared = ThemeManager()
    
    private var themes: [String: ThemeConfig] = [:]
    private var currentThemeName: String = "default"
    
    private init() {
        initializeDefaultThemes()
    }
    
    /**
     * Initialise les thèmes par défaut
     */
    private func initializeDefaultThemes() {
        // Thème par défaut
        themes["default"] = ThemeConfig(
            name: "default",
            backgroundColor: UIColor.white,
            titleColor: UIColor.black,
            subtitleColor: UIColor.gray,
            accentColor: UIColor.systemBlue,
            iconTintColor: UIColor.systemBlue
        )
        
        // Thème sombre
        themes["dark"] = ThemeConfig(
            name: "dark",
            backgroundColor: UIColor(red: 0.13, green: 0.13, blue: 0.13, alpha: 1.0),
            titleColor: UIColor.white,
            subtitleColor: UIColor.lightGray,
            accentColor: UIColor.systemBlue,
            iconTintColor: UIColor.systemBlue
        )
        
        // Thème bleu
        themes["blue"] = ThemeConfig(
            name: "blue",
            backgroundColor: UIColor(red: 0.89, green: 0.95, blue: 1.0, alpha: 1.0),
            titleColor: UIColor(red: 0.08, green: 0.40, blue: 0.75, alpha: 1.0),
            subtitleColor: UIColor.darkGray,
            accentColor: UIColor(red: 0.10, green: 0.46, blue: 0.82, alpha: 1.0),
            iconTintColor: UIColor(red: 0.10, green: 0.46, blue: 0.82, alpha: 1.0)
        )
        
        // Thème vert (pour état "en ligne")
        themes["green"] = ThemeConfig(
            name: "green",
            backgroundColor: UIColor(red: 0.91, green: 0.96, blue: 0.91, alpha: 1.0),
            titleColor: UIColor(red: 0.18, green: 0.49, blue: 0.20, alpha: 1.0),
            subtitleColor: UIColor.darkGray,
            accentColor: UIColor.systemGreen,
            iconTintColor: UIColor.systemGreen
        )
        
        // Thème orange (pour état "en attente")
        themes["orange"] = ThemeConfig(
            name: "orange",
            backgroundColor: UIColor(red: 1.0, green: 0.95, blue: 0.88, alpha: 1.0),
            titleColor: UIColor(red: 0.90, green: 0.32, blue: 0.0, alpha: 1.0),
            subtitleColor: UIColor.darkGray,
            accentColor: UIColor.systemOrange,
            iconTintColor: UIColor.systemOrange
        )
        
        // Thème rouge (pour état "urgent")
        themes["red"] = ThemeConfig(
            name: "red",
            backgroundColor: UIColor(red: 1.0, green: 0.92, blue: 0.93, alpha: 1.0),
            titleColor: UIColor(red: 0.78, green: 0.16, blue: 0.16, alpha: 1.0),
            subtitleColor: UIColor.darkGray,
            accentColor: UIColor.systemRed,
            iconTintColor: UIColor.systemRed
        )
    }
    
    /**
     * Définit le thème actuel
     */
    func setTheme(_ themeName: String) -> Bool {
        guard themes[themeName] != nil else {
            print("ThunderBG: Theme not found: \(themeName)")
            return false
        }
        
        currentThemeName = themeName
        print("ThunderBG: Theme changed to: \(themeName)")
        return true
    }
    
    /**
     * Crée un nouveau thème personnalisé
     */
    func createTheme(_ themeName: String, config: ThemeConfig) -> Bool {
        guard !themeName.isEmpty else {
            print("ThunderBG: Theme name cannot be empty")
            return false
        }
        
        var newConfig = config
        newConfig.name = themeName
        themes[themeName] = newConfig
        
        print("ThunderBG: Custom theme created: \(themeName)")
        return true
    }
    
    /**
     * Obtient le thème actuel
     */
    func getCurrentTheme() -> ThemeConfig? {
        return themes[currentThemeName] ?? themes["default"]
    }
    
    /**
     * Obtient un thème par nom
     */
    func getTheme(_ themeName: String) -> ThemeConfig? {
        return themes[themeName]
    }
    
    /**
     * Obtient tous les thèmes disponibles
     */
    func getAllThemes() -> [String: ThemeConfig] {
        return themes
    }
    
    /**
     * Supprime un thème personnalisé
     */
    func removeTheme(_ themeName: String) -> Bool {
        let defaultThemes = ["default", "dark", "blue", "green", "orange", "red"]
        if defaultThemes.contains(themeName) {
            print("ThunderBG: Cannot remove default theme: \(themeName)")
            return false
        }
        
        if themes.removeValue(forKey: themeName) != nil {
            print("ThunderBG: Theme removed: \(themeName)")
            return true
        }
        return false
    }
}

/**
 * Configuration d'un thème
 */
struct ThemeConfig {
    var name: String
    var backgroundColor: UIColor?
    var titleColor: UIColor?
    var subtitleColor: UIColor?
    var accentColor: UIColor?
    var iconTintColor: UIColor?
    var timerColor: UIColor?
    var buttonBackgroundColor: UIColor?
    var buttonTextColor: UIColor?
    var fontSize: CGFloat?
    var fontFamily: String?
    
    /**
     * Convertit en dictionnaire pour le retour JS
     */
    func toDictionary() -> [String: Any] {
        var dict: [String: Any] = ["name": name]
        
        if let bg = backgroundColor {
            dict["backgroundColor"] = colorToHex(bg)
        }
        if let title = titleColor {
            dict["titleColor"] = colorToHex(title)
        }
        if let subtitle = subtitleColor {
            dict["subtitleColor"] = colorToHex(subtitle)
        }
        if let accent = accentColor {
            dict["accentColor"] = colorToHex(accent)
        }
        if let icon = iconTintColor {
            dict["iconTintColor"] = colorToHex(icon)
        }
        if let timer = timerColor {
            dict["timerColor"] = colorToHex(timer)
        }
        if let btnBg = buttonBackgroundColor {
            dict["buttonBackgroundColor"] = colorToHex(btnBg)
        }
        if let btnText = buttonTextColor {
            dict["buttonTextColor"] = colorToHex(btnText)
        }
        if let size = fontSize {
            dict["fontSize"] = Int(size)
        }
        if let family = fontFamily {
            dict["fontFamily"] = family
        }
        
        return dict
    }
    
    /**
     * Convertit une couleur UIColor en hexadécimal
     */
    private func colorToHex(_ color: UIColor) -> String {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0
        
        color.getRed(&red, green: &green, blue: &blue, alpha: &alpha)
        
        let r = Int(red * 255)
        let g = Int(green * 255)
        let b = Int(blue * 255)
        
        return String(format: "#%02X%02X%02X", r, g, b)
    }
    
    /**
     * Crée une configuration depuis un dictionnaire
     */
    static func fromDictionary(_ dict: [String: Any]) -> ThemeConfig? {
        guard let name = dict["name"] as? String else {
            return nil
        }
        
        var config = ThemeConfig(name: name)
        
        if let bg = dict["backgroundColor"] as? String {
            config.backgroundColor = hexToColor(bg)
        }
        if let title = dict["titleColor"] as? String {
            config.titleColor = hexToColor(title)
        }
        if let subtitle = dict["subtitleColor"] as? String {
            config.subtitleColor = hexToColor(subtitle)
        }
        if let accent = dict["accentColor"] as? String {
            config.accentColor = hexToColor(accent)
        }
        if let icon = dict["iconTintColor"] as? String {
            config.iconTintColor = hexToColor(icon)
        }
        if let timer = dict["timerColor"] as? String {
            config.timerColor = hexToColor(timer)
        }
        if let btnBg = dict["buttonBackgroundColor"] as? String {
            config.buttonBackgroundColor = hexToColor(btnBg)
        }
        if let btnText = dict["buttonTextColor"] as? String {
            config.buttonTextColor = hexToColor(btnText)
        }
        if let size = dict["fontSize"] as? Int {
            config.fontSize = CGFloat(size)
        }
        if let family = dict["fontFamily"] as? String {
            config.fontFamily = family
        }
        
        return config
    }
    
    /**
     * Convertit une chaîne hexadécimale en UIColor
     */
    private static func hexToColor(_ hex: String) -> UIColor? {
        var hexSanitized = hex.trimmingCharacters(in: .whitespacesAndNewlines)
        hexSanitized = hexSanitized.replacingOccurrences(of: "#", with: "")
        
        var rgb: UInt64 = 0
        
        guard Scanner(string: hexSanitized).scanHexInt64(&rgb) else {
            return nil
        }
        
        let r = CGFloat((rgb & 0xFF0000) >> 16) / 255.0
        let g = CGFloat((rgb & 0x00FF00) >> 8) / 255.0
        let b = CGFloat(rgb & 0x0000FF) / 255.0
        
        return UIColor(red: r, green: g, blue: b, alpha: 1.0)
    }
}

