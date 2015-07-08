package com.example.dinh.kgarage;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.example.dinh.kgarage.utils.MYLOG;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RegistrationIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_REGISTRATION = "com.example.dinh.kgarage.utils.action.ACTION_REGISTRATION";


    public static void startRegisterGCM(Context context){
        Intent i = new Intent(context, RegistrationIntentService.class);
        i.setAction(ACTION_REGISTRATION);
        context.startService(i);
    }


    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REGISTRATION.equals(action)) {
                InstanceID instanceID = InstanceID.getInstance(this);
                try{
                    String token = instanceID.getToken("954689755652", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    MySharedPreferences.saveRegisterID(this,token);
                    Log.e("notification", "RegistrationIntentService token: " + token);
                }catch (IOException e){
                    MYLOG.getInstance().LOG("IOException: " + e.toString());
                }
            }
        }


    }


}
