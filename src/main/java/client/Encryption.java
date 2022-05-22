package client;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author faded828x
 * @date 2022/5/15
 */
public class Encryption {

    // encrypt-AES/CBC Base64编码
    public static String encrypt(String data, String key, String iv) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"), new IvParameterSpec(iv.getBytes()));
            byte[] result = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    // Base64解码 decrypt-AES/CBC
    public static String decrypt(String cip, String key, String iv) {
        byte[] data = Base64.getDecoder().decode(cip);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"), new IvParameterSpec(iv.getBytes()));
            return new String(cipher.doFinal(data));
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    // hash-SHA1 Base64编码
    public static String sha(String data) {
        MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance("SHA1");
            return Base64.getEncoder().encodeToString(sha1.digest(data.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "fail";
        }

    }

    public static void main(String[] args) throws IOException {
        String cipher = encrypt(Files.readString(Paths.get("/Users/faded828x/aest/AllNodesDistanceKInBinaryTree863.java")), "123456789abcdefg", "iviviviviviviviv"); // key 长度只能是 16、24 或 32 字节
        System.out.println(cipher);
        System.out.println(decrypt(cipher, "123456789abcdefg", "iviviviviviviviv"));
//        System.out.println(sha("hello"));
    }

}
