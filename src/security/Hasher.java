/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import sun.misc.BASE64Encoder;

/**
 *
 * @author miguelgomez
 */
public class Hasher {
    public static final String SHA256 = "SHA-256";
    
    protected MessageDigest hashGenerator;
    
    public Hasher(String algorithm) throws NoSuchAlgorithmException{
        hashGenerator = MessageDigest.getInstance(algorithm);
    }
    
    public void resetHasher(){
        hashGenerator.reset();
    }
    
    public void updateHash(String value,String charset) throws UnsupportedEncodingException{
        hashGenerator.update(value.getBytes(charset));
    }
    
    public void updateHash(byte[] value){
        hashGenerator.update(value);
    }
    
    public byte[] getHashBytes(){
        return hashGenerator.digest();
    }
    
    public String getHashBASE64(){
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(hashGenerator.digest());
    }
}
