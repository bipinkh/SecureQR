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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import static android.R.attr.width;
import static android.R.id.input;
import static com.bipinkh.secureqr.R.attr.height;

/**
 * Created by bipin on 10/12/2017.
 */



public class HomeActivity extends Activity{

    String cipher = "AES";
    String mode = "CBC";
    String padding = "PKCS5PADDING";

    Button readButton, writeButton;
    private String textToEncrypt = "dummy data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addListenerOnReadButton(); //scan qr button
        addListenerOnWriteButton(); //make qr button
        addListenerOnCipherButton(); //configure cipher

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

    private void addListenerOnCipherButton(){
        Button cipherButton;
        cipherButton = (Button) findViewById(R.id.makeCipher);
        cipherButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                configureCipher();
            }
        });
    }

    public void configureCipher(){

        ArrayAdapter<String> adapter1, adapter2, adapter3 =null;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Configure");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        //spinenr for cipher
            Spinner dropdown1 = new Spinner(this);
            String[] items = new String[]{"DES", "AES", "RSA"};
            adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdown1.setAdapter(adapter1);
            dropdown1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    cipher = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    cipher=parent.getItemAtPosition(0).toString();
                }
            });

        //spinenr for mode
            Spinner dropdown2 = new Spinner(this);
            String[] modes = new String[]{"CBC", "ECB", "CTR"};
            adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, modes);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdown2.setAdapter(adapter2);
            dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mode = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mode=parent.getItemAtPosition(0).toString();
                }
            });

        //spinenr for padding
            Spinner dropdown3 = new Spinner(this);
            String[] paddings = new String[]{"PKCS1PADDING", "PKCS2PADDING", "PKCS5PADDING", "NoPadding"};
            adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, paddings);
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdown3.setAdapter(adapter3);
            dropdown3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    padding = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    padding=parent.getItemAtPosition(0).toString();
                }
            });

        layout.addView(dropdown1);
        layout.addView(dropdown2);
        layout.addView(dropdown3);
        builder.setView(layout);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(HomeActivity.this, "Cipher : "+cipher+"/"+mode+"/"+padding, Toast.LENGTH_SHORT).show();
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
        input.setInputType(InputType.TYPE_CLASS_TEXT);
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
