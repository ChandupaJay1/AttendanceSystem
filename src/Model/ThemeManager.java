package Model;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;

public class ThemeManager {
    private static final Map<String, Class<? extends FlatLaf>> THEMES = new HashMap<>();
    private static String currentTheme;
    
    static {
        // Register all available themes
        THEMES.put("Flat Light", FlatLightLaf.class);
        THEMES.put("Flat Dark", FlatDarkLaf.class);
        THEMES.put("IntelliJ Light", FlatIntelliJLaf.class);
        THEMES.put("IntelliJ Dark", FlatDarculaLaf.class);

        // Set default theme
        currentTheme = "IntelliJ Light";
    }
    
    public static String[] getAvailableThemes() {
        return THEMES.keySet().toArray(new String[0]);
    }
    
    public static String getCurrentTheme() {
        return currentTheme;
    }
    
    public static void setTheme(String themeName) {
        try {
            if (THEMES.containsKey(themeName)) {
                // Replace deprecated newInstance() with getDeclaredConstructor().newInstance()
                FlatLaf theme = THEMES.get(themeName).getDeclaredConstructor().newInstance();
                FlatLaf.setup(theme);
                currentTheme = themeName;
                updateAllFrames();
                saveThemePreference(themeName);
            }
        } catch (Exception ex) {
            System.err.println("Failed to set theme: " + themeName);
            ex.printStackTrace();
            // Fallback to default theme
            try {
                FlatLaf.setup(new FlatIntelliJLaf());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void initialize() {
        String savedTheme = loadThemePreference();
        if (savedTheme != null && THEMES.containsKey(savedTheme)) {
            setTheme(savedTheme);
        } else {
            setTheme(currentTheme);
        }
        configureGlobalThemeSettings();
    }
    
    private static void updateAllFrames() {
        for (Frame frame : Frame.getFrames()) {
            SwingUtilities.updateComponentTreeUI(frame);
        }
    }
    
    private static void saveThemePreference(String themeName) {
        Preferences.userRoot().put("theme_preference", themeName);
    }
    
    private static String loadThemePreference() {
        return Preferences.userRoot().get("theme_preference", currentTheme);
    }
    
    public static void configureGlobalThemeSettings() {
        try {
            // Smooth scrolling
            UIManager.put("ScrollBar.showButtons", true);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
            
            // Modern tables
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", true);
            UIManager.put("Table.gridColor", new Color(0xE0E0E0));
            
            // Modern text fields
            UIManager.put("TextComponent.arc", 5);
            
            // Buttons
            UIManager.put("Button.arc", 5);
            UIManager.put("Component.focusWidth", 1);
            
            // Default font
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
        } catch (Exception e) {
            System.err.println("Failed to configure theme settings");
            e.printStackTrace();
        }
    }
}