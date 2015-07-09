package com.kooltech.droid.kgarage;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

public class MyInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Log.e("notification", "onTokenRefresh" );
        RegistrationIntentService.startRegisterGCM(this);
        super.onTokenRefresh();
    }
}
