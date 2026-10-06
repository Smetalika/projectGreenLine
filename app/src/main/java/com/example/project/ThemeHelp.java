package com.example.project;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelp {

    private static final String PREF_THEME = "theme_pref";
    private static final String THEME_DARK = "dark";
    private static final String THEME_LIGHT = "light";

    // Сохраняем выбранную тему
    public static void saveTheme(SharedPreferences prefs, boolean isDark) {
        prefs.edit().putString(PREF_THEME, isDark ? THEME_DARK : THEME_LIGHT).apply();
    }

    // Применяем тему при запуске
    public static void applyTheme(SharedPreferences prefs) {
        String theme = prefs.getString(PREF_THEME, THEME_LIGHT);
        if (theme.equals(THEME_DARK)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}