package com.guo.androidlib.bitmap;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Home
 */
public class HomeMD5 {
    private static HomeMD5 homeMD5;
    
    private HomeMD5() {
    }
    
    public static HomeMD5 getInstance(){
    	if(homeMD5 == null){
    		homeMD5 = new HomeMD5();
    	}
    	return homeMD5;
    }
    
    public String generate(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
