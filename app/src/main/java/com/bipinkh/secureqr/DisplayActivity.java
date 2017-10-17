package com.bipinkh.secureqr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.security.Key;
import java.security.KeyPair;

public class DisplayActivity extends AppCompatActivity {

    public static KeyPair kp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        kp= processing.getKeypair();
        operationEncoding();
    }

    public void operationEncoding(){

        Bundle bundle = getIntent().getExtras();
        String text = bundle.getString("text");
        String cipher = bundle.getString("cipher");
//        String mode = bundle.getString("mode");
//        String padding = bundle.getString("padding");
//        String password = bundle.getString("password");

        if (text.length() !=0 )
        {
            try {
                String Algorithm = cipher;
                String Encryptedtext = processing.encryption(text, kp.getPublic());
                try{
                    Bitmap bmQR = generateQR(1000,1000, Encryptedtext);
                    displayQR(bmQR, Algorithm, text, Encryptedtext);
                }
                catch (Exception e){
                    Toast.makeText(DisplayActivity.this, "Error Writing QR", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e){
                Toast.makeText(DisplayActivity.this, "Error in Encryption", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(DisplayActivity.this, "Insert some text", Toast.LENGTH_SHORT).show();
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
        return bmp;
    }

    public void displayQR(Bitmap bmQR, String algorithm, String Text, String ciphertext){
        ImageView qrImage = (ImageView) findViewById(R.id.qrImage);
        TextView qrDescription = (TextView) findViewById(R.id.qrDescription);
        qrImage.setPaddingRelative(5,5,5,5);
        qrImage.setImageBitmap(bmQR);
        qrDescription.setText("Original Message:::\n"+Text+"\n\nAlgorithm Used:::\n"+ algorithm + "" +
                "\n\nEncryptedMessage:::\n"+ciphertext);
    }

}
