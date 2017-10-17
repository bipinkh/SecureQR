package com.bipinkh.secureqr;

import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;
/**
 * Created by bipin on 10/17/2017.
 */

public class aesCipher {

    SecretKeySpec myKey;
    IvParameterSpec ivParameterSpec;

    //constructor for iv generation
    public aesCipher(int ivSize)
    {
        byte[] iv = new byte[ivSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        ivParameterSpec = new IvParameterSpec(iv);
    }

    // key generation
    public void generateKey(String password) throws Exception
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-256"); //hash functions
        digest.update(password.getBytes("UTF-8"));
        byte[] keyBytes = new byte[16];
        //digest.digest(byte of string x) gives hash value
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
        myKey = new SecretKeySpec(keyBytes, "AES");
    }

    //encryption method
    public byte[] encryption(String PlainText, String Algorithm) throws Exception
    {
        Cipher cipher = Cipher.getInstance(Algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, myKey, ivParameterSpec );
        byte[] byteResult = cipher.doFinal(PlainText.getBytes());
        String encodedResult = Base64.encodeToString(byteResult, Base64.DEFAULT);
        return byteResult;
    }

    //decryption method
    public String decryption(byte[] ciphertext, String Algorithm) throws Exception
    {
        Cipher cipher = Cipher.getInstance(Algorithm);
        cipher.init(Cipher.DECRYPT_MODE,myKey, ivParameterSpec);
        byte[] originalMessage = cipher.doFinal(ciphertext);
        return new String(originalMessage);
    }
}
