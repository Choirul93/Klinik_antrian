package com.skripsi.klinik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skripsi.klinik.model.Antrian;

public class DashboardActivity extends AppCompatActivity {
    TextView  tvAntrian, tvJumlahAntrian, tvUmur, tvBerat,
            tvTanggalLahir, tvAlamat, tvMenunggu, getTvMenungguAntrian, tvNomorAntrian;
    CardView cvNomorAntrian;

    ProgressBar progressBar;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private static String TAG = DashboardActivity.class.getSimpleName();
    int menunggu = 0;
    int nomor_antrian =19;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initUI();
        initFirebase();
        listener();
    }

    void initUI(){

        tvAntrian = findViewById(R.id.tvAntrian);
        tvJumlahAntrian = findViewById(R.id.tvJumlahAntrian);
        tvBerat = findViewById(R.id.tvBerat);
        tvTanggalLahir = findViewById(R.id.tvTanggalLahir);
        tvAlamat = findViewById(R.id.tvAlamat);
        progressBar = findViewById(R.id.progressBar);
        tvMenunggu = findViewById(R.id.tvMenunggu);
        getTvMenungguAntrian = findViewById(R.id.tvMenungguAntrian);
        tvNomorAntrian = findViewById(R.id.tvNomorAntrian);
        cvNomorAntrian = findViewById(R.id.cvNomorAntrian);
    }

    void initFirebase(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("antrian");
    }


    void listener(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Antrian antrian = new Antrian();

                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                    antrian = noteSnapshot.getValue(Antrian.class);
                }
                tvAntrian.setText(String.valueOf(antrian.getAntrian_sekarang()));

                tvJumlahAntrian.setText(String.valueOf(antrian.getJumlah_antrian()));
                tvMenunggu.setText(String.valueOf(antrian.getJumlah_antrian()-antrian.getAntrian_sekarang()));
                tvNomorAntrian.setText(String.valueOf(nomor_antrian));
                if(nomor_antrian-antrian.getAntrian_sekarang()<0){
                    getTvMenungguAntrian.setText("selesai");
                } else{
                    getTvMenungguAntrian.setText(String.valueOf(nomor_antrian-antrian.getAntrian_sekarang()));
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadNote:onCancelled", databaseError.toException());
            }
        };
        myRef.orderByKey().addValueEventListener(valueEventListener);
    }




}
