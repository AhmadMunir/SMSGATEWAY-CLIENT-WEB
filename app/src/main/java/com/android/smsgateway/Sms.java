package com.android.smsgateway;

public class Sms {
    private String no_tujuan;
    private String isi_sms;
    private String status;
    private String waktu;

    public Sms(){

    }

    public Sms(String no_tujuan, String isi_sms, String status, String waktu){
        this.no_tujuan = no_tujuan;
        this.isi_sms = isi_sms;
        this.status = status;
        this.waktu = waktu;
    }

    public String getno_tujuan(){
        return no_tujuan;
    }
    public String getisi_sms(){
        return isi_sms;
    }
    public String getstatus(){
        return status;
    }
    public String getwaktu(){
        return waktu;
    }
}
