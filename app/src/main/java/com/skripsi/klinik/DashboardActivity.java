package com.skripsi.klinik;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skripsi.klinik.model.Antrian;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {
    TextView  tvAntrian, tvJumlahAntrian, tvUmur, tvUsia, tvTanggalLahir, tvAlamat, tvMenunggu, tvMenungguAntrian, tvNomorAntrian,
            tvAntrianTutup, tvTanggalAntrian,tvTiketId,tvWaktu,tvNama;
    CardView cvNomorAntrian, cvDaftar;
    Button btnPanggil, btnDaftar;
    ShimmerFrameLayout shimmer_layout;

    private EditText etNama, etPhone, etTangalLahir, etUsia, etBerat, etAlamat;
    private ProgressBar pbDaftar;
    private RadioGroup rgJenisKelamin;
    private RadioButton rbJenisKelamin;
    ProgressBar progressBar;
    LinearLayout llNoConection;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private static String TAG = DashboardActivity.class.getSimpleName();
    int menunggu = 0;
    int nomor_antrian ;
    SharedData sharedData;
    private RequestQueue queue;
    Antrian antrian = new Antrian();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        sharedData = new SharedData(this);
        queue = Volley.newRequestQueue(this);

        if(sharedData.getString(SharedData.ISADMIN).equals("1")){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else{
            getAntrianSekarang("user",sharedData.getString(SharedData.ID));
        }
        initUI();
        initFirebase();
        listener();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(sharedData.getString(SharedData.ISADMIN).equals("1")){
            inflater.inflate(R.menu.menu_admin, menu);
        } else{
            inflater.inflate(R.menu.menu_user, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_user:
                logout();
                return true;
            case R.id.logout_admin:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void initUI(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        tvAntrian = findViewById(R.id.tvAntrian);
        tvJumlahAntrian = findViewById(R.id.tvJumlahAntrian);
        tvUsia = findViewById(R.id.tvUsia);
        tvTanggalLahir = findViewById(R.id.tvTanggalLahir);
        tvAlamat = findViewById(R.id.tvAlamat);
        progressBar = findViewById(R.id.progressBar);
        tvMenunggu = findViewById(R.id.tvMenunggu);
        tvMenungguAntrian = findViewById(R.id.tvMenungguAntrian);
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
        tvAntrianTutup = findViewById(R.id.tvAntrianTutup);
        tvTanggalAntrian = findViewById(R.id.tvTanggalAntrian);
        shimmer_layout = findViewById(R.id.shimmer_layout);
        cvNomorAntrian = findViewById(R.id.cvNomorAntrian);
        tvTiketId = findViewById(R.id.tvTiketId);
        tvWaktu = findViewById(R.id.tvWaktu);
        tvNama = findViewById(R.id.tvNama);
        llNoConection = findViewById(R.id.llNoConection);

        shimmer_layout.startShimmer();

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


                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()) {
                    antrian = noteSnapshot.getValue(Antrian.class);
                }
                updateUIHeaderAntrian();
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
        tvAntrianTutup.setVisibility(View.GONE);
        if(sharedData.getString(SharedData.ISADMIN).equals("1")){
            btnPanggil.setVisibility(View.VISIBLE);
        } else{
            btnDaftar.setVisibility(View.VISIBLE);
        }
    }

    void hideButton(){
        Log.i(TAG, "hideButton: ISADMIN ===>"+sharedData.getString(SharedData.ISADMIN));
        if(sharedData.getString(SharedData.ISADMIN).equals("0")){
            btnDaftar.setVisibility(View.GONE);
            tvAntrianTutup.setVisibility(View.VISIBLE);
            cvDaftar.setVisibility(View.GONE);
        } else{
            btnPanggil.setVisibility(View.VISIBLE);
        }
    }

    public void onAmbilAnrian(View view) {
        cvDaftar.setVisibility(View.VISIBLE);
        cvNomorAntrian.setVisibility(View.GONE);
        etNama.requestFocus();
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
        final StringRequest daftar = new StringRequest(Request.Method.POST, Api.ANTRIAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String Response) {
                        Log.i(TAG, "onResponse: ===>"+Response);
                        try{
                            JSONObject jsonObject = new JSONObject(Response);
                            int status = jsonObject.getInt("status");
                            if(status==1){
                                progresDialog(true,"berhasil Mendaftar");
                                fillCvAntrian(jsonObject);
                                cvDaftar.setVisibility(View.GONE);
                                cvNomorAntrian.setVisibility(View.VISIBLE);
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
                params.put("header_antrian_id",String.valueOf(antrian.getHeader_id()));

                params.put("function", "antrianCreate");
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

    void updateUIHeaderAntrian(){
        shimmer_layout.stopShimmer();
        tvTanggalAntrian.setText(antrian.getTanggal());
        tvAntrian.setText(String.valueOf(antrian.getAntrian_sekarang()));
        tvJumlahAntrian.setText(String.valueOf(antrian.getJumlah_antrian()));
        tvMenunggu.setText(String.valueOf(antrian.getJumlah_antrian()-antrian.getAntrian_sekarang()));
        tvNomorAntrian.setText(String.valueOf(nomor_antrian));

        if(nomor_antrian-antrian.getAntrian_sekarang()<0){
            tvMenungguAntrian.setText("selesai");
        } else{
            tvMenungguAntrian.setText(String.valueOf(nomor_antrian-antrian.getAntrian_sekarang()));
        }
        Log.i(TAG, "onDataChange: antrian.getIsOpen() ===>"+antrian.getIs_open());
        Log.i(TAG, "onDataChange: antrian.getheader ===>"+antrian.getHeader_id());

        if(antrian.getIs_open()==1)
            showButton() ;
        else{
            hideButton();

        }
    }
    
    void fillCvAntrian(JSONObject jsonObject) throws JSONException {
        llNoConection.setVisibility(View.GONE);
        JSONArray dataJsonarray = jsonObject.getJSONArray("data");
        JSONObject data = (JSONObject) dataJsonarray.get(0);
        etNama.setText(data.getString("nama"));
        tvTanggalLahir.setText(data.getString("tanggal_lahir"));
        tvUsia.setText(data.getString("usia"));
        tvAlamat.setText(data.getString("alamat"));
        tvTiketId.setText("#TIK"+data.getString("id"));
        tvWaktu.setText(data.getString("create_at"));
        tvNama.setText(data.getString("nama"));
        tvNomorAntrian.setText(data.getString("nomor_antrian"));
        nomor_antrian = Integer.valueOf(data.getString("nomor_antrian"));
        if(antrian.getAntrian_sekarang()>0){
            int menunggu = nomor_antrian-antrian.getAntrian_sekarang();
            if(menunggu>0){
                tvMenungguAntrian.setText(String.valueOf(menunggu));
            } else{
                tvMenungguAntrian.setText("Antrian selesai");
            }
        }
    }

    void getAntrianSekarang(final String type, final String generic){
        final StringRequest antrianSekarang = new StringRequest(Request.Method.POST, Api.ANTRIAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String Response) {
                        Log.i(TAG, "onResponse: ===>"+Response);
                        try{
                            JSONObject jsonObject = new JSONObject(Response);
                            int status = jsonObject.getInt("status");
                            if(status==1){
                                fillCvAntrian(jsonObject);
                                cvDaftar.setVisibility(View.GONE);
                                cvNomorAntrian.setVisibility(View.VISIBLE);
                            } else{
                                Toast.makeText(DashboardActivity.this,"Tidak ada nomor antrian",Toast.LENGTH_LONG).show();
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
                        Toast.makeText(DashboardActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        llNoConection.setVisibility(View.VISIBLE);
                    }
                })
        {
            @Override
            public Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("type",type);
                params.put("generic",generic);
                params.put("function", "antrianGetByGeneric");
                return params;
            }
        };
        queue.add(antrianSekarang);

    }

    public void onReSync(View view) {
        if(sharedData.getString(SharedData.ISADMIN).equals("1")){
            if(antrian.getAntrian_sekarang() != 0){
                getAntrianSekarang("admin",String.valueOf(antrian.getAntrian_sekarang()));
            }
        } else{
            getAntrianSekarang("user",sharedData.getString(SharedData.ID));
        }
    }

    void logout(){
        sharedData.clear();
        finish();
    }


}
