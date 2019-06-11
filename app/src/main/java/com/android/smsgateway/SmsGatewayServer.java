package com.android.smsgateway;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.SmsManager;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import com.android.smsgateway.DBHelper.*;

public class SmsGatewayServer extends WebSocketServer {

    private PendingIntent sendIntent;
    private PendingIntent deliveryIntent;

    private SQLiteDatabase db;

    public SmsGatewayServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public void setSendIntent(PendingIntent sendIntent){
        this.sendIntent = sendIntent;
    }

    public void setDeliveryIntent(PendingIntent sendIntent){
        this.deliveryIntent = deliveryIntent;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        SmsGatewayContainer.add(conn);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        SmsGatewayContainer.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
           try {
               JSONObject object = new JSONObject(message);

               try{
                   JSONArray to = object.getJSONArray("to");
                   String smsMessage = object.getString("message");

                   //lakukan bd
                   for (int i = 0; i< to.length(); i++){
                       SmsManager.getDefault().sendTextMessage(to.getString(i),
                               null, smsMessage, sendIntent, deliveryIntent);
                       Sms s = new Sms(to.getString(i), smsMessage, "Sukses", "sekarang");

                       smsmasukdb(s);
                   }
               }catch (JSONException e){
                   //to bukan array
                   String to = object.getString("to");
                   String smsMessage = object.getString("message");

                   SmsManager.getDefault().sendTextMessage(to,
                           null, smsMessage, sendIntent, deliveryIntent);

                   Sms s = new Sms(to, smsMessage, "Sukses", "sekarang");
                   smsmasukdb(s);
                   //kirin psn suksess
                   Map<String, String> map = new HashMap<String, String>();
                   map.put("type", "success");
                   map.put("message", "Success Send SMS");

                   JSONObject response = new JSONObject(map);
                   conn.send(response.toString());
               }


           }catch (JSONException e){
               Map<String, String> map = new HashMap<String, String>();
               map.put("type", "error");
               map.put("message", "Wrong JSON format");

               JSONObject response = new JSONObject(map);
               conn.send(response.toString());
           }



        }

    @Override
    public void onError(WebSocket conn, Exception ex) {
    }

    public void smsmasukdb(Sms sms){
        ContentValues cv =new ContentValues();
        cv.put(SmsContract.tbl_sms.COLUMN_NO, sms.getno_tujuan());
        cv.put(SmsContract.tbl_sms.COLUMN_SMS, sms.getisi_sms());
        cv.put(SmsContract.tbl_sms.COLUMN_STATUS, sms.getstatus());
        cv.put(SmsContract.tbl_sms.COLUMN_WAKTU, sms.getwaktu());
        db.insert(SmsContract.tbl_sms.TABLE_NAME, null, cv);
    }

}
