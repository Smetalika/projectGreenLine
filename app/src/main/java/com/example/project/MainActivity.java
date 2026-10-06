package com.example.project;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton btnVK = findViewById(R.id.btnVK);
        ImageButton btnEmail = findViewById(R.id.btnEmail);

        if (btnVK != null) {
            btnVK.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/club238841344"));
                startActivity(intent);
            });
        }

        if (btnEmail != null) {
            btnEmail.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:nikmh53@gmail.com"));
                startActivity(intent);
            });
        }


        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        NowNotificstions.createNotificationChannel(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

// Для Android 13+ также запросите разрешение на уведомления
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
        }
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        Button btnSet = findViewById(R.id.butt1);
        btnSet.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LandmarkListActivity.class);
            startActivity(intent);
        });

        applyFontSizeToActivity();
        // В MainActivity.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

// Для Android 13+ также запросите разрешение на уведомления
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // При возврате на экран обновляем размер шрифта
        applyFontSizeToActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem fontSizeItem = menu.findItem(R.id.action_font_size);
        if (fontSizeItem != null) {
            fontSizeItem.setTitle("Размер шрифта");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_font_size) {
            showFontSizeDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFontSizeDialog() {
        final String[] sizes = {"12", "14", "16", "18", "20", "22", "24", "26", "28", "30"};
        final float[] sizeValues = {12f, 14f, 16f, 18f, 20f, 22f, 24f, 26f, 28f, 30f};

        float currentSize = FontSizeHelper.getFontSize(prefs);
        int selectedIndex = 0;
        for (int i = 0; i < sizeValues.length; i++) {
            if (sizeValues[i] == currentSize) {
                selectedIndex = i;
                break;
            }
        }

        new android.app.AlertDialog.Builder(this)
                .setTitle("Выберите размер шрифта")
                .setSingleChoiceItems(sizes, selectedIndex, (dialog, which) -> {
                    float newSize = sizeValues[which];
                    FontSizeHelper.saveFontSize(prefs, newSize);
                    dialog.dismiss();

                    // Применяем новый размер
                    applyFontSizeToActivity();

                    Toast.makeText(this, "Размер шрифта: " + (int) newSize, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void applyFontSizeToActivity() {
        if (prefs == null) return;
        float fontSize = FontSizeHelper.getFontSize(prefs);
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            FontSizeHelper.applyFontSizeToView(rootView, fontSize);
        }
    }
}