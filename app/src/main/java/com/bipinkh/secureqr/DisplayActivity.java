package com.bipinkh.secureqr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.DataOutput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

import static android.R.attr.bitmap;

public class DisplayActivity extends AppCompatActivity {

    public static KeyPair kp;
    Bitmap qrBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        operationEncoding();
        try {
            setShareListener();
            setsharekeyButtonListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //share public key
    private void setsharekeyButtonListener() {
        Button btn = (Button) findViewById(R.id.sharekeyButton);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PublicKey pub = HomeActivity.getDefaultPublicKey();
                String pubString = new String(Base64.encode(pub.getEncoded(), Base64.DEFAULT));
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Base64 encoded Public Key");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, pubString);
                startActivity(Intent.createChooser(sharingIntent,pubString));
            }
        });
    }

    //share image
    private void setShareListener() throws Exception{
        final Context context = this;
        Button shareButton = (Button) findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri pathURL = Uri.parse("android.resource://your.package.name/" + R.id.qrImage);
                String imgPath = pathURL.toString();
                Uri bmpUri = Uri.fromFile(new File(imgPath));
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                share.putExtra(Intent.EXTRA_STREAM, bmpUri);
                startActivity(Intent.createChooser(share, "Share Image"));
            }
        });

    }

    public void operationEncoding(){
        Bundle bundle = getIntent().getExtras();
        String text = bundle.getString("text");
        Log.d("datsun", "text length"+String.valueOf(text.length()) );
            try {
                String Algorithm = "rsa";
                PublicKey pk = HomeActivity.getDefaultPublicKey();
                    String Encryptedtext = processing.encryption(text, pk);
                    try{
                        Bitmap bmQR = generateQR(1000,1000, Encryptedtext);
                        displayQR(bmQR, Algorithm, text);
                    }
                    catch (Exception e){
                        Toast.makeText(DisplayActivity.this, "Error Writing QR", Toast.LENGTH_SHORT).show();
                    }

            }
            catch (Exception e){
                Toast.makeText(DisplayActivity.this, "Error in Encryption", Toast.LENGTH_SHORT).show();
            }
    }

    public Bitmap generateQR(int wdth, int hght, String message)
    {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = null;
        try {
            matrix = writer.encode(message, BarcodeFormat.QR_CODE, 1000, 1000);
        }
        catch (WriterException ex) {
            ex.printStackTrace();
        }
        Bitmap bmp = Bitmap.createBitmap(wdth, hght, Bitmap.Config.RGB_565);
        for (int x = 0; x < wdth; x++){
            for (int y = 0; y < hght; y++){
                bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
            }
        }
        qrBitmap = bmp;
        return bmp;
    }

    public void displayQR(Bitmap bmQR, String algorithm, String Text){
        ImageView qrImage = (ImageView) findViewById(R.id.qrImage);
        TextView qrDescription = (TextView) findViewById(R.id.qrDescription);
        qrImage.setPaddingRelative(5,5,5,5);
        qrImage.setImageBitmap(bmQR);
        qrDescription.setText("\n:::Original Message:::\n"+Text.substring(5)+"\n\n:::Algorithm Used:::\n"+ algorithm);
    }

}
