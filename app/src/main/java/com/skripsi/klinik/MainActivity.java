package com.skripsi.klinik;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText etPhone, etNama;
    Button btLogin;
    ProgressBar pbLogin;
    String token;
    JSONObject jsonData;
    SharedData sharedData;

    private static final String TAG = "MainActivity";
    RequestQueue queue;

    Context context;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);
        context = this;
        activity = this;
        sharedData = new SharedData(activity);
        initUI();
        getTOken();
        startService(new Intent(this, MyFirebaseMessagingService.class));

    }



    void initUI(){
        etNama = findViewById(R.id.etNama);
        etPhone = findViewById(R.id.etPhone);
        btLogin = findViewById(R.id.btLogin);
        pbLogin = findViewById(R.id.pbLogin);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.app_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
    }


    public void onDaftar(View view) {
        startActivity(new Intent(this,DaftarActivity.class));
    }

    public void onLogin(View view) {
        if(etNama.getText().toString().isEmpty()){
            etNama.setError("nama belum diisi");
            etNama.requestFocus();
            return;
        }

        if(etPhone.getText().toString().isEmpty()){
            etPhone.setError("nomor belum diisi");
            etPhone.requestFocus();
            return;
        }

        String nama, no_telepon;
        nama = etNama.getText().toString();
        no_telepon = etPhone.getText().toString();
        progresDialog(false,"");
        login(nama,no_telepon);
    }

    void getTOken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        token = task.getResult().getToken();
                        Log.d(TAG, token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void login(final String nama, final String nomor_handphone){

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String Response) {
                        Log.i(TAG, "onResponse: ==>"+Response);
                        try{
                            JSONObject jsonObject = new JSONObject(Response);
                            int status = jsonObject.getInt("status");
                            if(status==1){
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                JSONObject data = (JSONObject) dataArray.get(0);
                                jsonData = data;
                                updateFirebaseToken(data.getString("id"),token);

                            } else{
                                Toast.makeText(MainActivity.this,jsonObject.getString("message"),Toast.LENGTH_LONG);
                                progresDialog(true,jsonObject.getString("message"));

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progresDialog(true,e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    e.printStackTrace();
                    progresDialog(true,e.toString());
                }
            })
            {
                @Override
                public Map<String, String> getParams(){
                    Map<String, String> params = new HashMap<>();
                    params.put("nama", nama);
                    params.put("nomor_hp", nomor_handphone);
                    params.put("function", "get");
                    return params;
                }
            };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Api.RTO,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }

    void updateFirebaseToken(final String id, final String firebaseToken){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String Response) {
                        try {
                            loginSucces();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        e.printStackTrace();
                        progresDialog(true,e.toString());
                    }
                })
        {
            @Override
            public Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("firebase_token", firebaseToken);
                params.put("function", "updateFCM");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Api.RTO,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void progresDialog(boolean isShowToast, String err){
        if (pbLogin != null && pbLogin.isShown()){
            pbLogin.setVisibility(View.GONE);
            if(isShowToast)Toast.makeText(MainActivity.this,err,Toast.LENGTH_SHORT).show();
        }

        else if(pbLogin != null ){
            pbLogin.setVisibility(View.VISIBLE);
        }
    }

    private void loginSucces() throws JSONException {
        sharedData.putString(SharedData.ID,jsonData.getString("id"));
        sharedData.putString(SharedData.NAMA,jsonData.getString("nama"));
        sharedData.putString(SharedData.ISADMIN,jsonData.getString("isAdmin"));
        progresDialog(false,"");
        finish();
        startActivity(new Intent(this,DashboardActivity.class));
    }


}
