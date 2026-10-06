package com.example.project;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class DetailFragment extends Fragment {

    private TextView descriptionText;
    private Button openWebsiteButton;
    private Button addToPlanButton;
    private ImageView detailsImage;
    private int currentImageResource;
    private String currentUrl = "";
    private String currentDescription = "";
    private String currentName = "";
    private String curmapUrl = "";

    // ✅ ИСПРАВЛЕННЫЙ МЕТОД с imageResource
    public static DetailFragment newInstance(String name, String url, String description, int imageResource, String mapUrl) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("url", url);
        args.putString("description", description);
        args.putInt("imageResource", imageResource);
        args.putString("mapUrl", mapUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // ⭐ ПОЛУЧАЕМ АРГУМЕНТЫ
        Bundle args = getArguments();
        if (args != null) {
            currentName = args.getString("name", "");
            currentUrl = args.getString("url", "");
            currentDescription = args.getString("description", "Не выбрано");
            currentImageResource = args.getInt("imageResource", R.drawable.galery);
            curmapUrl = args.getString("mapUrl", "");
        }

        // Находим все View
        detailsImage = view.findViewById(R.id.detailImage);
        descriptionText = view.findViewById(R.id.detailsText);
        openWebsiteButton = view.findViewById(R.id.openWebsiteButton);
        addToPlanButton = view.findViewById(R.id.addToPlanButton);
        Button showOnMapButton = view.findViewById(R.id.showOnMapButton);
        TextView detailTitle = view.findViewById(R.id.detailTitle);

        // Устанавливаем данные
        if (detailsImage != null) {
            detailsImage.setImageResource(currentImageResource);
        }
        descriptionText.setText(currentDescription);
        detailTitle.setText(currentName);

        // Настройка кнопки "Показать на карте"
        if (showOnMapButton != null) {
            showOnMapButton.setOnClickListener(v -> {
                if (curmapUrl != null && !curmapUrl.isEmpty()) {
                    openMap(curmapUrl);
                } else {
                    Toast.makeText(getContext(), "Ссылка на карту отсутствует", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Применяем размер шрифта
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        float fontSize = FontSizeHelper.getFontSize(prefs);
        descriptionText.setTextSize(fontSize);
        openWebsiteButton.setTextSize(fontSize);
        addToPlanButton.setTextSize(fontSize);
        detailTitle.setTextSize(fontSize + 4);  // заголовок чуть крупнее

        // Кнопка "Перейти на сайт"
        openWebsiteButton.setOnClickListener(v -> {
            if (currentUrl != null && !currentUrl.isEmpty()) {
                openWebsite(currentUrl);
            }
        });

        // Кнопка "В планы"
        addToPlanButton.setOnClickListener(v -> {
            showDateTimePicker();
        });

        return view;
    }
    private void openMap(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(mapIntent);
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePicker = new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) -> {
            TimePickerDialog timePicker = new TimePickerDialog(getContext(), (view2, hourOfDay, minute1) -> {
                String dateTime = String.format("%04d-%02d-%02d %02d:%02d", year1, month1+1, dayOfMonth, hourOfDay, minute1);
                savePlanToFile(dateTime);
            }, hour, minute, true);
            timePicker.show();
        }, year, month, day);
        datePicker.show();
    }

    private void savePlanToFile(String dateTime) {
        if (currentName == null || currentName.isEmpty()) {
            Toast.makeText(getContext(), "Ошибка: название не передано", Toast.LENGTH_SHORT).show();
            return;
        }

        String record = currentName + "|" + dateTime + "\n";

        try {
            File dir = requireContext().getExternalFilesDir(null);
            if (dir == null) {
                Toast.makeText(getContext(), "Ошибка: внешнее хранилище недоступно", Toast.LENGTH_SHORT).show();
                return;
            }
            File file = new File(dir, "plans.txt");
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(record.getBytes());
            fos.close();

            int newPlanIndex = getPlansCount() - 1;
            NowNotificstions.scheduleNotification(getContext(), currentName, dateTime, newPlanIndex);

            Toast.makeText(getContext(), "План добавлен: " + currentName + " на " + dateTime, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Ошибка сохранения: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private int getPlansCount() {
        File dir = requireContext().getExternalFilesDir(null);
        File file = new File(dir, "plans.txt");
        if (!file.exists()) return 0;

        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(file))) {
            int count = 0;
            while (reader.readLine() != null) count++;
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    private void openWebsite(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}