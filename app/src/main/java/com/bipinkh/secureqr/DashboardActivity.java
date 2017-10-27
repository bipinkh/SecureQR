package com.bipinkh.secureqr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class DashboardActivity extends AppCompatActivity {

    private PrivateKey prkey = null;
    private PublicKey pbkey = null;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase fdb;
    private FirebaseAuth mAuth;
    private DatabaseReference fdbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Listener for read button
        Button writebtn = (Button) findViewById(R.id.writeQR);
        writebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, QRwriteActivity.class));
            }
        });

        //Listener for scan button
        Button readbtn = (Button) findViewById(R.id.readQR);
        readbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, QRscanActivity.class));
            }
        });

        //listener for logout button
        logoutlistener();

        //load stored private key for that email,
        // or make new one in case there's none
        setupKeyPair();

    }

    private void setupKeyPair() {

        //key pair
        KeyPair kp = null;

        //initializing firebase
        mAuth = FirebaseAuth.getInstance();
        fdb = FirebaseDatabase.getInstance();
        fdbref = fdb.getReference();

        //get current user email
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        log("User Online :  "+email);
        log("setting up key pair...");

        //check if there is already saved private key for the email in local machine
        SharedPreferences sp = getApplicationContext().getSharedPreferences("privateKeys", 0);
        String privateKeyString = sp.getString(email, "-"); //get private key of email or get -
        log("got private key from stored sharedpref");

        if (privateKeyString.equals("-")) {
            log("not found in shared preference. now generating new pair.");
            //no private key found for the email...
            //generate key pair
            try {
                kp = rsaCipher.generateKeyPair(2048);
            } catch (NoSuchAlgorithmException e1) {
                log("key generation exception :: No Algorithm Exception");
            }
            prkey = kp.getPrivate();
            pbkey = kp.getPublic();
            //save private key to local database
            String privateKey = Base64.encodeToString(kp.getPrivate().getEncoded(), Base64.DEFAULT);
            SharedPreferences settings = getSharedPreferences("privateKeys", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(email, privateKey); //email and its private key as key pair
            editor.commit();
            log("Private Key saved to sp");

            //save public key to firebase or update if already existed
            String publickey = Base64.encodeToString(kp.getPublic().getEncoded(), Base64.DEFAULT);
            String databasePath = email.substring(0,email.charAt('@'));
//            String databasePath = email.replaceAll(".", "%2E");
//            databasePath = databasePath.replaceAll("_", "%1E");
//            databasePath = databasePath.replaceAll("@", "%0E");
            log("database path:: " + databasePath);
            fdbref.child("Public Keys").child(databasePath).setValue(publickey);
            log("uploaded key for \n"+email+"\n\n"+publickey);

        } else {
            //found privatekey in sp
            log("Private Key found in sp");
        }
    }

    public void logoutlistener(){
        Button signoutbtn = (Button) findViewById(R.id.signoutbtn);
        signoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Context context = DashboardActivity.this;
                Intent intent = new Intent(context,LoginSignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    public void toast(String s) {Toast.makeText(DashboardActivity.this,s,Toast.LENGTH_SHORT).show();}
    public void log (String s)  {Log.d("datsun",s);}
}
