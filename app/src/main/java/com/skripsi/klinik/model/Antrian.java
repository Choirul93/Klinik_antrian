package com.skripsi.klinik.model;

public class Antrian {
    private String key;
    private int antrian_sekarang;
    private int jumlah_antrian;

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

}
