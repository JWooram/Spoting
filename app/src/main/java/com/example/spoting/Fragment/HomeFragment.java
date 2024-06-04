package com.example.spoting.Fragment;
//implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener

import static androidx.core.content.ContextCompat.registerReceiver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.spoting.FirstAuthActivity;
import com.example.spoting.GeofenceHelper;
import com.example.spoting.LoginActivity;
import com.example.spoting.OnLockerReceivedListener;
import com.example.spoting.R;
import com.example.spoting.Receiver.GeofenceBroadcastReceiver;
import com.example.spoting.Receiver.LockerBroadcastReceiver;
import com.example.spoting.Request.GeofenceRequest;
import com.example.spoting.Request.ReservationRequest;
import com.example.spoting.Request.ReservationUpdate;
import com.example.spoting.Reservation;
import com.example.spoting.ReservationAdapter;
import com.example.spoting.SaveSharedPreference;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class HomeFragment extends Fragment{

    private static final String TAG = "HomeFragment";
    private BroadcastReceiver broadcastReceiver;
    TextView lockerNumberTextView;
    private GoogleMap mMap;
    MapView mapView = null;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private FusedLocationProviderClient fusedLocationClient;

    private float GEOFENCE_RADIUS = 50;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    final static private String URL = "http://ec2-13-209-22-235.ap-northeast-2.compute.amazonaws.com/GolfGeofence.php";
    private RecyclerView recyclerView;
    private TextView noReservationsText;
    public ReservationAdapter reservationAdapter;
    public static final String ACTION_DATA_RECEIVED = "com.example.ACTION_DATA_RECEIVED";
    private List<Reservation> reservationList;


    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onStart: 브로드캐스트리시버 등록");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        lockerNumberTextView = rootView.findViewById(R.id.homefragment_userLockerID);
        Context context = getContext();

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);

        // RecyclerView가 null인지 확인
        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView is null");
            return rootView;
        }
        
        // RecyclerView 초기화 및 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)) ;
        reservationAdapter = new ReservationAdapter();
        recyclerView.setAdapter(reservationAdapter);


        CheckReservation(context);
        return rootView;
    }

    private BroadcastReceiver lockerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: 라커 인텐트 수신");
            // 브로드캐스트를 수신했을 때 처리할 내용 작성
            if (intent.getAction().equals(LockerBroadcastReceiver.ACTION_DATA_RECEIVED)) {

                String lockerID = intent.getStringExtra("locker_id");
                Log.d(TAG, "onReceive: 라커 ID" + lockerID);
                SaveSharedPreference.setUserLockerID(context, lockerID);
                reservationAdapter.updateLocker(0, lockerID);
            }
        }
    };

    private void CheckReservation(Context context) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println(response);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        Log.d(TAG, "예약 정보 확인 성공");
                        JSONArray reservations = jsonObject.getJSONArray("reservations");
                        for (int i = 0; i < reservations.length(); i++) {
                            JSONObject reservationObj = reservations.getJSONObject(i);
                            String name = reservationObj.getString("user_name");
                            String reservation_date = reservationObj.getString("reservation_date");
                            String course_name = reservationObj.getString("course_name");
                            int headcount = reservationObj.getInt("headcount");
                            String age_range = reservationObj.getString("age_range");

                            Log.d(TAG, "예약자 성함 :" + name);
                            Log.d(TAG, "예약 일시 :" + reservation_date);
                            Log.d(TAG, "예약 코스 :" + course_name);
                            Log.d(TAG, "예약 인원수 :" + headcount);
                            Log.d(TAG, "예약 연령대 :" + age_range);

                            Reservation reservation = new Reservation();
                            reservation.setUser_name(name);
                            reservation.setReservation_date(reservation_date);
                            reservation.setCourse_name(course_name);
                            reservation.setHeadcount(headcount);
                            reservation.setAge_range(age_range);
//                            reservation.setLockerID(SaveSharedPreference.getUserLockerID(context));
                            Log.d(TAG, "onResponse: " + reservation.toString());

                            reservationAdapter.addReservation(reservation);
                        }
                    } else {
                        Log.d(TAG, "예약 정보 없음");
                        recyclerView.setVisibility(View.GONE);
                        noReservationsText.setVisibility(View.VISIBLE);
                        return;
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "exception");
                    e.printStackTrace();
                }
            }

        };

        String userID = SaveSharedPreference.getUserID(context);
        Log.d(TAG, userID);
        ReservationUpdate reservationUpdate = new ReservationUpdate(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(reservationUpdate);
    }



    @Override
    public void onStart() {
        super.onStart();
        // 리시버 등록
        IntentFilter filter = new IntentFilter(LockerBroadcastReceiver.ACTION_DATA_RECEIVED);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(lockerReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        // 리시버 해제
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(lockerReceiver);
    }
}