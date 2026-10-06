package com.example.project;

import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FontSizeHelper {

    private static final String PREF_FONT_SIZE = "font_size";

    // Сохранить размер шрифта
    public static void saveFontSize(SharedPreferences prefs, float size) {
        prefs.edit().putFloat(PREF_FONT_SIZE, size).apply();
    }

    // Получить сохранённый размер шрифта
    public static float getFontSize(SharedPreferences prefs) {
        return prefs.getFloat(PREF_FONT_SIZE, 16f);
    }

    // Применить размер шрифта ко всем текстовым элементам в View
    public static void applyFontSizeToView(View view, float textSize) {
        if (view == null) return;

        if (view instanceof TextView) {
            ((TextView) view).setTextSize(textSize);
        } else if (view instanceof Button) {
            ((Button) view).setTextSize(textSize);
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyFontSizeToView(group.getChildAt(i), textSize);
            }
        }
    }
}