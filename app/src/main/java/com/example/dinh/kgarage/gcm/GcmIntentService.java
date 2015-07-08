package com.example.dinh.kgarage.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
//import com.google.android.gms.gcm.GoogleCloudMessaging;
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    String TAG= "GcmIntentService";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
   

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging. MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging. MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            	
            	
            	if (GoogleCloudMessaging.     MESSAGE_TYPE_MESSAGE.equals(messageType)) {
	                // Post notification of received message.
	                //sendNotification("Received: " + extras.toString());
	                String tamp = extras.toString();
//	                Log.e("06", ">>>>>Received:-" + tamp);
	                int startIndex = tamp.indexOf("alert=");
	                if(startIndex>=0){
	                	if(extras.containsKey("alert")){
	                		String msg = extras.getString("alert");
	                		String msg2 = msg;
//	                		Log.e("06", "msg:" + msg);
	                		String[] temp = msg.split(":");
//	                		Log.e("cap4", "temp:" + temp.length);
	                		if(temp.length == 2){
	                			String msg1 = temp[0];
	                			msg = temp[1];	                			
	                			temp = msg.split("-");
//	                			Log.e("06", " msg2:" + msg2);
//	                			Log.e("06", " temp[0]:" + temp[0]);
//	                			Log.e("06", " temp[1]:" + temp[1]);
	                			if(temp.length == 2){
	                				sendEmergencyNotification(msg2,"", temp[0], temp[1], this);
	                			}
	                		}
	                		
	                	}
	                }
	        	}
//            	
//            	 Log.e("cap4", ">>>>>com.google.android.c2dm.intent.RECEIVE:-" );
////    			 Bundle extras = intent.getExtras();
////    		        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
//    		        // The getMessageType() intent parameter must be the intent you received
//    		        // in your BroadcastReceiver.
////    		        String messageType = gcm.getMessageType(intent);
//    		        if (!extras.isEmpty()) {
//    		        	
//    		        }
    		        
    		        
                // Post notification of received message.
                //sendNotification("Received: " + extras.toString());
//                String tamp = extras.toString();
//                Log.e(TAG, ">>>>>Received:-" + tamp);
//                int startIndex = tamp.indexOf("alert=");
//                if(startIndex>=0){
//                	String subString = tamp.substring(startIndex);
//                	int stopIndex = subString.indexOf(",");
//                	String msg = subString.substring(0+6,stopIndex);
//                	Log.e(TAG, "msg:" + msg);
//                	if(msg.contains("Emergency")){
//	                	int stopIndex1 = msg.indexOf(":");
//	                	int stopIndex2 = msg.indexOf("-");
//	                	String name = msg.substring(14,stopIndex1);
//	                	String mLat = msg.substring(stopIndex1+1, stopIndex2);
//	                	String mLong = msg.substring(stopIndex2+1);
//	                	String message = name + ": " + mLat + "--" + mLong;
//	                	sendEmergencyNotification(message,name,mLat,mLong,this);
//                	}else{
//	                	//Log.e(TAG, ">>>>>Received:-" + msg);
//	                	sendNotification(msg);
//                	}
//                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        BroadcastSendNotification.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        //get messgage
        //int fromIndex = msg.indexOf("price=") + 6;
       // int toIndex = msg.indexOf("collapse_key=") - 2;
        //String content = msg.substring(fromIndex, toIndex);
        //Log.e(TAG, content);

        
//        //PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//         //       new Intent(this, RegisterExample.class), 0);
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//        .setSmallIcon(R.drawable.ic_launcher)
//        .setLights(0xff00ff00, 1000, 1000)
//        .setContentTitle("KoolApp Notification")
//        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
//        .setContentText(msg);
//
//        //mBuilder.setContentIntent(contentIntent);
//        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//        //alarm and vibrate when notify is available
//        Vibrator vibrate = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
//        vibrate.vibrate(new long[]{500,500}, -1);
//        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//        r.play();
    }
    
    private void sendEmergencyNotification(String msg, String name, String latitude, String longitude, Context context) {
//      NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//      Intent resultIntent = new Intent(context, MainActivity.class);
//      resultIntent.putExtra("EXTRA_NAME", name);
//      resultIntent.putExtra("EXTRA_LATITUDE", Double.parseDouble(latitude));
//      resultIntent.putExtra("EXTRA_LONGITUDE", Double.parseDouble(longitude));
//
//      PendingIntent resultPendingIntent =
//      	    PendingIntent.getActivity(
//      	    		context,
//      	    		0,
//      	    		resultIntent,
//      	    		PendingIntent.FLAG_CANCEL_CURRENT
//      	    		);
//
//
//
//      NotificationCompat.Builder mBuilder =
//              new NotificationCompat.Builder(context)
//      .setSmallIcon(R.drawable.ic_launcher)
//      .setLights(0xff00ff00, 1000, 1000)
//      .setContentTitle("FallDetection animation")
//      .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
//      .setAutoCancel(true)
//      .setContentText(msg);
//
//      mBuilder.setContentIntent(resultPendingIntent);
//      mNotificationManager.notify(1, mBuilder.build());
 
      //alarm and vibrate when notify is available
//      Vibrator vibrate = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//      vibrate.vibrate(new long[]{500,500}, -1);
//      Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//      Ringtone r = RingtoneManager.getRingtone(context, notification);
//      r.play();
      
    }
    
      
    
    
    
}