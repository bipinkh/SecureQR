package com.bipinkh.secureqr;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        makeQRbtn = (Button) findViewById(R.id.makeQRbtn);
        messagebox = (EditText) findViewById(R.id.messagebox);
        receiverEmail = (EditText) findViewById(R.id.receiverEmail);

        mAuth = FirebaseAuth.getInstance();
        fdb = FirebaseDatabase.getInstance();
        fdbref = fdb.getReference();
        user = mAuth.getCurrentUser();
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

        //listener on make qr button
        makeQRbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messagebox.getText().toString();
                String receiver = receiverEmail.getText().toString();

                //get public key of the receiver
                //encrypt
                //make qr
                //display


            }
        });
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
