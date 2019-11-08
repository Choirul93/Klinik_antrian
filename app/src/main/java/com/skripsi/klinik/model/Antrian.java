package com.skripsi.klinik.model;

public class Antrian {
    private String key;
    private int antrian_sekarang;
    private int jumlah_antrian;
    private int header_id;
    private String tanggal;
    private int is_open;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getIs_open() {
        return is_open;
    }

    public void setIs_open(int is_open) {
        this.is_open = is_open;
    }

    public Antrian() {
    }

    public Antrian(int antrian_sekarang, int jumlah_antrian) {
        this.antrian_sekarang = antrian_sekarang;
        this.jumlah_antrian = jumlah_antrian;
    }

    public int getAntrian_sekarang() {
        return antrian_sekarang;
    }

    public void setAntrian_sekarang(int antrian_sekarang) {
        this.antrian_sekarang = antrian_sekarang;
    }

    public int getJumlah_antrian() {
        return jumlah_antrian;
    }

    public void setJumlah_antrian(int jumlah_antrian) {
        this.jumlah_antrian = jumlah_antrian;
    }

    public int getHeader_id() {
        return header_id;
    }

    public void setHeader_id(int header_id) {
        this.header_id = header_id;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
