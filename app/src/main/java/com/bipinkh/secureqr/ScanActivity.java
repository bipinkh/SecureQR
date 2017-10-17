package com.bipinkh.secureqr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.KeyPair;

public class ScanActivity extends AppCompatActivity {
    public String scanresult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Bundle bundle = getIntent().getExtras();
        scanresult = bundle.getString("scanresult");
        TextView tv = (TextView) findViewById(R.id.scanResultText);
        tv.setText(scanresult);
        setDecryptandDecodeListener();
    }

    private void setDecryptandDecodeListener() {
        Button decryptbutton = (Button) findViewById(R.id.decodeANDdecrypt);
        decryptbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = operationDecoding();
                TextView tv = (TextView) findViewById(R.id.scanResultText);
                tv.setText(result);
            }
        });
    }

    public String operationDecoding(){
        KeyPair kp= processing.getKeypair();
        String original = processing.decryption(scanresult, kp.getPrivate());
        return original;
    }
}
