package com.bipinkh.secureqr;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
                if(user != null){   //signed in
                    stoast("Signed In");
                    Context context = LoginSignUpActivity.this;
                    Intent intent = new Intent(context,DashboardActivity.class);
                    startActivity(intent);
                }
                else{ //signed out
                   stoast("Not logged in");
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        addSignUpListener();    //sign up button
        addSignInListener();    //sing in button
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


    private void addSignUpListener() {
        final Button signupbtn = (Button) findViewById(R.id.signUpbtn);
        final EditText emailbtn = (EditText) findViewById(R.id.email);
        final EditText passwordbtn = (EditText) findViewById(R.id.password);
            signupbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = emailbtn.getText().toString();
                    String password = passwordbtn.getText().toString();
                    Log.d("datsun","signing up...");
                    createAccount(email,password);
                    Toast.makeText(LoginSignUpActivity.this, "Sigining up...", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void addSignInListener() {
        final Button signinbtn = (Button) findViewById(R.id.signInbtn);
        final EditText emailbtn = (EditText) findViewById(R.id.email);
        final EditText passwordbtn = (EditText) findViewById(R.id.password);
       signinbtn.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               String email = emailbtn.getText().toString();
               String password = passwordbtn.getText().toString();
               stoast("sigining in with:: "+ email + " : "+password +"\nplease wait..");
               mAuth.signInWithEmailAndPassword(email,password);
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
                            Toast.makeText(LoginSignUpActivity.this, "Failed Registration: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("datsun", "Successful sign up.");
                        }
                    }
                });
    }

public void ltoast(String s){Toast.makeText(LoginSignUpActivity.this,s,Toast.LENGTH_LONG).show();}
public void stoast(String s){Toast.makeText(LoginSignUpActivity.this,s,Toast.LENGTH_SHORT).show();}
}
