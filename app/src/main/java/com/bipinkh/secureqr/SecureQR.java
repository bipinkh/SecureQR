package com.bipinkh.secureqr;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.firebase.client.Firebase;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

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
