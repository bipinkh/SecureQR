package com.bipinkh.secureqr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class ResultDisplayActivity extends AppCompatActivity {

    public String scanresult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_display);

        Bundle bundle = getIntent().getExtras();
        scanresult = bundle.getString("scanresult");
        TextView tv = (TextView) findViewById(R.id.resultText);
        tv.setText(scanresult);
        Log.d("datsun","scanresult:::"+scanresult);
        decryptButtonHandler();
    }

    private void decryptButtonHandler() {
        Button decryptBtn = (Button) findViewById(R.id.decryptBtn);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //if no user is signed in, just display result and disable decrypt button
        if (user == null){
            TextView guestinfo = (TextView) findViewById(R.id.guestInfo);
            decryptBtn.setEnabled(false);
            guestinfo.setText("(Requires \"Sign In\" to use the \"Decrpt Text\" feature)");
        }
        //if user is signed in, allow to decrypt
        else{
            decryptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byte[] byteText = Base64.decode(scanresult, Base64.DEFAULT);
                    PrivateKey prkey = getMyPrivateKey();
                    String result = null;  //decryption
                    try {
                        result = rsaCipher.decryption(byteText,prkey);
                        Log.d("datsun","decrypted text ::"+result);
                    } catch (Exception e) {
                        Log.d("datsun", "Could not decrypt::: "+e);
                    }
                    if (result.startsWith("qr://")){
                        TextView tv3 = (TextView) findViewById(R.id.decryptedText);
                        tv3.setText("Decrypted Result");
                        TextView tv2 = (TextView) findViewById(R.id.decryptResulttext);
                        tv2.setText(result.substring(5,result.length()));
                        Toast.makeText(ResultDisplayActivity.this, "Encryption Successful", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        final String decryptedText = result;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ResultDisplayActivity.this);
                        builder.setTitle("Alert");
                        builder.setMessage(" Decryption of this text doesn't match the structure. " +
                                "It may be plain text or encrypted using different algorithm. \n\nDecrypt it any way ?");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TextView tv3 = (TextView) findViewById(R.id.decryptedText);
                                tv3.setText("Decrypted Result");
                                TextView tv2 = (TextView) findViewById(R.id.decryptResulttext);
                                tv2.setText(decryptedText.substring(5,decryptedText.length())+"\n\n");
                            }});
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
            });
        }

    }

    private PrivateKey getMyPrivateKey() {
        String myEmail;
        PrivateKey pk = null;
        try {
            myEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            Log.d("datsun","getting private key for \n"+myEmail.toString());
            SharedPreferences sp = getApplicationContext().getSharedPreferences("privateKeys", 0);
            String privateKeyString = sp.getString(myEmail, "null");
            Log.d("datsun", "private key for \n"+myEmail.toString()+"  is:\n"+privateKeyString);
            try {
                KeyFactory kf = KeyFactory.getInstance("RSA");
                byte[] encodedPv = Base64.decode(privateKeyString, Base64.DEFAULT);
                PKCS8EncodedKeySpec keySpecPv = new PKCS8EncodedKeySpec(encodedPv);
                try {
                    pk = kf.generatePrivate(keySpecPv);
                    Log.d("datsun","private key retrieved :: " + pk);
                } catch (InvalidKeySpecException e) {
                    Log.d("datsun","invalid key exception for generating private key froom string");
                }
            } catch (NoSuchAlgorithmException e) {
                Log.d("datsun","KeyFactory exception:: No Algorithm");
            }

        }catch (Exception e){
            Log.d("datsun", "Error is getting email");
        }
        return pk;

    }

}

