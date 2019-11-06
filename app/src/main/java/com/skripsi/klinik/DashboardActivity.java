package com.skripsi.klinik;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skripsi.klinik.model.Antrian;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {
    TextView  tvAntrian, tvJumlahAntrian, tvUmur, tvBerat, tvTanggalLahir, tvAlamat, tvMenunggu, getTvMenungguAntrian, tvNomorAntrian;
    CardView cvNomorAntrian, cvDaftar;
    Button btnPanggil, btnDaftar;

    private EditText etNama, etPhone, etTangalLahir, etUsia, etBerat, etAlamat;
    private ProgressBar pbDaftar;
    private RadioGroup rgJenisKelamin;
    private RadioButton rbJenisKelamin;

    ProgressBar progressBar;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private static String TAG = DashboardActivity.class.getSimpleName();
    int menunggu = 0;
    int nomor_antrian =19;
    SharedData sharedData;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        sharedData = new SharedData(this);
        queue = Volley.newRequestQueue(this);

        if(sharedData.getString(SharedData.ISADMIN).equals("1")) getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
        cvDaftar = findViewById(R.id.cvDaftar);
        btnPanggil = findViewById(R.id.btnPanggil);
        btnDaftar = findViewById(R.id.btnDaftar);

        etNama = findViewById(R.id.etNama);
        etPhone = findViewById(R.id.etPhone);
        etTangalLahir= findViewById(R.id.etTangalLahir);
        etUsia = findViewById(R.id.etUsia);
        etBerat = findViewById(R.id.etBerat);
        etAlamat  = findViewById(R.id.etAlamat);
        pbDaftar = findViewById(R.id.pbDaftar);
        rgJenisKelamin = findViewById(R.id.rgJenisKelamin);
        etNama.requestFocus();
        etTangalLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

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
                Log.i(TAG, "onDataChange: antrian.getIsOpen() ===>"+antrian.getIsOpen());

                if(antrian.getIsOpen()==1)showButton() ;
                else{
                    hideButton();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadNote:onCancelled", databaseError.toException());
            }
        };
        myRef.orderByKey().addValueEventListener(valueEventListener);
    }


    public void onDaftar(View view) {
        onSimpan();
    }

    public void onOpenDatePIcker(View view) {
    }

    void showButton(){
        if(sharedData.getString(SharedData.ISADMIN).equals("1")){
            btnPanggil.setVisibility(View.VISIBLE);
        } else{
            btnDaftar.setVisibility(View.VISIBLE);
        }
    }

    void hideButton(){
        btnDaftar.setVisibility(View.GONE);
    }

    public void onAmbilAnrian(View view) {
        cvDaftar.setVisibility(View.VISIBLE);
    }

    public void onSimpan() {
        if(etNama.getText().toString().isEmpty()){
            etNama.setError("nama belum diisi");
            etNama.requestFocus();
            return;
        }

        progresDialog(false,"");
        String nama, tanggal_lahir, alamat , jenis_kelamin;
        int usia = 0;

        int selectedID = rgJenisKelamin.getCheckedRadioButtonId();
        rbJenisKelamin = findViewById(selectedID);
        if(rbJenisKelamin.getText().toString().equals("Laki-laki")){
            jenis_kelamin ="L";
        } else{
            jenis_kelamin ="P";
        }
        nama = etNama.getText().toString();
        tanggal_lahir = etTangalLahir.getText().toString();
        alamat = etAlamat.getText().toString();

        if(etUsia.getText().toString().isEmpty()){
        }
            else{
                usia = Integer.valueOf(etUsia.getText().toString());
        }

        final String finalUsia = String.valueOf(usia), finalNama=nama, finalTanggal_lahir=tanggal_lahir, finalAlamat = alamat, finalJenis_kelamin = jenis_kelamin;
        final StringRequest daftar = new StringRequest(Request.Method.POST, Api.PASIEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String Response) {
                        Log.i(TAG, "onResponse: ===>"+Response);
                        try{
                            JSONObject jsonObject = new JSONObject(Response);
                            int status = jsonObject.getInt("status");
                            if(status==1){
                                progresDialog(true,"berhasil Mendaftar");
                                cvDaftar.setVisibility(View.GONE);
                            } else{
                                progresDialog(true,jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progresDialog(true,e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        e.printStackTrace();
                        progresDialog(true,e.getMessage());
                    }
                })
        {
            @Override
            public Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("nama", finalNama);
                params.put("tanggal_lahir", finalTanggal_lahir);
                params.put("alamat", finalAlamat);
                params.put("jenis_kelamin", finalJenis_kelamin);
                params.put("usia", finalUsia);
                params.put("user_id",sharedData.getString(SharedData.ID));

                params.put("function", "create");
                return params;
            }
        };
        queue.add(daftar);

    }


    private void showDatePicker() {
        Calendar minDate = Calendar.getInstance();
        int mYear = Calendar.getInstance().get(Calendar.YEAR);
        int mMonth = Calendar.getInstance().get(Calendar.MONTH);
        int mDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        minDate.set(Calendar.DAY_OF_WEEK, mDay);
        minDate.set(Calendar.MONTH, mMonth);
        minDate.set(Calendar.YEAR, mYear);
        DatePickerDialog dp = new DatePickerDialog(this,
                R.style.DatePickerDialogTheme, date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        dp.setTitle("Pilih Tanggal");
        dp.show();
    }

    final Calendar myCalendar = Calendar.getInstance();
    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRENCH);
            etTangalLahir.setText(sdf.format(myCalendar.getTime()));
        }
    };

    private void progresDialog(boolean isShowToast, String err){
        if (pbDaftar != null && pbDaftar.isShown()){
            pbDaftar.setVisibility(View.GONE);
            if(isShowToast) Toast.makeText(this,err,Toast.LENGTH_LONG).show();
        }

        else if(pbDaftar != null ){
            pbDaftar.setVisibility(View.VISIBLE);
        }
    }
}
