package com.android.smsgateway;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.android.smsgateway.SmsContract.*;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sms.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;
    private SQLiteDatabase db;

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String sql = "create table tbl_sms(no integer primary key, no_tujuan text null, isi_sms text null, status text null, waktu text null);";
//        Log.d("Data", "onCreate: " + sql);
//        db.execSQL(sql);
        try {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS " + "tbl_sms" + " (" +
                            "id" +" integer primary key autoincrement," +
                            "no_tujuan" +" text NOT NULL," +
                            "isi_sms" +" text NOT NULL," +
                            "status" +" text NOT NULL," +
                            "waktu" +" text NOT NULL"+
                            ");"
            );

            Log.i("INFO", "Tabel "+"tbl_sms"+" was Created");
            Toast.makeText(context,"Tabel "+"tbl_sma"+" was Created",Toast.LENGTH_LONG);
            addsms();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addsms(){
        Sms s1 = new Sms("082316285715", "ini", "berhasil", "sekarang");
        smsmasukdb(s1);
        Sms s2 = new Sms("082316285715", "ini", "gagal", "sekarang");
        smsmasukdb(s2);
    }

    public void smsmasukdb(Sms sms){
        ContentValues cv =new ContentValues();
        cv.put(tbl_sms.COLUMN_NO, sms.getno_tujuan());
        cv.put(tbl_sms.COLUMN_SMS, sms.getisi_sms());
        cv.put(tbl_sms.COLUMN_STATUS, sms.getstatus());
        cv.put(tbl_sms.COLUMN_WAKTU, sms.getwaktu());
        db.insert(tbl_sms.TABLE_NAME, null, cv);
    }
}
