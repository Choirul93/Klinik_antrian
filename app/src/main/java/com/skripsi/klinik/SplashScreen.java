package com.skripsi.klinik;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    SharedData sharedData;
    private static final String TAG = SplashScreen.class.getSimpleName();
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        activity = this;
        sharedData = new SharedData(activity);
        final String id = sharedData.getString(SharedData.ID);
        Log.i(TAG, "onCreate: id ===> "+id);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                if(id.isEmpty()){
                //if(true){
                    startActivity(new Intent(SplashScreen.this,MainActivity.class));
                }else{
                    startActivity(new Intent(SplashScreen.this,DashboardActivity.class));
                }

            }
        },2000);

    }
}
