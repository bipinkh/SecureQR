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
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import static android.R.attr.width;
import static android.R.id.input;
import static com.bipinkh.secureqr.R.attr.dialogPreferredPadding;
import static com.bipinkh.secureqr.R.attr.height;

/**
 * Created by bipin on 10/12/2017.
 */



public class HomeActivity extends Activity{

    String cipher = "AES";
    String mode = "CBC";
    String padding = "PKCS5PADDING";
    String password = "1234567890123456";

    Button readButton, writeButton;
    private String textToEncrypt = "dummy data";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addListenerOnReadButton(); //scan qr button
        addListenerOnWriteButton(); //make qr button
        addListenerOnCipherButton(); //configure cipher
        addListenerOnGenerator(); //generate key pair

    }

    private void addListenerOnGenerator() {

    Toast.makeText(this, "New set of key pair generated", Toast.LENGTH_SHORT).show();
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
                cipher = "RSA";
//                configureCipher();
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
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Context context = HomeActivity.this;
                Intent intent = new Intent(context, DisplayActivity.class);
                Bundle bundle = new Bundle();
                String text = "qr://"+input.getText().toString();
                bundle.putString("text", text);
                intent.putExtras(bundle);
                startActivity(intent);
            }}
        );
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

//    public void configureCipher(){
//
//        ArrayAdapter<String> adapter1, adapter2, adapter3 =null;
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Configure");
//
//        LinearLayout layout = new LinearLayout(this);
//        layout.setOrientation(LinearLayout.VERTICAL);
//
//        //spinner for cipher
//        Spinner dropdown1 = new Spinner(this);
//        String[] items = new String[]{"AES", "RSA"};
//        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        dropdown1.setAdapter(adapter1);
//        dropdown1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                cipher = parent.getItemAtPosition(position).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                cipher=parent.getItemAtPosition(0).toString();
//            }
//        });
//
//        //spinner for mode
//        Spinner dropdown2 = new Spinner(this);
//        String[] modes = new String[]{"CBC", "ECB", "CTR"};
//        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, modes);
//        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        dropdown2.setAdapter(adapter2);
//        dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mode = parent.getItemAtPosition(position).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                mode=parent.getItemAtPosition(0).toString();
//            }
//        });
//
//        //spinner for padding
//        Spinner dropdown3 = new Spinner(this);
//        String[] paddings = new String[]{"PKCS1PADDING", "PKCS2PADDING", "PKCS5PADDING", "NoPadding"};
//        adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, paddings);
//        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        dropdown3.setAdapter(adapter3);
//        dropdown3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                padding = parent.getItemAtPosition(position).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                padding=parent.getItemAtPosition(0).toString();
//            }
//        });
//
//        //textfield for password
//        final EditText passwordField = new EditText(this);
//        passwordField.setHint("Password(16 character)");
//        passwordField.setInputType(InputType.TYPE_CLASS_TEXT);
//
//        layout.addView(dropdown1);
//        layout.addView(dropdown2);
//        layout.addView(dropdown3);
//        layout.addView(passwordField);
//        builder.setView(layout);
//
//        // Set up the buttons
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (passwordField.getText().toString().length()==16 ){
//                    password = passwordField.getText().toString();
//                }
//                else{
//                    Toast.makeText(HomeActivity.this, "Error :: Password must be 16 characters\n.Default password used",
//                            Toast.LENGTH_SHORT).show();
//                }
//                String algorithm = cipher+"/"+mode+"/"+padding;
//                Toast.makeText(HomeActivity.this, "Cipher : "+algorithm, Toast.LENGTH_SHORT).show();
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        builder.show();
//    }
}
