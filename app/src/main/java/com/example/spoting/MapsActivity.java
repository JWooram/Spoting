package com.example.spoting;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.textclassifier.TextClassifierEvent;
import android.widget.Toast;

import com.example.spoting.MapFragment;
import com.example.spoting.ProfileFragment;
import com.example.spoting.SettingFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;
import java.util.Objects;

public class MapsActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private MapFragment fragmentMap = new MapFragment();
    private SettingFragment fragmentSetting = new SettingFragment();
    private ProfileFragment fragmentProfile = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, fragmentMap).commit();
        NavigationBarView navigationBarView = findViewById(R.id.menu_bottom_navigation);
        navigationBarView.setOnItemSelectedListener(navListener);
    }

    private NavigationBarView.OnItemSelectedListener navListener = item -> {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_map) {
            getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, fragmentMap).commit();
            return true;
        } else if (itemId == R.id.menu_setting) {
            getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, fragmentSetting).commit();
            return true;
        } else if (itemId == R.id.menu_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, fragmentProfile).commit();
            return true;
        }
        return false;
    };


}
