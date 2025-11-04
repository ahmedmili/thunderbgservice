package com.webify.thunderbgservice.core;

import static com.webify.thunderbgservice.core.FgConstants.*;

import android.content.Intent;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.webify.thunderbgservice.tasks.TaskResultStorage;

@CapacitorPlugin(name = "ThunderBgService")
public class ThunderBgServicePlugin extends Plugin {
    @PluginMethod
    public void start(PluginCall call) {
        String title = call.getString("notificationTitle", "Online");
        String subtitle = call.getString("notificationSubtitle", "Running");
        boolean enableLocation = call.getBoolean("enableLocation", true);
        boolean soundsEnabled = call.getBoolean("soundsEnabled", false);
        Intent extras = new Intent();
        extras.putExtra(EXTRA_TITLE, title);
        extras.putExtra(EXTRA_SUBTITLE, subtitle);
        extras.putExtra(EXTRA_ENABLE_LOCATION, enableLocation);
        extras.putExtra(EXTRA_SOUNDS, soundsEnabled);
        // Optional custom layout from JS
        String customLayout = call.getString("customLayout", null);
        String titleViewId = call.getString("titleViewId", null);
        String subtitleViewId = call.getString("subtitleViewId", null);
        String timerViewId = call.getString("timerViewId", null);
        if (customLayout != null) extras.putExtra(EXTRA_CUSTOM_LAYOUT, customLayout);
        if (titleViewId != null) extras.putExtra(EXTRA_TITLE_VIEW_ID, titleViewId);
        if (subtitleViewId != null) extras.putExtra(EXTRA_SUBTITLE_VIEW_ID, subtitleViewId);
        if (timerViewId != null) extras.putExtra(EXTRA_TIMER_VIEW_ID, timerViewId);
        ForegroundTaskService.startAction(getContext(), ACTION_START, extras);
        JSObject ret = new JSObject(); ret.put("started", true); call.resolve(ret);
    }

    @PluginMethod
    public void stop(PluginCall call) {
        ForegroundTaskService.startAction(getContext(), ACTION_STOP, null);
        JSObject ret = new JSObject(); ret.put("stopped", true); call.resolve(ret);
    }

    @PluginMethod
    public void update(PluginCall call) {
        Intent extras = new Intent();
        if (call.hasOption("notificationTitle")) extras.putExtra(EXTRA_TITLE, call.getString("notificationTitle"));
        if (call.hasOption("notificationSubtitle")) extras.putExtra(EXTRA_SUBTITLE, call.getString("notificationSubtitle"));
        // Optional: change layout dynamically
        String customLayout = call.getString("customLayout", null);
        String titleViewId = call.getString("titleViewId", null);
        String subtitleViewId = call.getString("subtitleViewId", null);
        String timerViewId = call.getString("timerViewId", null);
        if (customLayout != null) extras.putExtra(EXTRA_CUSTOM_LAYOUT, customLayout);
        if (titleViewId != null) extras.putExtra(EXTRA_TITLE_VIEW_ID, titleViewId);
        if (subtitleViewId != null) extras.putExtra(EXTRA_SUBTITLE_VIEW_ID, subtitleViewId);
        if (timerViewId != null) extras.putExtra(EXTRA_TIMER_VIEW_ID, timerViewId);
        ForegroundTaskService.startAction(getContext(), ACTION_UPDATE, extras);
        JSObject ret = new JSObject(); ret.put("updated", true); call.resolve(ret);
    }

    @PluginMethod
    public void registerTask(PluginCall call) {
        String taskId = call.getString("taskId", "");
        String taskClass = call.getString("taskClass", "");
        long intervalMs = call.getLong("intervalMs", 5000);
        
        if (taskId.isEmpty() || taskClass.isEmpty() || intervalMs < 1000) {
            call.reject("Invalid parameters: taskId, taskClass required, intervalMs >= 1000");
            return;
        }
        
        Intent extras = new Intent();
        extras.putExtra(EXTRA_TASK_ID, taskId);
        extras.putExtra(EXTRA_TASK_CLASS, taskClass);
        extras.putExtra(EXTRA_TASK_INTERVAL, intervalMs);
        ForegroundTaskService.startAction(getContext(), ACTION_REGISTER_TASK, extras);
        
        JSObject ret = new JSObject();
        ret.put("registered", true);
        call.resolve(ret);
    }

    @PluginMethod
    public void unregisterTask(PluginCall call) {
        String taskId = call.getString("taskId", "");
        if (taskId.isEmpty()) {
            call.reject("taskId is required");
            return;
        }
        
        Intent extras = new Intent();
        extras.putExtra(EXTRA_TASK_ID, taskId);
        ForegroundTaskService.startAction(getContext(), ACTION_UNREGISTER_TASK, extras);
        
        JSObject ret = new JSObject();
        ret.put("unregistered", true);
        call.resolve(ret);
    }

    @PluginMethod
    public void getTaskResult(PluginCall call) {
        String taskId = call.getString("taskId", "");
        if (taskId.isEmpty()) {
            call.reject("taskId is required");
            return;
        }
        
        try {
            org.json.JSONObject result = TaskResultStorage.getResult(getContext(), taskId);
            JSObject ret = new JSObject();
            if (result != null) {
                // Convertir JSONObject en JSObject
                JSObject resultObj = new JSObject();
                java.util.Iterator<String> keys = result.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = result.get(key);
                    resultObj.put(key, value);
                }
                ret.put("result", resultObj);
            } else {
                ret.put("result", null);
            }
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Error getting task result: " + e.getMessage());
        }
    }
}

