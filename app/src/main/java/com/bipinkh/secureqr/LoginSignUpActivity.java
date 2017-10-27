package com.bipinkh.secureqr;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class LoginSignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!= null){
                    Log.d("datsun", "user on connection ::"+user.getEmail());
                    }else{
                        Log.d("datsun", "Email Not verified.");
                    }

                }
            };

        mAuth.addAuthStateListener(mAuthListener);
        addSignUpListener();    //sign up button listener
        addSignInListener();    //sing in button listener
        addQuickScanner();      //guest quick scanner

        if ( (mAuth.getCurrentUser() != null) && (mAuth.getCurrentUser().isEmailVerified()) ){
            Log.d("datsun", "onCreate: Session started at the begining");
            Intent intent = new Intent(LoginSignUpActivity.this, DashboardActivity.class);
            startActivity(intent);
        }
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

    private void addQuickScanner() {
        Button guestBtn = (Button) findViewById(R.id.guestBtn);
        guestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginSignUpActivity.this, QRscanActivity.class);
                startActivity(intent);
            }
        });

    }



    private void addSignInListener() {
            Button signinbtn = (Button) findViewById(R.id.signInbtn);
            signinbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkEmptyFieldAndWifi()) { //check if user has not entered wrong value and if wifi is off.

                    EditText emailbtn = (EditText) findViewById(R.id.email);
                    EditText passwordbtn = (EditText) findViewById(R.id.password);

                    String email = emailbtn.getText().toString();
                    String password = passwordbtn.getText().toString();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginSignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        stoast("Sign In Failed.");
                                        alertMessage("Sign In Failed. Password and email doesn't match." +
                                                "\n\nMake sure you have already signed up to our system");
                                    } else {
                                        Log.d("datsun", "Verifying your email");
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if (user.isEmailVerified()) {
                                            stoast("Email Verified");
                                            Log.d("datsun", "Email Verified");
                                            Intent intent = new Intent(LoginSignUpActivity.this, DashboardActivity.class);
                                            startActivity(intent);
                                        } else {
                                            alertMessage("Your Email is not verified." +
                                                    "Check the email we sent you to activate the account");
                                            Log.d("datsun", "Email not Verified. Cannot Sign In");
                                            FirebaseAuth.getInstance().signOut();
                                        }
                                    }
                                }
                            });
                }

            }
        });
    }

    private void addSignUpListener() {
        Button signupbtn = (Button) findViewById(R.id.signUpbtn);
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if user has not entered wrong value and if wifi is off.
                if (checkEmptyFieldAndWifi()) {
                EditText emailbtn = (EditText) findViewById(R.id.email);
                EditText passwordbtn = (EditText) findViewById(R.id.password);
                final String email = emailbtn.getText().toString();
                final String password = passwordbtn.getText().toString();
                Log.d("datsun", "signing up..." + email + "  with password: " + password);
                createAccount(email, password);
            }
            }
            });
    }

    public void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginSignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("datsun", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        Toast.makeText(LoginSignUpActivity.this, "Sign Up Status:: " + task.isSuccessful(), Toast.LENGTH_LONG).show();
                        if (!task.isSuccessful()) {
                            FirebaseAuthException e = (FirebaseAuthException )task.getException();
                            alertMessage("Cannot Sign Up.\n\n"+e.getMessage().toString());
                        } else {
                            Log.d("datsun", "Successful sign up.");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification();
                                alertMessage("Confirmation Email has been sent to your email." +
                                        "Click on the activation link on your email to verify the SecureQR account");
                                Log.d("datsun","email sent");
                            }else{
                                stoast("Null user");
                            }
                        }
                    }
                });
    }

    private boolean checkEmptyFieldAndWifi() {

        EditText email = (EditText) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);
        //make sure no field is empty
        if(email.getText().toString().trim().length()==0 || password.getText().toString().trim().length()==0 ){
            Toast.makeText(LoginSignUpActivity.this, "Please enter valid email and password.", Toast.LENGTH_LONG).show();
            return false;
        }
        //make sure password is minimum of 8 characters
        if(password.getText().toString().trim().length()<8){
            ltoast("Password requires a minimum of eight characters");
            return false;
        }
        //make sure wifi is also turned on
        if ( !checkWifi() ) {
            ltoast("You must grant permission to enable Wifi. Or, enable it yourself");
            return false;
        }
        //if all cases are matched, then login/sign up user
        stoast("sigining in with:: " + email);
        return true;
    }

    public boolean checkWifi(){
    //open wifi, if it's off
    @SuppressLint("WifiManagerLeak")
    final WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    if(! wifi.isWifiEnabled()){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginSignUpActivity.this);
        builder.setTitle("Info");
        builder.setMessage("Your Wifi needs to be enabled to sign in and sign up." +
                "\nApp cannot verify account with no Network access." +
                "\n\nSecure QR will now enable your Wifi.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wifi.setWifiEnabled(true);
                        ltoast("Wifi Enabled. Wait for it to be ON and resubmit your request");
                    }
                });
        builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    if(wifi.isWifiEnabled()){
        return true;
    }else{
        return false;
    }
}

    //my widgets for easy call
    public void ltoast(String s){Toast.makeText(LoginSignUpActivity.this,s,Toast.LENGTH_LONG).show();}
    public void stoast(String s){Toast.makeText(LoginSignUpActivity.this,s,Toast.LENGTH_SHORT).show();}
    public void alertMessage(String s){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginSignUpActivity.this);
        builder.setTitle("Info");
        builder.setMessage(s);
        builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        AlertDialog alert = builder.create();
        alert.show();
    }
}
