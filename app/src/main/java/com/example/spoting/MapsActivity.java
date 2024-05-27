package com.example.spoting;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.example.spoting.Fragment.HomeFragment;
import com.example.spoting.Fragment.ProfileFragment;
import com.example.spoting.Fragment.SettingFragment;
import com.google.android.material.navigation.NavigationBarView;

public class MapsActivity extends AppCompatActivity {

    private static final String TAG = "MapsActivity";
    private HomeFragment fragmentHome = new HomeFragment();
    private SettingFragment fragmentSetting = new SettingFragment();
    private ProfileFragment fragmentProfile = new ProfileFragment();
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.menu_frame_layout, fragmentHome, "fragmentHome")
                .add(R.id.menu_frame_layout, fragmentSetting, "fragmentSetting").hide(fragmentSetting)
                .add(R.id.menu_frame_layout, fragmentProfile, "fragmentProfile").hide(fragmentProfile)
                .commit();

        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, fragmentHome).commit();
        activeFragment = fragmentHome;

        NavigationBarView navigationBarView = findViewById(R.id.menu_bottom_navigation);
        navigationBarView.setSelectedItemId(R.id.menu_home);
        navigationBarView.setOnItemSelectedListener(navListener);

        LocalBroadcastManager.getInstance(this).registerReceiver(lockerReceiver,
                new IntentFilter("com.example.ACTION_DATA_AVAILABLE"));

    }

    private BroadcastReceiver lockerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Intent에서 데이터 가져오기
            Log.d(TAG, "수신 인텐트" + intent.toString());
            String dataFromServer = intent.getStringExtra("lockerID");
            if (dataFromServer != null && fragmentHome != null) {
                Log.d(TAG, "수신 데이터" + dataFromServer);
                fragmentHome.updateTextView(dataFromServer);
            }
        }
    };

    private final NavigationBarView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.menu_map) {
            selectedFragment = fragmentSetting;
        } else if (itemId == R.id.menu_home) {
            selectedFragment = fragmentHome;
        } else if (itemId == R.id.menu_profile) {
            selectedFragment = fragmentProfile;
        }

        if (selectedFragment != null && selectedFragment != activeFragment) {
            getSupportFragmentManager().beginTransaction().hide(activeFragment).show(selectedFragment).commit();
            activeFragment = selectedFragment;
        }
        return true;
    };


}
