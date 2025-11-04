package com.webify.thunderbgservice.core;

public final class FgConstants {
    private FgConstants() {}
    public static final String CHANNEL_ID_FOREGROUND = "thunder_fg";
    public static final int NOTIFICATION_ID_FOREGROUND = 9101;
    public static final String ACTION_START = "com.webify.thunderbgservice.action.START";
    public static final String ACTION_STOP = "com.webify.thunderbgservice.action.STOP";
    public static final String ACTION_UPDATE = "com.webify.thunderbgservice.action.UPDATE";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_SUBTITLE = "extra_subtitle";
    public static final String EXTRA_ENABLE_LOCATION = "extra_enable_location";
    public static final String EXTRA_SOUNDS = "extra_sounds";

    // Optional custom notification layout
    public static final String EXTRA_CUSTOM_LAYOUT = "extra_custom_layout"; // layout resource name
    public static final String EXTRA_TITLE_VIEW_ID = "extra_title_view_id"; // id resource name
    public static final String EXTRA_SUBTITLE_VIEW_ID = "extra_subtitle_view_id"; // id resource name
    public static final String EXTRA_TIMER_VIEW_ID = "extra_timer_view_id"; // id resource name
    
    // Background tasks
    public static final String ACTION_REGISTER_TASK = "com.webify.thunderbgservice.action.REGISTER_TASK";
    public static final String ACTION_UNREGISTER_TASK = "com.webify.thunderbgservice.action.UNREGISTER_TASK";
    public static final String EXTRA_TASK_ID = "extra_task_id";
    public static final String EXTRA_TASK_CLASS = "extra_task_class";
    public static final String EXTRA_TASK_INTERVAL = "extra_task_interval";
}

