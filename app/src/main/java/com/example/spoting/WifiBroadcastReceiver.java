package com.example.spoting;

import static android.content.Context.WIFI_SERVICE;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class WifiBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "WifiBroadcastReceiver";
    private static final String CHANNEL_ID = "wifi_scan_channel";
    WifiManager wifiManager;
    List<ScanResult> scanResultList;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (intent.getAction() != null && intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            getWifiInfo(context);
        }
    }

    @SuppressLint("MissingPermission")
    public void getWifiInfo(Context context) {
        Log.d(TAG, "receive success!!");
        scanResultList = wifiManager.getScanResults();
        ArrayList<String> storeNames = new ArrayList<>();
        if (scanResultList != null) {
            storeNames.add("AndroidWifi");
            int thresHold = -60;

            int size = scanResultList.size();
            Log.d(TAG, "현재 인식되는 와이파이 수 : " + size);
            for (int i = 0; i < size; i++) {
                ScanResult scanResult = scanResultList.get(i);

                if (storeNames.contains(scanResult.SSID)){

                    if(scanResult.level > thresHold){
                        Log.d(TAG, "Wi-Fi AP SSID : " + scanResult.SSID);
                        Log.d(TAG, "Wi-Fi AP RSSI : " + scanResult.level);
                        sendNotification(context, "Found " + scanResult.SSID, "Tap to open app", "com.android.chrome");
                        context.stopService(new Intent(context, WifiScanService.class));
                    }

                }
            }
        }
    }

    private void sendNotification(Context context, String title, String message, String packageName) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "WiFi Scan Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for WiFi Scan Results");
            notificationManager.createNotificationChannel(channel);
        }

        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
        Log.d(TAG, "Notification sent to launch app: " + packageName);
    }
}
