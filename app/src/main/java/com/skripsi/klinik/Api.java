package com.skripsi.klinik;

public class Api {
    public static String BASE = "http://192.168.43.172/antrian/";
    //public static String BASE = "http://klinikafiatisoreang.rf.gd/";
    public static String USER = BASE+"user.php";
    public static String PASIEN = BASE+"pasien.php";
    public static String ANTRIAN = BASE+"antrian.php";
    public static String HEADERANTRIAN = BASE+"headerAntrian.php";
    public static int RTO = 5000;
}
