package com.challengers.area_analysis;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Switch;

/**
 * Created by Jimin on 2015-07-21.
 */
public class OnService extends Service {
    boolean count = false;
    int UpDownCount;
    Handler handler;

    private static final String TAG = "OnService";

    LocationManager manager;
    MyLocationListener[] listener = new MyLocationListener[]{
            new MyLocationListener(),
            new MyLocationListener()
    };


    boolean gps_enabled = false;
    boolean network_enabled = false;

    // ********************** Two GPS ************************* //
    Switch sw_accident, sw_construction, sw_caution;

    Double destinationLatitude, destinationLongitude, tempLatitude, tempLongitude;
    Double currentLatitude, currentLongitude;
    Double currentDistance, beforeDistance, formerDistance, firstDistance;

    boolean passCheck, apiCheck, approachCheck;
    long passStartTime, afterPassTime, desElapsedTime;

    // ************************ Sudden Stop ***************************** //
    Switch sw_push;

    float maxSpeed, currentSpeed, beforeSpeed, minSpeed;
    int currentCount, minCount;

    long suddenElapsedTime, suddenStopStartTime, currentTime;
    boolean suddenStopStartCheck, suddenStopCheck;

    Double gapDistance, beforeLatitude, beforeLongitude;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");

        // ****************** Two GPS ********************** //

        destinationLatitude = destinationLongitude = tempLatitude = tempLongitude = 0.0;
        currentLatitude = currentLongitude = currentDistance = beforeDistance = formerDistance = 0.0;

        passCheck = apiCheck = approachCheck = false;
        passStartTime = afterPassTime = desElapsedTime = 0;
        UpDownCount = 0;

        // ************************* Sudden Stop ************************ //

        maxSpeed = currentSpeed = beforeSpeed = minSpeed = 0;
        minSpeed = 49; // ��ӵ��δ� �����ӵ� 50km/h

        currentCount = minCount = 0;

        suddenElapsedTime = suddenStopStartTime = currentTime = 0;
        suddenStopStartCheck = suddenStopCheck = false;

        gapDistance = beforeLatitude = beforeLongitude = firstDistance = 0.0;


        // ******************** Manual Push ************************* //

        // ******************** Activity ******************* //
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        long minTime = 1000;
        float minDistance = 0;

        //listener[0] = new MyLocationListener();
        //listener[1] = new MyLocationListener();

        gps_enabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        /*if (!gps_enabled && !network_enabled) {
            Log.e(TAG, "!gps_enabled !network_enabled");
        } else {
            if (network_enabled) {
                Log.e(TAG, "network_enabled");
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, listener[0]);
            }
            if (gps_enabled) {
                Log.e(TAG, "gps_enabled");
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, listener[1]);
            }
        }*/
        try {
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, listener[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, listener[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + currentLatitude + ", " + currentLongitude);

            beforeLatitude = currentLatitude; // suddenStop GapDistance���ϱ� ���� ���.
            beforeLongitude = currentLongitude;

            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Toast.makeText(getApplicationContext(), "onStatus", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            //Toast.makeText(getApplicationContext(), "onProvider", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            //Toast.makeText(getApplicationContext(), "Disabled", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

        if (manager != null) {
            for (int i = 0; i < listener.length; i++) {
                try {
                    manager.removeUpdates(listener[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }
}