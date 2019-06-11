package com.android.smsgateway;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DBHelper SQLite = new DBHelper(this);

    private SmsGatewayServer server;
    public Button tombol;
    public Button tombolstop;
    public TextView status;

    String[] permissions = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.INTERNET};
    private static final int REQUEST_MULTIPLE_PERMISSIONS = 117;

    public MainActivity() {
        try {
            server = new SmsGatewayServer(6868);
        } catch (UnknownHostException e) {
            Log.e(MainActivity.class.getName(), e.getMessage(), e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.status);

        boolean perms = checkPermissions();

        SQLite = new DBHelper(getApplicationContext());
        tombol = findViewById(R.id.button);
        tombol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startserver();
            }
        });

        tombolstop = findViewById(R.id.stop);
        tombolstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopserver();
            }
        });

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        SmsGatewayContainer.notification("SMS sent", true);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        SmsGatewayContainer.notification("Generic failure",false);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        SmsGatewayContainer.notification("No service", false);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        SmsGatewayContainer.notification("Null PDU", false);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        SmsGatewayContainer.notification("Radio off", false);
                        break;

                }
            }
        }, new IntentFilter("SMS_SENT"));

        //receiver untuk delivery intent
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            SmsGatewayContainer.notification("SMS delivered", true);
                            break;
                        case Activity.RESULT_CANCELED:
                            SmsGatewayContainer.notification("SMS not delivered", false);
                            break;
                    }

                }
        }, new IntentFilter("SMS_DELIVERED"));

        //buat pending intent
        PendingIntent sendIntent = PendingIntent.getBroadcast(this, 0,
                new Intent("SMS_SENT"), 0);

        //buat delivery intent
        PendingIntent deliveryIntent = PendingIntent.getBroadcast(this, 0,
                new Intent("SMS_DELIVERED"), 0);

        server.setSendIntent(sendIntent);
        server.setDeliveryIntent(deliveryIntent);

    }


    @Override
    protected void onDestroy() {

        try {
            server.stop();
        } catch (IOException e) {
            Log.e(MainActivity.class.getName(), e.getMessage(), e);
        } catch (InterruptedException e) {
            Log.e(MainActivity.class.getName(), e.getMessage(), e);
        }
        super.onDestroy();
    }

    public void startserver(){
        server.start();
        tombol.setVisibility(View.INVISIBLE);
        tombolstop.setVisibility(View.VISIBLE);
        status.setText("Status Server : Berjalan");
        status.setTextColor(Color.GREEN);
    }

    public void stopserver(){
        try {
            server.stop();
            tombolstop.setVisibility(View.INVISIBLE);
            tombol.setVisibility(View.VISIBLE);
            status.setText("Status Server : Berhenti");
            status.setTextColor(Color.RED);
        } catch (IOException e) {
            Log.e(MainActivity.class.getName(), e.getMessage(), e);
        } catch (InterruptedException e) {
            Log.e(MainActivity.class.getName(), e.getMessage(), e);
        }
    }


    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    finish();
                    Toast.makeText(MainActivity.this, "Please Grant All Permission to Use All Feature", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}


