package com.bipinkh.secureqr;

import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by bipin on 10/17/2017.
 */

public class processing {
    static KeyPair keypair = null;

    public static KeyPair getKeypair(){
        if (keypair==null){
            try {
                keypair = rsaCipher.generateKeyPair(2048);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return keypair;
    }


    public static String encryption(String message, PublicKey pubkey){
        int ivSize = 16;
        PublicKey p = pubkey;
        byte[] encryptedByte= null;
                try {
                    Log.v("datsun","called here");
                    encryptedByte = rsaCipher.encryption(message,p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        return Base64.encodeToString(encryptedByte, Base64.DEFAULT);
    }

    public static String decryption(String message, PrivateKey prvKey){
        PrivateKey p = prvKey;
        String result = null;
        byte[] byteText = Base64.decode(message, Base64.DEFAULT);
        try {
            result = rsaCipher.decryption(byteText,prvKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
