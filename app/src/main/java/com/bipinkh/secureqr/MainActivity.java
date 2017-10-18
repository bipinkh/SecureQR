package com.bipinkh.secureqr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;


public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler
{
    private static final int REQUEST_CAMERA =1;
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(MainActivity.this, "Permision Granted", Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }
        }
    }

        private boolean checkPermission()
        {
            boolean status = ContextCompat.checkSelfPermission(MainActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED;
            return status;
        }

        private void requestPermission()
        {
            ActivityCompat.requestPermissions(this,new String[]{CAMERA},  REQUEST_CAMERA );
        }

        public void onRequestPermissionResult(int requestCode, String permission[], int grantResults[])
        {
            switch (requestCode)
            {
                case REQUEST_CAMERA:
                    if (grantResults.length > 0)
                    {
                        boolean cameraAccepted = ( grantResults[0] == PackageManager.PERMISSION_GRANTED );
                        if (cameraAccepted)
                        {
                            Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            {
                                if (shouldShowRequestPermissionRationale(CAMERA))
                                {
                                    displayAlertMessage("You need to allow permission for both permissions",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                        requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                    break;
            }
        }

    @Override
    public void onResume()
    {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkPermission())
            {
                if (scannerView == null)
                {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }
            else
            {
                requestPermission();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener)
        {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(message)
                    .setPositiveButton("OK", listener)
                    .setNegativeButton("CANCEL", null)
                    .create()
                    .show();
        }


    @Override
    public void handleResult(Result result) {
        String scanresult = result.getText();

        final Context context = MainActivity.this;
        Intent intent = new Intent(context, ScanActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("scanresult", scanresult);
        intent.putExtras(bundle);
        startActivity(intent);

    }
}
