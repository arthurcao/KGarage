package com.kooltech.droid.kgarage;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {
    public MyGcmListenerService() {
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);
//        Log.e("notification", "MyGcmListenerService from: " + from);
//        Log.e("notification", "MyGcmListenerService data: " + data);
//        Toast.makeText(this,"fomr: " + from,Toast.LENGTH_SHORT).show();


    }
}
