package com.example.sipappmerge.Utils;

import com.google.common.hash.Hashing;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Formatter;
import java.util.Set;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA3_256;

public class ShaUtilss {
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final String OUTPUT_FORMAT = "%-20s:%s";


    public static String encryptPassword(String password)
    {
        String sha1 = "";
        /*try
        {*/

           /* Keccak.Digest256 digest256 = new Keccak.Digest256();
        byte[] hashbytes = digest256.digest(
                password.getBytes(StandardCharsets.UTF_8));
        String sha3_256hex = new String(Hex.encode(hashbytes));*/
            /*Set<String> messageDigest = Security.getAlgorithms("MessageDigest");
            System.out.println(messageDigest.toString());

            MessageDigest crypt = MessageDigest.getInstance("SHA-256");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());*/
            String sha256hex = Hashing.sha256()
                    .hashString(password, StandardCharsets.UTF_8)
                    .toString();

     /*   }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }*/
        return sha256hex;
    }

    public static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
