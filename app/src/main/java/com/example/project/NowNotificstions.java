package com.example.project;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NowNotificstions {
    private static final String CHANNEL_ID = "plans_channel";
    private static  final String CHANNEL_NAME = "Планы и напоминания";
    private static final int NOTIFICATION_ID = 1000;
    public static void createNotificationChannel(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Напоминания о запланированных мероприятиях");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if(manager != null){
                manager.createNotificationChannel(channel);
            }
        }
    }
    public static void scheduleNotification(Context context, String planName, String dateTime, int planIndex) {
        // Парсим дату и время из строки "2026-05-20 14:30"
        String[] dateTimeParts = dateTime.split(" ");
        String[] dateParts = dateTimeParts[0].split("-");
        String[] timeParts = dateTimeParts[1].split(":");

        Calendar eventCalendar = Calendar.getInstance();
        eventCalendar.set(Calendar.YEAR, Integer.parseInt(dateParts[0]));
        eventCalendar.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
        eventCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[2]));
        eventCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
        eventCalendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
        eventCalendar.set(Calendar.SECOND, 0);

        // Вычисляем время уведомления: за 4 часа до события
        Calendar notifyCalendar = (Calendar) eventCalendar.clone();
        notifyCalendar.add(Calendar.HOUR_OF_DAY, -4);

        // Если время уведомления уже прошло — не планируем
        if (notifyCalendar.getTimeInMillis() <= System.currentTimeMillis()) {
            Toast.makeText(context, "Время уведомления уже прошло", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создаём Intent для BroadcastReceiver
        Intent intent = new Intent(context, NotificationReceived.class);
        intent.putExtra("planName", planName);
        intent.putExtra("dateTime", dateTime);
        intent.putExtra("planIndex", planIndex);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                planIndex,  // Уникальный ID для каждого плана
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Настраиваем AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

// Для Android 12+ проверяем разрешение на точные будильники
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Разрешение не дано — показываем уведомление без точного времени
                Toast.makeText(context, "Для точных напоминаний дайте разрешение в настройках", Toast.LENGTH_LONG).show();
                return;
            }
        }

// Безопасный вызов с обработкой исключения
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        notifyCalendar.getTimeInMillis(),
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        notifyCalendar.getTimeInMillis(),
                        pendingIntent
                );
            }
        } catch (SecurityException e) {
            // Если разрешение всё равно не сработало — логируем и показываем сообщение
            e.printStackTrace();
            Toast.makeText(context, "Ошибка: нет разрешения на точные будильники", Toast.LENGTH_LONG).show();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        Toast.makeText(context, "Напоминание запланировано на " + sdf.format(notifyCalendar.getTime()), Toast.LENGTH_LONG).show();
        android.util.Log.d("NOTIFICATION", "Уведомление запланировано на " +
                new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(notifyCalendar.getTime()));
    }

    // Отмена уведомления для конкретного плана
    public static void cancelNotification(Context context, int planIndex) {
        Intent intent = new Intent(context, NotificationReceived.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                planIndex,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    // Показ самого уведомления (вызывается из BroadcastReceiver)
    public static void showNotification(Context context, String planName, String dateTime) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Форматируем дату для отображения
        String[] dateTimeParts = dateTime.split(" ");
        String[] dateParts = dateTimeParts[0].split("-");
        String formattedDate = dateParts[2] + "." + dateParts[1] + "." + dateParts[0] + " " + dateTimeParts[1];

        Intent intent = new Intent(context, PlansActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_agenda)
                .setContentTitle("🔔 Напоминание о плане")
                .setContentText("Через 4 часа: " + planName + " (" + formattedDate + ")")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
        android.util.Log.d("NOTIFICATION", "showNotification вызван для " + planName);
    }

}
