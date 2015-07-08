package com.example.dinh.kgarage.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.example.dinh.kgarage.MainActivity;
import com.example.dinh.kgarage.R;
import com.example.dinh.kgarage.utils.MYLOG;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by NgocTien on 5/23/2015.
 */
public class BroadcastSendNotification extends WakefulBroadcastReceiver {
//B1:3A:64:1C:50:F7:32:9F:15:97:32:AF:91:67:05:A1:B1:F1:E5:15
    //    private static int mNumberNotification = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("20150609", "action: " + action);
//        mNumberNotification++;
        if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
//            ComponentName comp = new ComponentName(context.getPackageName(),  GcmIntentService.class.getName());
//            // Start the service, keeping the device awake while it is launching.
//            startWakefulService(context, (intent.setComponent(comp)));
//            setResultCode(Activity.RESULT_OK);

            Bundle extras = intent.getExtras();
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            String messageType = gcm.getMessageType(intent);
            MYLOG.getInstance().saveLog("Notification", "com.google.android.c2dm.intent.RECEIVE");
//               action: com.google.android.c2dm.intent.RECEIVE
//              tamp: Bundle[{from=967986164064, alert={"door_id":"8","door_status":"1","door_battery":"64","door_time":"2015-06-10 07:52:11","door_auto_time":"2015-06-10 07:52:11"}, collapse_key=do_not_collapse}]
//              action: com.google.android.c2dm.intent.RECEIVE
//              tamp: Bundle[{from=967986164064, alert=Close door_id 8, collapse_key=do_not_collapse}]
            if (!extras.isEmpty()) {
                String tamp = extras.toString();
//                Toast.makeText(context, "tamp: " + tamp, Toast.LENGTH_SHORT).show();
//                Log.e("notification", "tamp: " + tamp);
                if (extras.containsKey("alert")) {
                    String msg = extras.getString("alert");

                    MYLOG.getInstance().saveLog("Notification", "msg: " + msg);

                    Log.e("GoogleCloudMessaging", ">>>>>>>>>>>>> msg: " + msg);
                    if (msg.startsWith("Close")) {
                        String message = msg;
                        msg = msg.replace("door_id", "");
                        msg = msg.replace("Close", "");
                        msg = msg.trim();
                        String doorID = msg;
                        message = "Door " + doorID + " auto close.";
                        sendAutoCloseNotification(message, doorID, context);
                    } else {
                        Object json = null;
                        try {
                            json = new JSONTokener(msg).nextValue();
                            if (json instanceof JSONObject) {
//                                Bundle[{from=967986164064, alert={"door_id":"8","door_status":"1","door_battery":"64","door_time":"2015-06-10 07:52:11","door_auto_time":"2015-06-10 07:52:11"}, collapse_key=do_not_collapse}]
                                JSONObject jsonObject = (JSONObject) json;
                                String door_id = jsonObject.getString("door_id");
                                int door_status = jsonObject.getInt("door_status");
                                int door_battery = jsonObject.getInt("door_battery");
                                String door_time = jsonObject.getString("door_time");
                                String door_auto_time = jsonObject.getString("door_auto_time");
                                String message = "Door " + door_id + " change status: ";
                                if (door_status == 1) {
                                    message += "close";
                                } else {
                                    message += "open";
                                }
                                sendChangeStatusNotification(message, door_id, context);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            sendEmergencyNotification(context);
                        }

                    }
                }
            }
        }
    }

    private void sendEmergencyNotification(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String msg = "Unkown notification (not in protocol)";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_bell)
                .setLights(0xff00ff00, 1000, 1000)
                .setContentTitle("Garage Door ")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setAutoCancel(true)
                .setContentText(msg);

//        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(3, mBuilder.build());
        sound(context);
    }

    private void sendAutoCloseNotification(String msg, String id, Context context) {

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_bell)
                .setLights(0xff00ff00, 1000, 1000)
                .setContentTitle("Garage Door ")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setAutoCancel(true)
                .setContentText(msg);

//      mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(1, mBuilder.build());

//        alarm and vibrate when notify is available
        sound(context);

    }


    private void sendChangeStatusNotification(String msg, String id, Context context) {

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_bell)
                .setLights(0xff00ff00, 1000, 1000)
                .setContentTitle("Garage Door ")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setAutoCancel(true)
                .setContentText(msg);

        mNotificationManager.notify(2, mBuilder.build());

        sound(context);
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void sound(Context context){
        Vibrator vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrate.vibrate(new long[]{500,500}, -1);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }

}
