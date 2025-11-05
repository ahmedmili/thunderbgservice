package com.ahmedmili.thunderbgservice.core;

import static com.ahmedmili.thunderbgservice.core.FgConstants.*;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.ahmedmili.thunderbgservice.helpers.NotificationHelper;
import com.ahmedmili.thunderbgservice.helpers.LocationHelper;
import com.ahmedmili.thunderbgservice.tasks.BackgroundTask;
import com.ahmedmili.thunderbgservice.tasks.BackgroundTaskManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ForegroundTaskService extends Service {
    private static final String PREFS_SERVICE = "thunder_bg_service_state";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";
    private static final String KEY_ENABLE_LOCATION = "enable_location";
    private static final String KEY_SOUNDS = "sounds";
    private static final String KEY_CUSTOM_LAYOUT = "custom_layout";
    private static final String KEY_TITLE_VIEW_ID = "title_view_id";
    private static final String KEY_SUBTITLE_VIEW_ID = "subtitle_view_id";
    private static final String KEY_TIMER_VIEW_ID = "timer_view_id";
    private static final String KEY_START_AT = "start_at_millis";
    private static final String KEY_IS_RUNNING = "is_running";
    private static final String KEY_VIEW_DATA_JSON = "view_data_json";
    private static final String KEY_BUTTONS_JSON = "buttons_json";
    public static void startAction(Context context, String action, Intent extras) {
        Intent i = new Intent(context, ForegroundTaskService.class);
        i.setAction(action);
        if (extras != null) i.putExtras(extras);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, i);
        } else {
            context.startService(i);
        }
    }

    private NotificationHelper notificationHelper; private LocationHelper locationHelper;
    private ScheduledExecutorService scheduler; private final AtomicInteger heartbeat = new AtomicInteger(0);
    private volatile long startAtMillis = 0L;

    @Override public void onCreate() { super.onCreate(); notificationHelper = new NotificationHelper(this); locationHelper = new LocationHelper(this); Log.i("ThunderBG","Service created"); }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                // Charger l'état si extras absents
                android.content.SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_SERVICE, Context.MODE_PRIVATE);
                String title = intent.hasExtra(EXTRA_TITLE) ? intent.getStringExtra(EXTRA_TITLE) : prefs.getString(KEY_TITLE, null);
                String subtitle = intent.hasExtra(EXTRA_SUBTITLE) ? intent.getStringExtra(EXTRA_SUBTITLE) : prefs.getString(KEY_SUBTITLE, null);
                boolean enableLocation = intent.hasExtra(EXTRA_ENABLE_LOCATION) ? intent.getBooleanExtra(EXTRA_ENABLE_LOCATION, true) : prefs.getBoolean(KEY_ENABLE_LOCATION, true);
                boolean sounds = intent.hasExtra(EXTRA_SOUNDS) ? intent.getBooleanExtra(EXTRA_SOUNDS, false) : prefs.getBoolean(KEY_SOUNDS, false);
                String customLayout = intent.hasExtra(EXTRA_CUSTOM_LAYOUT) ? intent.getStringExtra(EXTRA_CUSTOM_LAYOUT) : prefs.getString(KEY_CUSTOM_LAYOUT, null);
                String titleIdName = intent.hasExtra(EXTRA_TITLE_VIEW_ID) ? intent.getStringExtra(EXTRA_TITLE_VIEW_ID) : prefs.getString(KEY_TITLE_VIEW_ID, null);
                String subtitleIdName = intent.hasExtra(EXTRA_SUBTITLE_VIEW_ID) ? intent.getStringExtra(EXTRA_SUBTITLE_VIEW_ID) : prefs.getString(KEY_SUBTITLE_VIEW_ID, null);
                String timerIdName = intent.hasExtra(EXTRA_TIMER_VIEW_ID) ? intent.getStringExtra(EXTRA_TIMER_VIEW_ID) : prefs.getString(KEY_TIMER_VIEW_ID, null);

                if (customLayout != null && !customLayout.isEmpty()) {
                    notificationHelper.setCustomLayout(customLayout, titleIdName, subtitleIdName, timerIdName);
                }
                String viewDataJson = intent.hasExtra(EXTRA_VIEW_DATA_JSON) ? intent.getStringExtra(EXTRA_VIEW_DATA_JSON) : prefs.getString(KEY_VIEW_DATA_JSON, null);
                String buttonsJson = intent.hasExtra(EXTRA_BUTTONS_JSON) ? intent.getStringExtra(EXTRA_BUTTONS_JSON) : prefs.getString(KEY_BUTTONS_JSON, null);
                startForegroundInternal(title, subtitle, sounds, viewDataJson, buttonsJson);
                if (enableLocation) locationHelper.start();

                long savedStart = prefs.getLong(KEY_START_AT, 0L);
                if (savedStart > 0L) {
                    startAtMillis = savedStart;
                } else {
                    startAtMillis = System.currentTimeMillis();
                    prefs.edit().putLong(KEY_START_AT, startAtMillis).apply();
                }
                // Sauvegarder l'état courant
                prefs.edit()
                        .putString(KEY_TITLE, title)
                        .putString(KEY_SUBTITLE, subtitle)
                        .putBoolean(KEY_ENABLE_LOCATION, enableLocation)
                        .putBoolean(KEY_SOUNDS, sounds)
                        .putString(KEY_CUSTOM_LAYOUT, customLayout)
                        .putString(KEY_TITLE_VIEW_ID, titleIdName)
                        .putString(KEY_SUBTITLE_VIEW_ID, subtitleIdName)
                        .putString(KEY_TIMER_VIEW_ID, timerIdName)
                        .putBoolean(KEY_IS_RUNNING, true)
                        .putString(KEY_VIEW_DATA_JSON, viewDataJson)
                        .putString(KEY_BUTTONS_JSON, buttonsJson)
                        .apply();

                // Réenregistrer les tâches persistées
                try {
                    java.util.Map<String, com.ahmedmili.thunderbgservice.tasks.BackgroundTaskManager.TaskConfig> all = com.ahmedmili.thunderbgservice.tasks.BackgroundTaskManager.getAllTaskConfigs(this);
                    for (java.util.Map.Entry<String, com.ahmedmili.thunderbgservice.tasks.BackgroundTaskManager.TaskConfig> e : all.entrySet()) {
                        String taskId = e.getKey();
                        com.ahmedmili.thunderbgservice.tasks.BackgroundTaskManager.TaskConfig cfg = e.getValue();
                        try {
                            Class<?> clazz = Class.forName(cfg.className);
                            BackgroundTask task = (BackgroundTask) clazz.getDeclaredConstructor().newInstance();
                            BackgroundTaskManager.registerTask(this, taskId, task, cfg.intervalMs);
                            Log.i("ThunderBG", "Task restored: " + taskId);
                        } catch (Exception ex) {
                            Log.e("ThunderBG", "Failed to restore task: " + taskId, ex);
                        }
                    }
                } catch (Exception ex) {
                    Log.e("ThunderBG", "Error restoring tasks", ex);
                }

                startHeartbeat();
            } else if (ACTION_STOP.equals(action)) { 
                stopHeartbeat(); 
                BackgroundTaskManager.stopAll(this);
                stopForegroundInternal(); 
                locationHelper.stop(); 
                // Nettoyer l'état
                android.content.SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_SERVICE, Context.MODE_PRIVATE);
                prefs.edit().clear().apply();
            }
            else if (ACTION_UPDATE.equals(action)) {
                // Optional: change layout dynamically
                String customLayout = intent.getStringExtra(EXTRA_CUSTOM_LAYOUT);
                String titleIdName = intent.getStringExtra(EXTRA_TITLE_VIEW_ID);
                String subtitleIdName = intent.getStringExtra(EXTRA_SUBTITLE_VIEW_ID);
                String timerIdName = intent.getStringExtra(EXTRA_TIMER_VIEW_ID);
                if (customLayout != null && !customLayout.isEmpty()) {
                    notificationHelper.setCustomLayout(customLayout, titleIdName, subtitleIdName, timerIdName);
                    Log.i("ThunderBG", "Layout changed to: " + customLayout);
                }
                String viewDataJson = intent.getStringExtra(EXTRA_VIEW_DATA_JSON);
                String buttonsJson = intent.getStringExtra(EXTRA_BUTTONS_JSON);
                notificationHelper.updateNotification(intent.getStringExtra(EXTRA_TITLE), intent.getStringExtra(EXTRA_SUBTITLE), null, viewDataJson, buttonsJson);
                // Persister la mise à jour partielle
                android.content.SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_SERVICE, Context.MODE_PRIVATE);
                android.content.SharedPreferences.Editor ed = prefs.edit();
                if (intent.hasExtra(EXTRA_TITLE)) ed.putString(KEY_TITLE, intent.getStringExtra(EXTRA_TITLE));
                if (intent.hasExtra(EXTRA_SUBTITLE)) ed.putString(KEY_SUBTITLE, intent.getStringExtra(EXTRA_SUBTITLE));
                if (customLayout != null) ed.putString(KEY_CUSTOM_LAYOUT, customLayout);
                if (titleIdName != null) ed.putString(KEY_TITLE_VIEW_ID, titleIdName);
                if (subtitleIdName != null) ed.putString(KEY_SUBTITLE_VIEW_ID, subtitleIdName);
                if (timerIdName != null) ed.putString(KEY_TIMER_VIEW_ID, timerIdName);
                if (viewDataJson != null) ed.putString(KEY_VIEW_DATA_JSON, viewDataJson);
                if (buttonsJson != null) ed.putString(KEY_BUTTONS_JSON, buttonsJson);
                ed.apply();
            }
            else if (ACTION_REGISTER_TASK.equals(action)) {
                String taskId = intent.getStringExtra(EXTRA_TASK_ID);
                String taskClass = intent.getStringExtra(EXTRA_TASK_CLASS);
                long interval = intent.getLongExtra(EXTRA_TASK_INTERVAL, 5000);
                
                if (taskId != null && taskClass != null) {
                    try {
                        Class<?> clazz = Class.forName(taskClass);
                        BackgroundTask task = (BackgroundTask) clazz.getDeclaredConstructor().newInstance();
                        BackgroundTaskManager.registerTask(this, taskId, task, interval);
                        Log.i("ThunderBG", "Task registered: " + taskId + " (class: " + taskClass + ", interval: " + interval + "ms)");
                    } catch (Exception e) {
                        Log.e("ThunderBG", "Failed to register task: " + taskId, e);
                    }
                }
            }
            else if (ACTION_UNREGISTER_TASK.equals(action)) {
                String taskId = intent.getStringExtra(EXTRA_TASK_ID);
                if (taskId != null) {
                    BackgroundTaskManager.unregisterTask(this, taskId);
                    Log.i("ThunderBG", "Task unregistered: " + taskId);
                }
            }
        }
        return START_STICKY;
    }

    @Override public void onTaskRemoved(Intent rootIntent) {
        // Redémarre le service après un swipe-kill du task
        Intent restartIntent = new Intent(getApplicationContext(), ForegroundTaskService.class);
        restartIntent.setAction(ACTION_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(getApplicationContext(), restartIntent);
        } else {
            getApplicationContext().startService(restartIntent);
        }
        super.onTaskRemoved(rootIntent);
    }

    @Override public void onDestroy() { stopHeartbeat(); super.onDestroy(); }

    @Override public IBinder onBind(Intent intent) { return null; }

    private void startForegroundInternal(String title, String subtitle, boolean sounds, String viewDataJson, String buttonsJson) { createChannel(); Notification n = notificationHelper.buildNotification(title, subtitle, sounds, viewDataJson, buttonsJson); startForeground(NOTIFICATION_ID_FOREGROUND, n); Log.i("ThunderBG","Service started in foreground"); }
    private void stopForegroundInternal() { Log.i("ThunderBG","Service stopping"); stopForeground(true); stopSelf(); }
    private void createChannel() { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { NotificationChannel c = new NotificationChannel(CHANNEL_ID_FOREGROUND, "Thunder BG", NotificationManager.IMPORTANCE_LOW); c.enableLights(false); c.enableVibration(false); c.setLightColor(Color.BLUE); ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(c);} }

    private String formatElapsed(long elapsedMs) {
        long totalSeconds = elapsedMs / 1000L;
        long hours = totalSeconds / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        long seconds = totalSeconds % 60L;
        String hh = hours < 10 ? "0" + hours : String.valueOf(hours);
        String mm = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
        String ss = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
        return hh + ":" + mm + ":" + ss;
    }

    private void startHeartbeat() {
        if (scheduler != null) return;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            int n = heartbeat.incrementAndGet();
            long now = System.currentTimeMillis();
            long elapsed = startAtMillis > 0 ? (now - startAtMillis) : 0L;
            String clock = formatElapsed(elapsed);
            Log.i("ThunderBG", "Alive #" + n + " • " + clock);
            android.content.SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_SERVICE, Context.MODE_PRIVATE);
            String viewDataJson = prefs.getString(KEY_VIEW_DATA_JSON, null);
            String buttonsJson = prefs.getString(KEY_BUTTONS_JSON, null);
            notificationHelper.updateNotification(null, null, clock, viewDataJson, buttonsJson);
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void stopHeartbeat() {
        try { if (scheduler != null) { scheduler.shutdownNow(); scheduler = null; } } catch (Exception ignored) {}
        heartbeat.set(0);
    }
}

