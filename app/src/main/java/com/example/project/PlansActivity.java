package com.example.project;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PlansActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<PlanItem> plansList = new ArrayList<>();
    private PlansAdapter adapter;
    private File plansFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Мои планы");
        }

        recyclerView = findViewById(R.id.recyclerViewPlans);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Получаем путь к файлу планов
        File dir = getExternalFilesDir(null);
        plansFile = new File(dir, "plans.txt");

        loadPlansFromFile();
        adapter = new PlansAdapter(plansList, this::deletePlan);
        recyclerView.setAdapter(adapter);
    }

    private void loadPlansFromFile() {
        plansList.clear();
        if (!plansFile.exists()) return;

        Calendar now = Calendar.getInstance();
        boolean hasExpired = false;

        try (FileInputStream fin = new FileInputStream(plansFile);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fin))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    String dateTimeStr = parts[1];

                    // Проверяем, не просрочен ли план
                    if (isExpired(dateTimeStr, now)) {
                        hasExpired = true;
                        // Отменяем уведомление для просроченного плана
                        // (нужно знать индекс, поэтому пока просто помечаем)
                        continue; // пропускаем добавление просроченного плана
                    }
                    plansList.add(new PlanItem(parts[0], parts[1]));
                }
            }

            // Если были просроченные планы — пересохраняем файл
            if (hasExpired) {
                savePlansToFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean isExpired(String dateTimeStr, Calendar now) {
        try {
            // Парсим дату и время "2026-05-20 14:30"
            String[] parts = dateTimeStr.split(" ");
            String[] dateParts = parts[0].split("-");
            String[] timeParts = parts[1].split(":");

            Calendar eventCalendar = Calendar.getInstance();
            eventCalendar.set(Calendar.YEAR, Integer.parseInt(dateParts[0]));
            eventCalendar.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
            eventCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[2]));
            eventCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
            eventCalendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
            eventCalendar.set(Calendar.SECOND, 0);

            // Если время события уже прошло
            return eventCalendar.getTimeInMillis() <= now.getTimeInMillis();
        } catch (Exception e) {
            return true; // при ошибке парсинга считаем просроченным
        }
    }
    private void savePlansToFile() {
        try (FileOutputStream fos = new FileOutputStream(plansFile, false)) {
            for (int i = 0; i < plansList.size(); i++) {
                PlanItem plan = plansList.get(i);
                String line = plan.getName() + "|" + plan.getDateTime() + "\n";
                fos.write(line.getBytes());

                // Перепланируем уведомления для оставшихся планов с правильными индексами
                NowNotificstions.cancelNotification(this, i);
                NowNotificstions.scheduleNotification(this, plan.getName(), plan.getDateTime(), i);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    private void deletePlan(int position) {
        NowNotificstions.cancelNotification(this, position);
        PlanItem removed = plansList.remove(position);
        savePlansToFile();
        adapter.notifyItemRemoved(position);
        Toast.makeText(this, "Удалено: " + removed.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlansFromFile();  // теперь здесь же происходит удаление просроченных
        adapter.notifyDataSetChanged();

        if (plansList.isEmpty()) {
            findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            findViewById(R.id.emptyView).setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Модель данных для плана
    static class PlanItem {
        private String name;
        private String dateTime;

        PlanItem(String name, String dateTime) {
            this.name = name;
            this.dateTime = dateTime;
        }

        String getName() { return name; }
        String getDateTime() { return dateTime; }
    }
    private void scheduleAllNotifications() {
        for (int i = 0; i < plansList.size(); i++) {
            PlanItem plan = plansList.get(i);
            NowNotificstions.scheduleNotification(this, plan.getName(), plan.getDateTime(), i);
        }
    }

}