package com.android.smsgateway;

import android.provider.BaseColumns;

public class SmsContract {
    private SmsContract(){

    }

    public static class tbl_sms implements BaseColumns{
        public static final String TABLE_NAME = "tbl_sms";
        public static final String COLUMN_NO = "no_tujuan";
        public static final String COLUMN_SMS = "isi_sms";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_WAKTU = "waktu";
    }
}
