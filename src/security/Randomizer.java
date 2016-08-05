/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.owasp.esapi.errors.EncryptionException;

/**
 * This class is used to securely generate random values,
 * @author miguelgomez
 */
public class Randomizer implements org.owasp.esapi.Randomizer{
    protected static final String SHA1PRNG = "SHA1PRNG";
    
    protected SecureRandom randomGenerator;

    /**
     * 
     * @param seed
     * @throws NoSuchAlgorithmException 
     */
    public Randomizer(long seed) throws NoSuchAlgorithmException{
        randomGenerator = SecureRandom.getInstance(SHA1PRNG);
        randomGenerator.setSeed(seed);
    }
    
    @Override
    public String getRandomString(int i, char[] chars) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getRandomBoolean() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getRandomInteger(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 
     * @return 
     */
    @Override
    public long getRandomLong() {
        return this.randomGenerator.nextLong();
    }

    @Override
    public String getRandomFilename(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public float getRandomReal(float f, float f1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getRandomGUID() throws EncryptionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public byte[] getRandomBytes(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
