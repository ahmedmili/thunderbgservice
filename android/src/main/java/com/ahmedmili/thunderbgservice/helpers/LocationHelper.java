package com.ahmedmili.thunderbgservice.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationHelper {
    private final FusedLocationProviderClient fused; private boolean running = false;
    public LocationHelper(Context ctx) { this.fused = LocationServices.getFusedLocationProviderClient(ctx); }
    @SuppressLint("MissingPermission") public void start() { if (running) return; running = true; new LocationRequest.Builder(10000).setPriority(Priority.PRIORITY_HIGH_ACCURACY).build(); }
    public void stop() { running = false; }
}

