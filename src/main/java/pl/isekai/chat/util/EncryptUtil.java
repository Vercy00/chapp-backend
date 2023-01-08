package pl.isekai.chat.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class EncryptUtil {
    private final static String ALGORITHM = "RSA";

    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
        generator.initialize(4096);
        return generator.generateKeyPair();
    }

    public static String encryptString(String stringToEncrypt, PublicKey publicKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException {
        Cipher encryptCipher = Cipher.getInstance(ALGORITHM + "/ECB/OAEPPadding");
        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1",
                new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);

        byte[] secretMessageBytes = stringToEncrypt.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);

        return Base64.getEncoder().encodeToString(encryptedMessageBytes);
    }

    public static String decryptString(String stringToDecrypt, PrivateKey privateKey) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            InvalidAlgorithmParameterException {
        Cipher decryptCipher = Cipher.getInstance(ALGORITHM + "/ECB/OAEPPadding");
        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1",
                new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);

        byte[] encryptedMessageBytes = Base64.getDecoder().decode(stringToDecrypt);
        byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);

        return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
    }

    public static String keyToString(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static Key stringToKey(String stringKey, Class<? extends Key> keyClass) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        byte[] keyBytes = Base64.getDecoder().decode(stringKey);
        EncodedKeySpec keySpec;

        if (keyClass.equals(PrivateKey.class)) {
            keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return keyFactory.generatePrivate(keySpec);
        } else {
            keySpec = new X509EncodedKeySpec(keyBytes);
            return keyFactory.generatePublic(keySpec);
        }
    }
}

