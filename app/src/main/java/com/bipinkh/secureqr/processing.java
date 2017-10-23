package com.bipinkh.secureqr;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by bipin on 10/17/2017.
 */

public class processing {


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
