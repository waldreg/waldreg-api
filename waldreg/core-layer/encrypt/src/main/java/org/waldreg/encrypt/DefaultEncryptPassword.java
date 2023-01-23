package org.waldreg.encrypt;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class DefaultEncryptPassword implements EncryptPassword{

    @Override
    public String createSalt(){
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[20];

        secureRandom.nextBytes(salt);

        StringBuffer buffer = new StringBuffer();
        for (byte b : salt){
            buffer.append(String.format("%02x", b));
        }

        return buffer.toString();
    }

    @Override
    public String getEncryptPassword(String password, String salt){
        String result;
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update((password + salt).getBytes());
            byte[] encryptedPassword = messageDigest.digest();
            result = byteToString(encryptedPassword);
        } catch (NoSuchAlgorithmException NSAE){
            throw new RuntimeException(NSAE);
        }
        return result;
    }

    private String byteToString(byte[] passwordSalt){
        StringBuffer buffer = new StringBuffer();
        for (byte b : passwordSalt){
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

}
