package com.webify.thunderbgservice.core;

import static com.webify.thunderbgservice.core.FgConstants.*;

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

import com.webify.thunderbgservice.helpers.NotificationHelper;
import com.webify.thunderbgservice.helpers.LocationHelper;
import com.webify.thunderbgservice.tasks.BackgroundTask;
import com.webify.thunderbgservice.tasks.BackgroundTaskManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ForegroundTaskService extends Service {
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
                String title = intent.getStringExtra(EXTRA_TITLE); String subtitle = intent.getStringExtra(EXTRA_SUBTITLE);
                boolean enableLocation = intent.getBooleanExtra(EXTRA_ENABLE_LOCATION, true);
                boolean sounds = intent.getBooleanExtra(EXTRA_SOUNDS, false);
                // Optional custom layout
                String customLayout = intent.getStringExtra(EXTRA_CUSTOM_LAYOUT);
                String titleIdName = intent.getStringExtra(EXTRA_TITLE_VIEW_ID);
                String subtitleIdName = intent.getStringExtra(EXTRA_SUBTITLE_VIEW_ID);
                String timerIdName = intent.getStringExtra(EXTRA_TIMER_VIEW_ID);
                if (customLayout != null && !customLayout.isEmpty()) {
                    notificationHelper.setCustomLayout(customLayout, titleIdName, subtitleIdName, timerIdName);
                }
                startForegroundInternal(title, subtitle, sounds);
                if (enableLocation) locationHelper.start();
                startAtMillis = System.currentTimeMillis();
                startHeartbeat();
            } else if (ACTION_STOP.equals(action)) { 
                stopHeartbeat(); 
                BackgroundTaskManager.stopAll(this);
                stopForegroundInternal(); 
                locationHelper.stop(); 
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
                notificationHelper.updateNotification(intent.getStringExtra(EXTRA_TITLE), intent.getStringExtra(EXTRA_SUBTITLE));
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

    private void startForegroundInternal(String title, String subtitle, boolean sounds) { createChannel(); Notification n = notificationHelper.buildNotification(title, subtitle, sounds); startForeground(NOTIFICATION_ID_FOREGROUND, n); Log.i("ThunderBG","Service started in foreground"); }
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
            notificationHelper.updateNotification(null, "Running", clock);
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void stopHeartbeat() {
        try { if (scheduler != null) { scheduler.shutdownNow(); scheduler = null; } } catch (Exception ignored) {}
        heartbeat.set(0);
    }
}

