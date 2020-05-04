package org.codespeak.distribution.client.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

/**
 * A class that contains miscellaneous utility functions
 *
 * @author Vector
 */
public class MiscUtil {

    private static MessageDigest messageDigest = null;
    
    private static MessageDigest getMessageDigest() throws NoSuchAlgorithmException {
        if (messageDigest == null) {
            messageDigest = MessageDigest.getInstance("MD5");
        }
        
        return messageDigest;
    }

    /**
     * Gets the checksum of the specified path
     * @param path path to file
     * @return checksum of the file as specified path
     */
    public static String getFileChecksum(Path path) {
        try {
            MessageDigest messageDigest = getMessageDigest();
            messageDigest.update(Files.readAllBytes(path));
            byte[] checksumBytes = messageDigest.digest();
            messageDigest.reset();
            
            return DatatypeConverter.printHexBinary(checksumBytes);
        } catch (IOException | NoSuchAlgorithmException ex) {
            
        }
        
        return null;
    }

}
