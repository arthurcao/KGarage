package com.kooltech.droid.kgarage.MyGarage;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.kooltech.droid.kgarage.utils.MYLOG;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MyWebsocketService extends Service {
    boolean isServiceActive = false;

    Context mContext;
    String path = "GarageDoor";
    public MyWebsocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }

    @Override
    public void onCreate() {

        Log.e("WebSocketClient", "MyWebsocketService: onCreate" );
        isServiceActive = true;
        path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/GarageDoor";
        File appDirectory = new File(path);
        if(!appDirectory.exists()){
            appDirectory.mkdirs();
        }


        mContext = this;
        resumeReceiver();
        if(!isSocketOpen){
            connectWebSocket();
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isServiceActive = true;
        if(!isSocketOpen){
            connectWebSocket();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        isServiceActive = false;
        stopReceiver();
        if(mWebSocketClient != null){
            mWebSocketClient.close();
            isSocketOpen = false;
        }
        Log.e("WebSocketClient", "MyWebsocketService: onDestroy" );

        super.onDestroy();
    }

    private String getCurrentTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(c.getTime());
    }
    private void writeLog(String line){
        try{

           String file = path + "/Log.txt";
           File f = new File(file);
            if(!f.exists()){
                f.createNewFile();
            }
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(f, true), "UTF-8");
            BufferedWriter fbw = new BufferedWriter(writer);

            fbw.write("Time: " +getCurrentTime() + ">>>>" + line);
            fbw.newLine();
            fbw.close();
        }catch (Exception e) {

        }
    }

    WebSocketClient mWebSocketClient;
    boolean isSocketOpen = false;
    private void connectWebSocket() {
        Log.e("20150609", "connected isServiceActive:" + isServiceActive );
        Log.e("20150609", "connected isSocketOpen:" + isSocketOpen );
        if(!isServiceActive){
            return;
        }
        if(isSocketOpen){
            return;
        }
        isSocketOpen = true;
        URI uri;
        try {

//            ws://socket.kooltechs.com:12345
//            uri = new URI("ws://echo.websocket.org");
            Log.e("20150609", "Start connect websocket" );
            uri = new URI("ws://socket.kooltechs.com:12345");
//            uri = new URI("ws://socket.kooltechs.com");
            Log.e("20150609", "connected WebSocket" );
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("20150609", "connected URISyntaxException: " + e.toString());
            isSocketOpen = false;
            return;
        }


        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                MYLOG.getInstance().saveLog("WebSocketClient","onOpen ");
                isSocketOpen = true;
                writeLog("MyWebsocketService onOpen");
                Log.e("WebSocketClient", "mWebSocketClient onOpen");
            }

            @Override
            public void onMessage(String message) {

                MYLOG.getInstance().LOG("20150609", "WebSocketClient");
                MYLOG.getInstance().LOG("20150611", "message: " + message);
                MYLOG.getInstance().LOG("20150609", "-----------------------------------------");
                Object jsonObject = null;
                try {
                    jsonObject = new JSONTokener(message).nextValue();
                    if(jsonObject instanceof JSONObject){
                        //response is JSONObject
                        JSONObject json = (JSONObject)jsonObject;
                        if(json.has("command")){
                            int command = json.getInt("command");
                            if(command == 1){
                                //{"command":1,"door":{"door_id":"8","door_status":"0"}}
                                if(json.has("door")){
                                    JSONObject json1 = json.getJSONObject("door");

                                    int door_id = getIntValue(json1, "door_id");
                                    int door_status = getIntValue(json1, "door_status");
                                    MYLOG.getInstance().LOG("20150611", " door_id: " + door_id);
                                    MYLOG.getInstance().LOG("20150611", " door_status: " + door_status);

                                    Intent i = new Intent(RECEIVE_COMMAND_1);
                                    i.putExtra("door_id",door_id);
                                    i.putExtra("door_status",door_status);
                                    mContext.sendBroadcast(i);

                                }
                            }else if(command == 2){
                                //"command":2,"door":{"control_id":"3768","door_id":"8","control_status":"0"}
                                if(json.has("door")){
                                    JSONObject json1 = json.getJSONObject("door");

                                    int control_id = getIntValue(json1, "control_id");
                                    int door_id = getIntValue(json1, "door_id");
                                    int control_status = getIntValue(json1, "control_status");
                                    MYLOG.getInstance().LOG("20150611", " door_id: " + door_id);
                                    MYLOG.getInstance().LOG("control_id", " control_id: " + control_id);
                                    MYLOG.getInstance().LOG("20150611", " control_status: " + control_status);


                                    Intent i = new Intent(RECEIVE_COMMAND_2);
                                    i.putExtra("control_id",control_id);
                                    i.putExtra("door_id",door_id);
                                    i.putExtra("control_status",control_status);
                                    mContext.sendBroadcast(i);
                                }
                            }else if(command == 4){
//                                {"command":4,"door":{"door_id":"8","door_battery":"10"}}
                                if(json.has("door")){
                                    JSONObject json1 = json.getJSONObject("door");

                                    int door_id = getIntValue(json1, "door_id");
                                    int door_battery = getIntValue(json1,"door_battery");

                                    MYLOG.getInstance().LOG("20150611", " json1: " + json1);
                                    MYLOG.getInstance().LOG("20150611", " door_id: " + door_id);
                                    MYLOG.getInstance().LOG("20150611", " door_battery: " + door_battery);

                                    Intent i = new Intent(RECEIVE_COMMAND_4);
                                    i.putExtra("door_id",door_id);
                                    i.putExtra("door_battery",door_battery);
                                    mContext.sendBroadcast(i);
                                }
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("20150609", "connected JSONException: " + e.toString());
                }


            }

            @Override
            public void onClose(int i, String s, boolean b) {
                isSocketOpen = false;
                Log.e("WebSocketClient", "WebSocketClient onClose");
                Log.e("20150609", "onClose i: " + i);
                Log.e("20150609", "onClose s: " + s);
                Log.e("20150609", "onClose b: " + b);
                MYLOG.getInstance().saveLog("WebSocketClient","onClose + i" + i + " s:" + s +  "  b: " + b);

                new Thread(){
                    @Override
                    public void run() {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Intent i = new Intent(RESTART_WEDSOCKET);
                        mContext.sendBroadcast(i);
                    }
                }.start();
            }

            @Override
            public void onError(Exception e) {
                isSocketOpen = false;
                Log.e("WebSocketClient", "WebSocketClient onError");
                MYLOG.getInstance().saveLog("WebSocketClient", "onError: " + e.toString());
            }
        };
        mWebSocketClient.connect();
    }

    public void sendMessage(String msg) {

        mWebSocketClient.send(msg);

    }

    public int getIntValue( JSONObject json, String field) throws JSONException {
        if(json.has(field)){
            return json.getInt(field);
        }
        return -1;
    }
    public String getStringValue( JSONObject json,  String field) throws JSONException {
        if(json.has(field)){
            return json.getString(field);
        }
        return "";
    }




    private void resumeReceiver() {

        if(mGattUpdateReceiver == null){
            mGattUpdateReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (SEND_COMMAND_3.equals(action)) {
                       ArrayList<String> list = intent.getStringArrayListExtra("LISTID");


                        try {
                            JSONObject json = new JSONObject();
                            json.put("command",3);
                            JSONArray doors = new JSONArray();
                            for(int i = 0; i <list.size(); i++){
                                JSONObject json1 = new JSONObject();
                                json1.put("door_id",list.get(i));
                                doors.put(json1);
                            }
                            json.put("doors",doors);
                            if(isSocketOpen){
                                mWebSocketClient.send(json.toString().trim());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else  if (RESTART_WEDSOCKET.equals(action)) {
                        Log.e("onReceive", "restart connectWebSocket");
                        MYLOG.getInstance().saveLog("WebSocketClient", "Restart connectWebSocket");
                        connectWebSocket();
                    }
                }
            } ;
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
    }

    private void stopReceiver() {
        if(mGattUpdateReceiver != null){
            unregisterReceiver(mGattUpdateReceiver);
            mGattUpdateReceiver = null;
        }
    }
    public final static String RECEIVE_COMMAND_1 = "RECEIVE_COMMAND_1";
    public final static String RECEIVE_COMMAND_2 = "RECEIVE_COMMAND_2";
    public final static String RECEIVE_COMMAND_4 = "RECEIVE_COMMAND_4";
    public final static String RECEIVE_DATA_POST_SERVER= "RECEIVE_DATA_POST_SERVER";;
    public final static String SEND_COMMAND_3 = "SEND_COMMAND_3";
    public final static String RESTART_WEDSOCKET = "RESTART_WEDSOCKET";


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SEND_COMMAND_3);
        intentFilter.addAction(RESTART_WEDSOCKET);
//        intentFilter.addAction(GymDJVariable.ACTION_BLE_SERVICE_REMOVE_DEVICE);

        return intentFilter;
    }

    private  BroadcastReceiver mGattUpdateReceiver = null;

}
