package com.bipinkh.secureqr;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

/**
 * Created by bipin on 10/16/2017.
 */

public class rsaCipher {

    public static KeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException
    {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        return keyPair;
    }

    public static byte[] encryption(String message, PublicKey publicKey) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKey = cipher.doFinal(message.getBytes());
        return encryptedKey;
    }

    public static byte[] decryption(byte[] encryptedKey, PrivateKey privateKey) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] symmetricKey = cipher.doFinal(encryptedKey);
        return symmetricKey;
    }

}
