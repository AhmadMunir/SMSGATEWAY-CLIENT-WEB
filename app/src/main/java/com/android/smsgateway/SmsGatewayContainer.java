package com.android.smsgateway;

import com.google.gson.JsonObject;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsGatewayContainer {
        private static List<WebSocket> sockets = new ArrayList<WebSocket>();

        public static void add(WebSocket socket) {
            sockets.add(socket);
        }

        public static void remove(WebSocket socket) {
            sockets.remove(socket);
        }

        public  static void send(String message){
            for (WebSocket socket : sockets){
                socket.send(message);
            }
        }
        public static void notification(String message, boolean success){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("type", "notification");
            map.put("message", message);
            map.put("success", success);
            JSONObject response = new JSONObject(map);

//            panggil metode sent
            send(response.toString());
        }

    }

