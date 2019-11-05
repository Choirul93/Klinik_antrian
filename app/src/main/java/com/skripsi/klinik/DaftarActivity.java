package com.skripsi.klinik;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DaftarActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText etNama, etPhone, etTangalLahir, etUsia, etBerat, etAlamat;
    private ProgressBar pbDaftar;
    private RadioGroup rgJenisKelamin;
    private RadioButton rbJenisKelamin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);
        initUI();
    }

    void initUI(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onSimpan(View view) {

        if(etNama.getText().toString().isEmpty()){
            etNama.setError("nama belum diisi");
            etNama.requestFocus();
            return;
        }

        if(etPhone.getText().toString().isEmpty()){
            etPhone.setError("nomor hp belum diisi");
            etPhone.requestFocus();
            return;
        }

        if(etTangalLahir.getText().toString().isEmpty()){
            etTangalLahir.setError("Tanggal lahir belum diisi");
            etTangalLahir.requestFocus();
            return;
        }

        pbDaftar.setVisibility(View.VISIBLE);

        String nama, nomor_hp, tanggal_lahir, alamat, jenis_kelamin;
        int usia = 0;
        double berat = 0;

        int selectedID = rgJenisKelamin.getCheckedRadioButtonId();
        rbJenisKelamin = findViewById(selectedID);
        if(rbJenisKelamin.getText().toString().equals("Laki-laki")){
            jenis_kelamin ="L";
        } else{
            jenis_kelamin ="P";
        }
        nama = etNama.getText().toString();
        nomor_hp = etPhone.getText().toString();
        tanggal_lahir = etTangalLahir.getText().toString();

        if(!etUsia.getText().toString().isEmpty()) usia = Integer.getInteger(etUsia.getText().toString());
        if(!etBerat.getText().toString().isEmpty()) berat = Double.valueOf(etBerat.getText().toString());

    }

    public void onOpenDatePIcker(View view) {
        showDatePicker();
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

}
