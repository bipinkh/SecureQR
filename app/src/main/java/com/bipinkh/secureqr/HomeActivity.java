package com.bipinkh.secureqr;
import android.content.SharedPreferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by bipin on 10/12/2017.
 */



public class HomeActivity extends Activity {

    public static PrivateKey defaultPrivateKey = null;
    public static PublicKey defaultPublicKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addListenerOnReadButton(); //scan qr button
        addListenerOnWriteButton(); //make qr button
//        addListenerOnCipherButton(); //configure cipher
        addListenerOnGenerator(); //generate key pair
        addListenerOnSharePublicKey(); //save current keypair
        try {
            loaddefault();
        } catch (Exception e) {
            Toast.makeText(this, "Cannot load default key", LENGTH_SHORT).show();
        }
    }

    public void loaddefault() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String defaultPrivateKeyString, defaultPublicKeyString;
        SharedPreferences settings = getSharedPreferences("private_public_keys", 0);
        defaultPrivateKeyString = settings.getString("defaultPrivateKey", null);
        defaultPublicKeyString = settings.getString("defaultPublicKey", null);
        Log.d("datsun", "Private Key::\n" + defaultPrivateKey + "\n\n");
        Log.d("datsun", "Public Key::\n" + defaultPublicKey + "\n\n");

        if (defaultPrivateKey == null || defaultPublicKey == null) {
            KeyPair kp = null;
            try {
                kp = rsaCipher.generateKeyPair(2048);
            } catch (NoSuchAlgorithmException e) {
                Toast.makeText(this, "Failed to generate key pair", LENGTH_SHORT).show();
            }
            defaultPrivateKey = kp.getPrivate();
            defaultPublicKey = kp.getPublic();
        } else {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            byte[] encodedPv = Base64.decode(defaultPrivateKeyString, Base64.DEFAULT);
            PKCS8EncodedKeySpec keySpecPv = new PKCS8EncodedKeySpec(encodedPv);
            defaultPrivateKey = kf.generatePrivate(keySpecPv);

            byte[] encodedPb = Base64.decode(defaultPublicKeyString, Base64.DEFAULT);
            X509EncodedKeySpec keySpecPb = new X509EncodedKeySpec(encodedPb);
            defaultPublicKey = kf.generatePublic(keySpecPb);

            Log.d("datsun", "retrieved prkey :\n" + defaultPrivateKey.toString());
            Log.d("datsun", "retrieved pbkey :\n" + defaultPublicKey.toString());
        }
    }

    public void setdefault(KeyPair kp) {
        String privateKey = new String(Base64.encode(kp.getPrivate().getEncoded(), Base64.DEFAULT));
        String publicKey = new String(Base64.encode(kp.getPublic().getEncoded(), Base64.DEFAULT));
        SharedPreferences settings = getSharedPreferences("private_public_keys", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("defaultPrivateKey", privateKey);
        editor.putString("defaultPublicKey", publicKey);
        editor.commit();
        Toast.makeText(this, "Default Key Pair saved. You can share your Public Key to others.", Toast.LENGTH_LONG).show();
    }

    private void addListenerOnGenerator() {

        Button btn = (Button) findViewById(R.id.generate);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final KeyPair genKeys = rsaCipher.generateKeyPair(2048);

                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setTitle("Make Default");
                    builder.setMessage(" Generation Successful. Make it default ?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setdefault(genKeys);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }
                    );
                    AlertDialog alert = builder.create();
                    alert.show();

                } catch (NoSuchAlgorithmException e) {
                    Log.d("datsun", "Generating Failed");
                    e.printStackTrace();
                }

            }
        });
    }

    public void addListenerOnReadButton() {
        final Context context = this;
        Button readButton = (Button) findViewById(R.id.readQR);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addListenerOnWriteButton() {
        Button writeButton = (Button) findViewById(R.id.writeQR);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForString();
            }
        });
    }

    public void askForString() {

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
                        String text = "qr://" + input.getText().toString();
                        bundle.putString("text", text);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
        );
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public static PrivateKey getDefaultPrivateKey(){
        return defaultPrivateKey;
    }
    public static PublicKey getDefaultPublicKey(){
        return defaultPublicKey;
    }

    private void addListenerOnSharePublicKey() {
        try {
            Button btn = (Button) findViewById(R.id.sharePublicKey);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("datsun", "sharing publick key");
                    String pubString = new String(Base64.encode(defaultPublicKey.getEncoded(), Base64.DEFAULT));
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Base64 encoded RSA Public Key");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, pubString);
                    startActivity(Intent.createChooser(sharingIntent,pubString));
                }
            });
        }catch (Exception e){
            Toast.makeText(HomeActivity.this, "Failed to share.", LENGTH_SHORT).show();
        }
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


