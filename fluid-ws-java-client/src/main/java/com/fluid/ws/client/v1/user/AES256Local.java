package com.fluid.ws.client.v1.user;

import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.ArrayUtils;

import com.fluid.ws.client.FluidClientException;


/**
 * Created by jasonbruwer on 15/01/01.
 */
public class AES256Local {

    public static final int IV_SIZE_BYTES = 16;
    public static final int SEED_SIZE_BYTES = 32;

    private static final String HMAC_ALGO = "HmacSHA256";

    private static final String ALGO_CBC = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGO = "AES";

    private static SecureRandom secureRandom;

    /**
     *
     * @param seedParam
     * @return
     */
    public static byte[] generateRandom(int seedParam) {

        if(AES256Local.secureRandom == null)
        {
            AES256Local.secureRandom = new SecureRandom();
        }

        return new IvParameterSpec(AES256Local.secureRandom.generateSeed(seedParam)).getIV();
    }

    /**
     *
     * @param hMacKeyParam
     * @param encryptedDataParam
     * @return
     */
    public static byte[] hmacSha256(byte[] hMacKeyParam, byte[] encryptedDataParam) {
        try {
            // hmac
            Mac hmac = Mac.getInstance(HMAC_ALGO);
            hmac.init(new SecretKeySpec(hMacKeyParam, HMAC_ALGO));
            byte[] signature = hmac.doFinal(encryptedDataParam);

            return signature;
        }
        //
        catch (InvalidKeyException | NoSuchAlgorithmException except) {
            throw new FluidClientException("Unable to create HMAC from key. " + except.getMessage(), except,
                    FluidClientException.ErrorCode.AES_256);
        }
    }

    /**
     *
     * @param encryptedDataParam
     * @param passwordParam Password (clear text) used to derive the key from.
     * @param saltParam The password salt as passed by the Init request.
     * @param seedParam Seed to be poisoned
     * @return
     */
    public static byte[] generateLocalHMAC(byte[] encryptedDataParam, String passwordParam, String saltParam, byte[] seedParam){
        byte[] poisonedSeed = poisonBytes(seedParam);

        byte[] passwordSha256 = sha256(
                passwordParam.concat(saltParam).getBytes());

        //Add the seed to the password and SHA-256...
        byte[] derivedKey = sha256(ArrayUtils.addAll(passwordSha256, poisonedSeed));

        return hmacSha256(derivedKey, encryptedDataParam);
    }

    /**
     *
     * @param encryptedDataParam
     * @param keyParam Key used to derive the key from.
     * @param seedParam Seed to be poisoned
     * @return
     */
    public static byte[] generateLocalHMACForReqToken(byte[] encryptedDataParam, byte[] keyParam, byte[] seedParam){
        byte[] poisonedSeed = poisonBytes(seedParam);

        //Add the seed to the password and SHA-256...
        byte[] derivedKey = sha256(ArrayUtils.addAll(keyParam, poisonedSeed));

        return hmacSha256(derivedKey, encryptedDataParam);
    }

    /**
     *
     * @param bytesToPoisonParam
     * @return
     */
    private static byte[] poisonBytes(byte[] bytesToPoisonParam) {
        if (bytesToPoisonParam == null) {
            return null;
        }

        byte[] returnVal = new byte[bytesToPoisonParam.length];

        for (int index = 0; index < bytesToPoisonParam.length; index++) {
            byte poisoned = (byte) (bytesToPoisonParam[index] ^ 222);

            returnVal[index] = poisoned;
        }

        return returnVal;
    }

    /**
     *
     * @param encryptedDataParam The Base64 data to decrypt.
     * @param passwordParam Password in the clear.
     * @param saltParam The password salt as passed by the Init request.
     * @param ivParam IV value used during packet encryption
     * @param seedParam
     * @return
     */
    public static byte[] decryptInitPacket(
            byte[] encryptedDataParam,
            String passwordParam,
            String saltParam,
            byte[] ivParam,
            byte[] seedParam){

        //Stored like this in the database, so we have to get the password as stored in the database so that the
        // SHa256 and SALT combination will be valid...
        byte[] passwordSha256 = sha256(passwordParam.concat(saltParam).getBytes());

        //Add the seed to the password and SHA-256...
        byte[] derivedKey = sha256(ArrayUtils.addAll(passwordSha256, seedParam));

        //
        return decrypt(derivedKey, encryptedDataParam, ivParam);
    }

    /**
     *
     * @param keyParam
     * @param dataToDecryptParam
     * @param ivParam
     * @return
     */
    public static byte[] decrypt(byte[] keyParam, byte[] dataToDecryptParam, byte[] ivParam) {

        Key key = new SecretKeySpec(keyParam, KEY_ALGO);

        try {
            Cipher cipher = Cipher.getInstance(ALGO_CBC);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivParam));

            return cipher.doFinal(dataToDecryptParam);
        }
        //
        catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException |
                NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException except) {

            throw new FluidClientException("Unable to decrypt data. " +
                    except.getMessage(), except, FluidClientException.ErrorCode.AES_256);
        }
    }

    /**
     *
     * @param keyParam
     * @param dataToEncryptParam
     * @param ivParam - 16 bytes for AES-256.
     * @return
     */
    public static byte[] encrypt(byte[] keyParam, byte[] dataToEncryptParam, byte[] ivParam) {
        if (dataToEncryptParam == null) {
            throw new FluidClientException("No data to encrypt provided. ",
                    FluidClientException.ErrorCode.AES_256);
        }

        Key key = new SecretKeySpec(keyParam, KEY_ALGO);

        try {
            Cipher cipher = Cipher.getInstance(ALGO_CBC);

            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivParam));

            return cipher.doFinal(dataToEncryptParam);
        }
        //
        catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException except) {
            throw new FluidClientException("Unable to encrypt data. " + except.getMessage(), except,
                    FluidClientException.ErrorCode.AES_256);
        }
    }


    /**
     * Compute SHA256 digest
     *
     * @param data
     * @return SHA256 digest
     */
    public static byte[] sha256(final byte[] data) {
        if (data == null || data.length == 0) {
            return new byte[] {};
        }

        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data);
        }
        //
        catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
