package com.bipinkh.secureqr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import static android.R.attr.width;
import static com.bipinkh.secureqr.R.attr.height;

/**
 * Created by bipin on 10/12/2017.
 */

public class HomeActivity extends Activity{

    Button readButton, writeButton;
    private String textToEncrypt = "dummy data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addListenerOnReadButton(); //scan qr button
        addListenerOnWriteButton(); //make qr button

    }

    public void addListenerOnReadButton()
    {
        final Context context = this;
        readButton = (Button) findViewById(R.id.readQR);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addListenerOnWriteButton() {
        writeButton = (Button) findViewById(R.id.writeQR);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForString();
            }
        });
    }


    public void askForString(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter String");

        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textToEncrypt = input.getText().toString();
                if (textToEncrypt != null)
                {
                    try{
                        Bitmap bmQR = generateQR(1000,1000);
                        displayQR(bmQR);

                    }
                    catch (Exception e){
                        Toast.makeText(HomeActivity.this, "Making QR Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public Bitmap generateQR(int wdth, int hght)
    {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = null;
        try {
            matrix = writer.encode(textToEncrypt, BarcodeFormat.QR_CODE, 1000, 1000);
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

    public void displayQR(Bitmap bmQR){

        AlertDialog.Builder ImageDialog = new AlertDialog.Builder(this);
        ImageDialog.setTitle("QR");
        ImageView showImage = new ImageView(this);
        showImage.setImageBitmap(bmQR);
        ImageDialog.setView(showImage);

        ImageDialog.setNegativeButton("Close", new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        ImageDialog.show();
    }
}
