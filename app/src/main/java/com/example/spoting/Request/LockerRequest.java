package com.example.spoting.Request;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class LockerRequest extends StringRequest{

    final static private String URL = "http://ec2-13-209-22-235.ap-northeast-2.compute.amazonaws.com/CheckLocker.php";
    public LockerRequest(Response.Listener<String> listener) {
        super(Method.GET, URL, listener, null);
        Log.d("LockerRequest", "라커 할당 요청");
    }
}
