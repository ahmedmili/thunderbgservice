package com.ahmedmili.thunderbgservice.helpers;

import static com.ahmedmili.thunderbgservice.core.FgConstants.*;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private final Context context;
    private Integer customLayoutId = null;
    private Integer titleViewId = null;
    private Integer subtitleViewId = null;
    private Integer timerViewId = null;
    
    // IDs par défaut pour le layout du plugin (résolus dynamiquement)
    private Integer defaultLayoutId = null;
    private Integer defaultTitleId = null;
    private Integer defaultSubtitleId = null;
    private Integer defaultIconId = null;

    public NotificationHelper(Context ctx) { 
        this.context = ctx.getApplicationContext();
        // Résoudre les IDs par défaut une fois
        resolveDefaultResourceIds();
    }
    
    private void resolveDefaultResourceIds() {
        String pkg = context.getPackageName();
        String pluginPkg = "com.ahmedmili.thunderbgservice"; // Package du plugin
        
        // Essayer d'abord dans le package du plugin (si les ressources sont copiées)
        defaultLayoutId = context.getResources().getIdentifier("notification_foreground", "layout", pluginPkg);
        defaultTitleId = context.getResources().getIdentifier("title", "id", pluginPkg);
        defaultSubtitleId = context.getResources().getIdentifier("subtitle", "id", pluginPkg);
        defaultIconId = context.getResources().getIdentifier("ic_notification", "drawable", pluginPkg);
        
        // Si pas trouvé dans le plugin, essayer dans l'app hôte
        if (defaultLayoutId == 0) {
            defaultLayoutId = context.getResources().getIdentifier("notification_foreground", "layout", pkg);
        }
        if (defaultTitleId == 0) {
            defaultTitleId = context.getResources().getIdentifier("title", "id", pkg);
        }
        if (defaultSubtitleId == 0) {
            defaultSubtitleId = context.getResources().getIdentifier("subtitle", "id", pkg);
        }
        if (defaultIconId == 0) {
            defaultIconId = context.getResources().getIdentifier("ic_notification", "drawable", pkg);
        }
        
        // Si toujours pas trouvé, utiliser des fallbacks Android système
        if (defaultLayoutId == 0) {
            defaultLayoutId = android.R.layout.simple_list_item_1; // Fallback
        }
        if (defaultTitleId == 0) {
            defaultTitleId = android.R.id.text1; // Fallback
        }
        if (defaultSubtitleId == 0) {
            defaultSubtitleId = android.R.id.text2; // Fallback
        }
        if (defaultIconId == 0) {
            defaultIconId = android.R.drawable.ic_dialog_info; // Fallback
        }
    }

    public void setCustomLayout(String layoutName, String titleViewIdName, String subtitleViewIdName, String timerViewIdName) {
        String pkg = context.getPackageName();
        int layoutId = context.getResources().getIdentifier(layoutName, "layout", pkg);
        int tId = titleViewIdName != null ? context.getResources().getIdentifier(titleViewIdName, "id", pkg) : 0;
        int sId = subtitleViewIdName != null ? context.getResources().getIdentifier(subtitleViewIdName, "id", pkg) : 0;
        int tmId = timerViewIdName != null ? context.getResources().getIdentifier(timerViewIdName, "id", pkg) : 0;
        this.customLayoutId = layoutId != 0 ? layoutId : null;
        this.titleViewId = tId != 0 ? tId : null;
        this.subtitleViewId = sId != 0 ? sId : null;
        this.timerViewId = tmId != 0 ? tmId : null;
        Log.i("ThunderBG", "setCustomLayout layout=" + layoutName + " -> id=" + this.customLayoutId + ", titleId=" + this.titleViewId + ", subtitleId=" + this.subtitleViewId + ", timerId=" + this.timerViewId);
    }

    public Notification buildNotification(String title, String subtitle, boolean sounds) {
        RemoteViews views = null;
        if (customLayoutId != null) {
            views = new RemoteViews(context.getPackageName(), customLayoutId);
            if (titleViewId != null && title != null) views.setTextViewText(titleViewId, title);
            if (subtitleViewId != null && subtitle != null) views.setTextViewText(subtitleViewId, subtitle);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()),
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID_FOREGROUND)
                .setSmallIcon(defaultIconId)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setShowWhen(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE);
        if (views != null) {
            b.setCustomContentView(views).setCustomBigContentView(views).setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        }
        if (sounds) b.setDefaults(NotificationCompat.DEFAULT_ALL);
        return b.build();
    }
    
    public Notification buildNotification(String title, String subtitle, boolean sounds, String viewDataJson, String buttonsJson) {
        RemoteViews views = null;
        if (customLayoutId != null) {
            views = new RemoteViews(context.getPackageName(), customLayoutId);
        }
        // Apply base fields
        if (views != null) {
            if (titleViewId != null && title != null) views.setTextViewText(titleViewId, title);
            if (subtitleViewId != null && subtitle != null) views.setTextViewText(subtitleViewId, subtitle);
        }
        // Dynamic bindings
        if (views != null) applyDynamicBindings(views, viewDataJson, buttonsJson);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()),
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID_FOREGROUND)
                .setSmallIcon(defaultIconId)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setShowWhen(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE);
        if (views != null) {
            b.setCustomContentView(views).setCustomBigContentView(views).setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        }
        if (sounds) b.setDefaults(NotificationCompat.DEFAULT_ALL);
        return b.build();
    }
    public void updateNotification(String title, String subtitle) {
        updateNotification(title, subtitle, null);
    }

    public void updateNotification(String title, String subtitle, String timerText) {
        RemoteViews views = null;
        if (customLayoutId != null) {
            views = new RemoteViews(context.getPackageName(), customLayoutId);
            if (titleViewId != null && title != null) views.setTextViewText(titleViewId, title);
            if (subtitleViewId != null && subtitle != null) views.setTextViewText(subtitleViewId, subtitle);
            if (timerViewId != null && timerText != null) views.setTextViewText(timerViewId, timerText);
        }
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID_FOREGROUND)
                .setSmallIcon(defaultIconId)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setShowWhen(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE);
        if (views != null) {
            b.setCustomContentView(views).setCustomBigContentView(views).setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        }
        androidx.core.app.NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID_FOREGROUND, b.build());
    }

    public void updateNotification(String title, String subtitle, String timerText, String viewDataJson, String buttonsJson) {
        RemoteViews views = null;
        if (customLayoutId != null) {
            views = new RemoteViews(context.getPackageName(), customLayoutId);
        }
        if (views != null) {
            if (titleViewId != null && title != null) views.setTextViewText(titleViewId, title);
            if (subtitleViewId != null && subtitle != null) views.setTextViewText(subtitleViewId, subtitle);
            if (timerViewId != null && timerText != null) views.setTextViewText(timerViewId, timerText);
            applyDynamicBindings(views, viewDataJson, buttonsJson);
        }

        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID_FOREGROUND)
                .setSmallIcon(defaultIconId)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setShowWhen(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE);
        if (views != null) {
            b.setCustomContentView(views).setCustomBigContentView(views).setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        }
        androidx.core.app.NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID_FOREGROUND, b.build());
    }

    private void applyDynamicBindings(RemoteViews views, String viewDataJson, String buttonsJson) {
        String pkg = context.getPackageName();
        // Text bindings
        if (viewDataJson != null && !viewDataJson.isEmpty()) {
            try {
                org.json.JSONObject obj = new org.json.JSONObject(viewDataJson);
                java.util.Iterator<String> keys = obj.keys();
                while (keys.hasNext()) {
                    String viewIdName = keys.next();
                    int id = context.getResources().getIdentifier(viewIdName, "id", pkg);
                    if (id != 0) {
                        String text = String.valueOf(obj.get(viewIdName));
                        views.setTextViewText(id, text);
                        Log.d("ThunderBG", "Set text viewData[" + viewIdName + "]=" + text + " (id=" + id + ")");
                    } else {
                        Log.w("ThunderBG", "View ID not found: " + viewIdName + " in package " + pkg);
                    }
                }
            } catch (Exception e) {
                Log.w("ThunderBG", "Failed parsing viewDataJson", e);
            }
        }
        // Button bindings
        if (buttonsJson != null && !buttonsJson.isEmpty()) {
            try {
                org.json.JSONArray arr = new org.json.JSONArray(buttonsJson);
                Log.d("ThunderBG", "Processing " + arr.length() + " buttons");
                for (int i = 0; i < arr.length(); i++) {
                    org.json.JSONObject btn = arr.getJSONObject(i);
                    String viewIdName = btn.optString("viewId", null);
                    String action = btn.optString("action", null);
                    org.json.JSONObject extras = btn.optJSONObject("extras");
                    if (viewIdName == null || action == null) {
                        Log.w("ThunderBG", "Button missing viewId or action: viewId=" + viewIdName + ", action=" + action);
                        continue;
                    }
                    int vid = context.getResources().getIdentifier(viewIdName, "id", pkg);
                    if (vid == 0) {
                        Log.e("ThunderBG", "Button view ID NOT FOUND: " + viewIdName + " in package " + pkg + ". Check your XML layout.");
                        continue;
                    }
                    // Find receiver explicitly for better reliability
                    android.content.pm.PackageManager pm = context.getPackageManager();
                    android.content.Intent testIntent = new android.content.Intent(action);
                    testIntent.setPackage(pkg);
                    java.util.List<android.content.pm.ResolveInfo> receivers = pm.queryBroadcastReceivers(testIntent, 0);
                    
                    android.content.Intent intent;
                    if (receivers != null && !receivers.isEmpty()) {
                        // Use explicit component (more reliable than setPackage)
                        android.content.pm.ResolveInfo ri = receivers.get(0);
                        android.content.ComponentName component = new android.content.ComponentName(ri.activityInfo.packageName, ri.activityInfo.name);
                        intent = new android.content.Intent(action).setComponent(component);
                        Log.d("ThunderBG", "✅ Using explicit component: " + component.toString());
                    } else {
                        // Fallback: use setPackage (less reliable but better than nothing)
                        Log.e("ThunderBG", "⚠️ NO RECEIVER FOUND for action=" + action + " in package=" + pkg + ". Using setPackage fallback. Check AndroidManifest.xml!");
                        intent = new android.content.Intent(action);
                        intent.setPackage(pkg);
                    }
                    
                    if (extras != null) {
                        java.util.Iterator<String> k = extras.keys();
                        while (k.hasNext()) {
                            String key = k.next();
                            intent.putExtra(key, String.valueOf(extras.get(key)));
                        }
                    }
                    
                    // Use unique requestCode per button (hash of viewIdName + action)
                    int requestCode = (viewIdName + action).hashCode();
                    // Ensure requestCode is positive (PendingIntent requirement)
                    if (requestCode < 0) requestCode = Math.abs(requestCode);
                    
                    // Create PendingIntent with correct flags
                    PendingIntent pi = PendingIntent.getBroadcast(
                        context, 
                        requestCode, 
                        intent, 
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
                    );
                    
                    views.setOnClickPendingIntent(vid, pi);
                    Log.i("ThunderBG", "Button bound: viewId=" + viewIdName + " (id=" + vid + ") -> action=" + action + " (requestCode=" + requestCode + ", component=" + intent.getComponent() + ")");
                }
            } catch (Exception e) {
                Log.e("ThunderBG", "Failed parsing buttonsJson: " + buttonsJson, e);
            }
        }
    }
}

