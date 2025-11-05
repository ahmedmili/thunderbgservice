package com.ahmedmili.thunderbgservice.core;

import static com.ahmedmili.thunderbgservice.core.FgConstants.*;

import android.content.Intent;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.ahmedmili.thunderbgservice.tasks.TaskResultStorage;
import com.ahmedmili.thunderbgservice.geofencing.GeofenceManager;

@CapacitorPlugin(name = "ThunderBgService")
public class ThunderBgServicePlugin extends Plugin {
    private static ThunderBgServicePlugin instance;
    private GeofenceManager geofenceManager;
    
    @Override
    public void load() {
        super.load();
        instance = this;
        geofenceManager = new GeofenceManager(getContext());
    }
    
    public static ThunderBgServicePlugin getInstance() {
        return instance;
    }
    
    /**
     * Méthode publique pour émettre des événements depuis les classes externes.
     * Permet à TaskEventEmitter d'émettre des événements vers JS.
     */
    public void emitTaskEvent(String eventName, JSObject data) {
        notifyListeners(eventName, data);
    }
    
    @PluginMethod
    public void start(PluginCall call) {
        // S'assurer que l'instance est stockée
        if (instance == null) {
            instance = this;
        }
        String title = call.getString("notificationTitle", null);
        String subtitle = call.getString("notificationSubtitle", null);
        boolean enableLocation = call.getBoolean("enableLocation", true);
        boolean soundsEnabled = call.getBoolean("soundsEnabled", false);
        Intent extras = new Intent();
        if (title != null) extras.putExtra(EXTRA_TITLE, title);
        if (subtitle != null) extras.putExtra(EXTRA_SUBTITLE, subtitle);
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
        // Dynamic bindings (optional): viewData (object) and buttons (array)
        try {
            com.getcapacitor.JSObject viewData = call.getObject("viewData", null);
            if (viewData != null) {
                extras.putExtra(EXTRA_VIEW_DATA_JSON, viewData.toString());
            }
            com.getcapacitor.JSArray buttons = call.getArray("buttons", null);
            if (buttons != null) {
                extras.putExtra(EXTRA_BUTTONS_JSON, buttons.toString());
            }
        } catch (Exception ignored) {}
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
        if (call.hasOption("notificationTitle")) { String v = call.getString("notificationTitle"); if (v != null) extras.putExtra(EXTRA_TITLE, v); }
        if (call.hasOption("notificationSubtitle")) { String v = call.getString("notificationSubtitle"); if (v != null) extras.putExtra(EXTRA_SUBTITLE, v); }
        // Optional: change layout dynamically
        String customLayout = call.getString("customLayout", null);
        String titleViewId = call.getString("titleViewId", null);
        String subtitleViewId = call.getString("subtitleViewId", null);
        String timerViewId = call.getString("timerViewId", null);
        if (customLayout != null) extras.putExtra(EXTRA_CUSTOM_LAYOUT, customLayout);
        if (titleViewId != null) extras.putExtra(EXTRA_TITLE_VIEW_ID, titleViewId);
        if (subtitleViewId != null) extras.putExtra(EXTRA_SUBTITLE_VIEW_ID, subtitleViewId);
        if (timerViewId != null) extras.putExtra(EXTRA_TIMER_VIEW_ID, timerViewId);
        // Dynamic bindings (optional)
        try {
            com.getcapacitor.JSObject viewData = call.getObject("viewData", null);
            if (viewData != null) {
                extras.putExtra(EXTRA_VIEW_DATA_JSON, viewData.toString());
            }
            com.getcapacitor.JSArray buttons = call.getArray("buttons", null);
            if (buttons != null) {
                extras.putExtra(EXTRA_BUTTONS_JSON, buttons.toString());
            }
        } catch (Exception ignored) {}
        ForegroundTaskService.startAction(getContext(), ACTION_UPDATE, extras);
        JSObject ret = new JSObject(); ret.put("updated", true); call.resolve(ret);
    }

    @PluginMethod
    public void registerTask(PluginCall call) {
        String taskId = call.getString("taskId", "");
        String taskClass = call.getString("taskClass", "");
        long intervalMs = call.getLong("intervalMs", 5000L);
        
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
    
    @PluginMethod
    public void addGeofence(PluginCall call) {
        try {
            String id = call.getString("id", "");
            double latitude = call.getDouble("latitude", 0.0);
            double longitude = call.getDouble("longitude", 0.0);
            double radius = call.getDouble("radius", 100.0);
            String onEnter = call.getString("onEnter", null);
            String onExit = call.getString("onExit", null);
            
            if (id.isEmpty()) {
                call.reject("id is required");
                return;
            }
            
            GeofenceManager.GeofenceConfig config = new GeofenceManager.GeofenceConfig(
                id, latitude, longitude, (float) radius
            );
            config.onEnterAction = onEnter;
            config.onExitAction = onExit;
            
            // Extraire les extras
            try {
                JSObject extrasObj = call.getObject("extras", null);
                if (extrasObj != null) {
                    java.util.Iterator<String> keys = extrasObj.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        config.extras.put(key, extrasObj.getString(key));
                    }
                }
            } catch (Exception ignored) {}
            
            geofenceManager.addGeofence(config);
            
            JSObject ret = new JSObject();
            ret.put("added", true);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Error adding geofence: " + e.getMessage());
        }
    }
    
    @PluginMethod
    public void removeGeofence(PluginCall call) {
        String geofenceId = call.getString("geofenceId", "");
        if (geofenceId.isEmpty()) {
            call.reject("geofenceId is required");
            return;
        }
        
        geofenceManager.removeGeofence(geofenceId);
        
        JSObject ret = new JSObject();
        ret.put("removed", true);
        call.resolve(ret);
    }
    
    @PluginMethod
    public void removeAllGeofences(PluginCall call) {
        geofenceManager.removeAllGeofences();
        
        JSObject ret = new JSObject();
        ret.put("removed", true);
        call.resolve(ret);
    }
}

