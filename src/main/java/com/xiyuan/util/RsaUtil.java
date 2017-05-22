package com.xiyuan.util;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * Created by xiyuan_fengyu on 2017/5/22.
 */
public class RsaUtil {

    private static final String RSA = "RSA";

    private static final KeyFactory rsaKeyFactory;

    static {
        KeyFactory temp;
        try {
            temp = KeyFactory.getInstance(RSA);
        } catch (NoSuchAlgorithmException e) {
            temp = null;
        }
        rsaKeyFactory = temp;
    }

    private static KeyPair generateKey() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA);
            generator.initialize(1024);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createAndSave(String savePath, String prefix) {
        KeyPair keyPair = generateKey();
        if (keyPair != null) {
            File saveDir = new File(savePath);
            if ((!saveDir.exists() && saveDir.mkdirs()) || saveDir.isDirectory()) {
                try (FileOutputStream out = new FileOutputStream(new File(savePath + "/" + prefix + "_private.key"))) {
                    out.write(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try (FileOutputStream out = new FileOutputStream(new File(savePath + "/" + prefix + "_public.key"))) {
                    out.write(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static RSAPublicKey loadPublicKey(String keyPath) {
        try (FileInputStream in = new FileInputStream(new File(keyPath))) {
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            X509EncodedKeySpec x509 = new X509EncodedKeySpec(Base64.decodeBase64(bytes));
            return (RSAPublicKey) rsaKeyFactory.generatePublic(x509);
        } catch (IOException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RSAPrivateKey loadPrivateKey(String keyPath) {
        try (FileInputStream in = new FileInputStream(new File(keyPath))) {
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(bytes));
            return (RSAPrivateKey) rsaKeyFactory.generatePrivate(pkcs8);
        } catch (IOException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encrypt(byte[] source, RSAPublicKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            ByteOutputStream out = new ByteOutputStream();
            for (int i = 0, len = source.length; i < len; i += 117) {
                out.write(cipher.doFinal(source, i, Math.min(i + 117, len) - i));
            }
            return Arrays.copyOf(out.getBytes(), out.getCount());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encryptToBase64(String source, RSAPublicKey key) {
        if (source == null) return null;
        byte[] bytes = encrypt(source.getBytes(StandardCharsets.UTF_8), key);
        return bytes != null ? Base64.encodeBase64String(bytes) : null;
    }

    public static byte[] decrypt(byte[] from, RSAPrivateKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            ByteOutputStream out = new ByteOutputStream();
            for (int i = 0, len = from.length; i < len; i += 128) {
                out.write(cipher.doFinal(from, i, Math.min(i + 128, len) - i));
            }
            return Arrays.copyOf(out.getBytes(), out.getCount());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptFromBase64(String from, RSAPrivateKey key) {
        if (from == null) return null;
        byte[] bytes = decrypt(Base64.decodeBase64(from), key);
        return bytes != null ? new String(bytes, StandardCharsets.UTF_8) : null;
    }

    public static void main(String[] args) {
//        createAndSave("./target", "test");
        RSAPrivateKey privateKey = loadPrivateKey("./target/test_private.key");
        RSAPublicKey publicKey = loadPublicKey("./target/test_public.key");
//        String str = "nGGJDZNHMZrItYlfnd9NZNYiXQwGF356428bPM/R5X/ugtQeiSiQKh4p3Tach5KK/19bwu3BDpG6UJd4SLip6rnIVTgi20fE8AaZUWosMaTuauDoLiUvN98aPT684JuQlO4B/h1O9+F32PqlIer2N9KertPkhKi/uo48nOenWa8=";
        String str = "123";
        String encryptedStr = encryptToBase64(str, publicKey);
        System.out.println(encryptedStr);
        System.out.println(decryptFromBase64(encryptedStr, privateKey));

//        RSAPrivateKey privateKey = loadPrivateKey("./target/test_private.key");
//        String str = "CbwW37/erjc0x7fcGWp9uJVyLl/TamFoBPoV2l7fhIbRULDmpI1aIUZz6SpNIvsBkLMspl4ojaQgSNEDDymZqThH/9BPdqsmLHIBf7609NP94ykx/w4hBtTFrcbe2JS0AZc8PeoyIooHZXcEpS9I/ivgltjJkOq9nYDdrdoWnkA=";
//        System.out.println(decryptFromBase64(str, privateKey));
    }

}