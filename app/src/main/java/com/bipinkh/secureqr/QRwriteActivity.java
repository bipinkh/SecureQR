package com.bipinkh.secureqr;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.deser.Deserializers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class QRwriteActivity extends AppCompatActivity {

    Button makeQRbtn;
    EditText messagebox;
    EditText receiverEmail;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase fdb;
    private FirebaseAuth mAuth;
    private DatabaseReference fdbref;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrwrite);
        //buttons
        makeQRbtn = (Button) findViewById(R.id.makeQRbtn);
        messagebox = (EditText) findViewById(R.id.messagebox);
        receiverEmail = (EditText) findViewById(R.id.receiverEmail);
        //authentication
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!= null){
                    log("signed in..");
                }
                log("auth state changed");
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        //user
        user = mAuth.getCurrentUser();
        //database and references
        fdb = FirebaseDatabase.getInstance();
        fdbref = fdb.getReference();

        //listener on make qr button
        makeqrbtnListener();
    }

    private void makeqrbtnListener() {

        makeQRbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = "qr://"+messagebox.getText().toString();
                log("message to encrypt::"+message);
                final String receiver = receiverEmail.getText().toString();
                log("check");
                log(receiver);
                //truncate special characters from email to get database key
                String databasePath = receiver.replaceAll("[^a-zA-Z0-9]", "");
                log("database key:::"+databasePath);

                //get public key of the receiver
                DatabaseReference refOfPublicKey = fdbref.child("Public Keys").child(databasePath);
                refOfPublicKey.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class); //public key string of receiver pulled from firebase
                        log("downloaded public key of\n"+receiver+"\n"+value);
                        try {
                            KeyFactory kf = KeyFactory.getInstance("RSA");
                            byte[] encodedPb = Base64.decode(value, Base64.DEFAULT);
                            X509EncodedKeySpec keySpecPv = new X509EncodedKeySpec(encodedPb);
                            PublicKey receiverKey = null;
                            try {
                                receiverKey = kf.generatePublic(keySpecPv);
                            } catch (InvalidKeySpecException e) {
                                e.printStackTrace();
                                log("chchchchch"+e.toString());
                            }
                            log("public key retrieved from firebase :: "+receiverKey);

                            //encrypt message
                            try {
                                byte[] bytemsg = rsaCipher.encryption(message,receiverKey);
                                String finalText = Base64.encodeToString(bytemsg, Base64.DEFAULT);
                                log("Encrypted text to QR ::: "+finalText);
                                //make qr
                                Bitmap bmQR = generateQR(1000,1000, finalText);
                                //call imager display for qr
                                Intent intent = new Intent (QRwriteActivity.this, ImageDisplayActivity.class);
                                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                                bmQR.compress(Bitmap.CompressFormat.PNG, 100, bs);
                                intent.putExtra("byteArray", bs.toByteArray());
                                intent.putExtra("receiver",receiver);
                                intent.putExtra("sender",mAuth.getCurrentUser().getEmail());
                                startActivity(intent);

                            } catch (Exception e) {
                                log("Cannot encrypt ::"+e);
                            }

                        } catch (NoSuchAlgorithmException e) {
                            log("KeyFactory exception:: No Algorithm");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        toast("Error in database: "+databaseError.getMessage().toString());
                        log("Error in database: "+databaseError.getMessage().toString());
                    }
                });
            }
        });
    }

    private Bitmap generateQR(int wdth, int hght, String message) {
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

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void toast(String s) {
        Toast.makeText(QRwriteActivity.this,s,Toast.LENGTH_SHORT).show();}
    public void log (String s)  {
        Log.d("datsun",s);}

}