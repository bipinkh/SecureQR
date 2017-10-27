package com.bipinkh.secureqr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ImageDisplayActivity extends AppCompatActivity {

    Button saveBtn, shareBtn;
    ImageView qrView;
    Bitmap bmpImage;
    TextView qrDesc;
    String receiver;
    String sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        Log.d("datsun", "onCreate: bitmap parsing");
        receiver = getIntent().getStringExtra("receiver");
        sender = getIntent().getStringExtra("sender");

        qrView = (ImageView) findViewById(R.id.qrImage);
        saveBtn = (Button) findViewById(R.id.saveImage);
        shareBtn = (Button) findViewById(R.id.shareImage);
        qrDesc = (TextView) findViewById(R.id.qrDescription);
        qrDesc.setText("\n\nSender:\n"+sender+"\n\nReceiver:\n"+receiver);

        bmpImage = BitmapFactory.decodeByteArray(
                getIntent().getByteArrayExtra("byteArray"),0,getIntent()
                        .getByteArrayExtra("byteArray").length);
        Log.d("datsun", "onCreate: success in parsing");
        qrView.setImageBitmap(bmpImage);

       //save button listener
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    String filename = filesave();
                    Toast.makeText(ImageDisplayActivity.this, "Saved to gallery with name :: "+filename, Toast.LENGTH_SHORT).show();
                }else{
                    requestPermission();
                }
            }
        });

        //share button listener
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    sharefile();
                }else{
                    requestPermission();
                }
            }
        });
    }


    private String filesave(){

        final String folder_path = Environment.getExternalStorageDirectory().getAbsolutePath();
        final File dir = new File(folder_path + "/SecureQR");
        if(!dir.exists())
            dir.mkdirs();

        final String filename =new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".png";
        final String img_path = dir.getAbsolutePath()+"/"+filename;

        File img = new File(img_path);
        try {
            img.createNewFile();
        } catch (IOException e) {
        }
        File file = new File(dir, filename);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            bmpImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("datsun", "saveBtnListener: Could not save to gallery ::"+e);
        }
    return filename;
    }

    private void sharefile(){
        String myFileName = filesave();
        String image_location = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SecureQR/";

        try{
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/png");
            final File photoFile = new File(image_location,myFileName);
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
            startActivity(Intent.createChooser(share, "Share QR"));
        }catch (Exception e){
            Log.d("datsun", "sharefile: sharing image failed"+e);
        }
        File img = new File(image_location);
        img.delete();
    }

    private boolean checkPermission()
    {
        boolean status1 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean status2 = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (status1 && status2 ) {
            return true;
        }else{
            return false;
        }
    }

    private void requestPermission()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ImageDisplayActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("In order to save/share image, it is must that you give permission for Storage, one time.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ActivityCompat.requestPermissions(ImageDisplayActivity.this,new String[]{READ_EXTERNAL_STORAGE},  1 );
                ActivityCompat.requestPermissions(ImageDisplayActivity.this,new String[]{WRITE_EXTERNAL_STORAGE},  1 );
            }});
        builder.create().show();

    }

}
