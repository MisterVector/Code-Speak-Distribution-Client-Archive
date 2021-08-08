package org.codespeak.distribution.client.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * Takes the specified parent folder and file and checks if the file
     * resolved to the parent folder exists, looping until the file no
     * longer exists, and is then returned
     * @param parentFolder parent directory to be resolved against a file
     * @param filename file (without the extension) to be resolved with parent
     * directory
     * @return new path including the parent directory and file that represents
     * a file that does not exist
     */
    public static Path getNonExistentPath(Path parentFolder, String filename) {
        boolean exists = false;
        int nextNumber = 2;
        Path fullPath = null;
        String newFile = filename;
        
        do {
            fullPath = parentFolder.resolve(newFile);
            exists = fullPath.toFile().exists();
            
            if (exists) {
                String fileWithoutExtension = filename.substring(0, filename.lastIndexOf("."));
                String ext = filename.substring(filename.lastIndexOf(".") + 1);

                newFile = fileWithoutExtension + " (" + nextNumber + ")" + "." + ext;
                nextNumber++;
            }
        } while (exists);
        
        return fullPath;
    }

    /**
     * Ensures that the specified path exists by making sure that the directory
     * structure is created if not all of it exists
     * @param path the path to ensure that it exists
     */
    public static void ensurePathExists(Path path) {
        File file = path.toFile();

        if (!file.exists()) {
            file.mkdirs();
        }
    }
    
    /**
     * Checks if the specified file is a non-empty text file
     * @param programFilePath the path to the program file being checked
     * @return if the specified file is a non-empty text file
     */
    public static boolean isNonEmptyTextFile(Path programFilePath) {
        if (programFilePath.toFile().length() > 0) {
            String filename = programFilePath.getFileName().toString();
            int lastIndex = filename.lastIndexOf(".");
            
            if (lastIndex > -1) {
                String ext = filename.substring(lastIndex + 1).toLowerCase();

                return (ext.equals("txt") || ext.equals("log") || ext.equals("ini")
                        || ext.equals("rtf"));                            
            }
        }

        return false;
    }

    /**
     * Creates a process builder and returns it
     * @param processPath path to the process
     * @return process builder made from the process path
     */
    public static ProcessBuilder createProcessBuilder(Path processPath) {
        Path absoluteProcessPath = processPath.toAbsolutePath();
        Path directory = absoluteProcessPath.getParent();
        String absoluteProcessPathRaw = absoluteProcessPath.toString();

        List<String> commands = new ArrayList<String>();

        if (absoluteProcessPathRaw.endsWith(".jar")) {
            commands.add("java");
            commands.add("-jar");
        }

        commands.add(absoluteProcessPathRaw);
        commands.add("--csds-launch");

        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(directory.toFile());

        return pb;
    }
    
}
