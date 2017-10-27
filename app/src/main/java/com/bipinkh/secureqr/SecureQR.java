package com.bipinkh.secureqr;

import android.app.Application;
import android.widget.Toast;

import com.firebase.client.Firebase;

/**
 * Created by bipin on 10/22/2017.
 */

public class SecureQR extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
