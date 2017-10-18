package com.bipinkh.secureqr;
import android.content.SharedPreferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.security.cert.X509Certificate;

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
        SharedPreferences settings = getApplicationContext().getSharedPreferences("private_public_keys", 0);
        defaultPrivateKeyString = settings.getString("defaultPrivateKey", null);
        defaultPublicKeyString = settings.getString("defaultPublicKey", null);
        String checkString = settings.getString("checkString", null);
        Log.d("datsun","Checking :\n"+checkString);
        Log.d("datsun", "Private Key::\n" + defaultPrivateKey + "\n\n");
        Log.d("datsun", "Public Key::\n" + defaultPublicKey + "\n\n");

        if (defaultPrivateKey == null || defaultPublicKey == null) {
            Toast.makeText(this, "No stored keypair found. Generated new key pair to use.", Toast.LENGTH_LONG).show();
            KeyPair kp = null;
            try {
                kp = rsaCipher.generateKeyPair(2048);
            } catch (NoSuchAlgorithmException e) {
                Toast.makeText(this, "Failed to generate key pair", LENGTH_SHORT).show();
            }
            defaultPrivateKey = kp.getPrivate();
            defaultPublicKey = kp.getPublic();/**/
            setdefault(kp);
            Log.d("datsun", "generated !!");
        } else {
            KeyFactory kf = KeyFactory.getInstance("RSA");

            byte[] encodedPv = Base64.decode(defaultPrivateKeyString, Base64.DEFAULT);
            PKCS8EncodedKeySpec  keySpecPv = new PKCS8EncodedKeySpec (encodedPv);
            defaultPrivateKey = kf.generatePrivate(keySpecPv);

            byte[] encodedPb = Base64.decode(defaultPublicKeyString, Base64.DEFAULT);
            X509EncodedKeySpec keySpecPb = new X509EncodedKeySpec(encodedPb);
            defaultPublicKey = kf.generatePublic(keySpecPb);

            Log.d("datsun", "retrieved prkey :\n" + defaultPrivateKey.toString());
            Log.d("datsun", "retrieved pbkey :\n" + defaultPublicKey.toString());
        }
    }

    public void setdefault(KeyPair kp) {
        String privateKey = Base64.encodeToString(kp.getPrivate().getEncoded(), Base64.DEFAULT);
        String publicKey = Base64.encodeToString(kp.getPublic().getEncoded(), Base64.DEFAULT);
        SharedPreferences settings = getSharedPreferences("private_public_keys", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("defaultPrivateKey", privateKey);
        editor.putString("defaultPublicKey", publicKey);
        editor.putString("checkString", "This string is to be stored");
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
        builder.setTitle("Enter messages to encrypt");

        //layout
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

//            final TextView tv = new TextView(this);
//            tv.setText("Enter your text lines to encrypt");
//            tv.setTextColor(Color.parseColor("#ff0000"));
//            tv.setTextSize(20);

            final EditText input = new EditText(this);
            input.setLines(3);
            input.setHint("(Message here)");
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

//            final TextView tv2 = new TextView(this);
//            tv2.setTextSize(20);
//            tv2.setText("Enter Base64 encoded PublicKey");
//            tv2.setTextColor(Color.parseColor("#ff0000"));
//
//            final EditText publicKeyinput = new EditText(this);
//            publicKeyinput.setHint("(Leave blank to use own public key)");


//            layout.addView(tv2);
//            layout.addView(publicKeyinput);
//            layout.addView(tv);
            layout.addView(input);
            builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().length() > 0){
                            final Context context = HomeActivity.this;
                            Intent intent = new Intent(context, DisplayActivity.class);
                            Bundle bundle = new Bundle();
                            String text = "qr://" + input.getText().toString();
                            bundle.putString("text", text);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(HomeActivity.this, "Insert Some Text", LENGTH_SHORT).show();
                        }

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


//    public void saveToKeyStore(PrivateKey pbk) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {
//        Log.d("datsun","check it 1 :: "+pbk.toString());
//        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
//        keyStore.load(null);
//        String alias = "myPrivateKey";
//        String password = "pass1234";
//        try {
//            keyStore.setKeyEntry(alias, pbk, password.toCharArray(), null);
//        }catch (Exception e){
//            Log.d("datsun","Exception occured here :: "+e);
//        }
//        Log.d("datsun","stored encoded");
//        PrivateKey pk = (PrivateKey) keyStore.getKey(alias,password.toCharArray());
//        Log.d("datsun","check it 2 :: "+pk.toString());
//        }
    }



