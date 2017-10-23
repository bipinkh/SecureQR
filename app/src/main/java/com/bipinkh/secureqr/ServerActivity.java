package com.bipinkh.secureqr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;


public class ServerActivity extends AppCompatActivity {

    EditText rkeyView;
    EditText rvalueView;
    Button submitButton;
    Firebase usersFirebase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        Toast.makeText(this, "hello there", Toast.LENGTH_LONG).show();

        rkeyView = (EditText) findViewById(R.id.keyView);
        rvalueView = (EditText) findViewById(R.id.valueView);
        submitButton = (Button) findViewById(R.id.buttonView);
        usersFirebase = new Firebase("https://secureqr-bipinkh.firebaseio.com");

        submitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String key =rkeyView.getText().toString();
                String value = rvalueView.getText().toString();
                Toast.makeText(ServerActivity.this, key + " :::" + value, Toast.LENGTH_LONG).show();
                try{
                    Firebase entry = usersFirebase.child(key);
                    entry.setValue(value);
                    Toast.makeText(ServerActivity.this,"sent to firebase",Toast.LENGTH_LONG).show();
                }catch(Exception e){
                    Log.v("datsun", "Exception in writing to Firebase::: "+e);
                }
            }
        });
    }
}
