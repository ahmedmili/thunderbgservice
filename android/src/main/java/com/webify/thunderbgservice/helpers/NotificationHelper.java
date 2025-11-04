package com.webify.thunderbgservice.helpers;

import static com.webify.thunderbgservice.core.FgConstants.*;

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

    public NotificationHelper(Context ctx) { this.context = ctx.getApplicationContext(); }

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
            views = new RemoteViews(context.getPackageName(), R.layout.notification_foreground);
            views.setTextViewText(R.id.title, title != null ? title : "Online");
            views.setTextViewText(R.id.subtitle, subtitle != null ? subtitle : "Running");
        }
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()),
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID_FOREGROUND)
                .setSmallIcon(R.drawable.ic_notification)
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
            views = new RemoteViews(context.getPackageName(), R.layout.notification_foreground);
            if (title != null) views.setTextViewText(R.id.title, title);
            if (subtitle != null) views.setTextViewText(R.id.subtitle, subtitle);
        }
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID_FOREGROUND)
                .setSmallIcon(R.drawable.ic_notification)
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

