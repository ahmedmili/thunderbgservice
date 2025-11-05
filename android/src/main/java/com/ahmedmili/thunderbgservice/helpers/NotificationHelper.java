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
        RemoteViews views;
        if (customLayoutId != null) {
            views = new RemoteViews(context.getPackageName(), customLayoutId);
            if (titleViewId != null) views.setTextViewText(titleViewId, title != null ? title : "Online");
            if (subtitleViewId != null) views.setTextViewText(subtitleViewId, subtitle != null ? subtitle : "Running");
        } else {
            views = new RemoteViews(context.getPackageName(), defaultLayoutId);
            views.setTextViewText(defaultTitleId, title != null ? title : "Online");
            views.setTextViewText(defaultSubtitleId, subtitle != null ? subtitle : "Running");
        }
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()),
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID_FOREGROUND)
                .setSmallIcon(defaultIconId)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setCustomContentView(views)
                .setCustomBigContentView(views)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setShowWhen(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE);
        if (sounds) b.setDefaults(NotificationCompat.DEFAULT_ALL);
        return b.build();
    }
    public void updateNotification(String title, String subtitle) {
        updateNotification(title, subtitle, null);
    }

    public void updateNotification(String title, String subtitle, String timerText) {
        RemoteViews views;
        if (customLayoutId != null) {
            views = new RemoteViews(context.getPackageName(), customLayoutId);
            if (titleViewId != null && title != null) views.setTextViewText(titleViewId, title);
            if (subtitleViewId != null && subtitle != null) views.setTextViewText(subtitleViewId, subtitle);
            if (timerViewId != null && timerText != null) views.setTextViewText(timerViewId, timerText);
        } else {
            views = new RemoteViews(context.getPackageName(), defaultLayoutId);
            if (title != null) views.setTextViewText(defaultTitleId, title);
            if (subtitle != null) views.setTextViewText(defaultSubtitleId, subtitle);
        }
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID_FOREGROUND)
                .setSmallIcon(defaultIconId)
                .setCustomContentView(views)
                .setCustomBigContentView(views)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setShowWhen(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE);
        androidx.core.app.NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID_FOREGROUND, b.build());
    }
}

