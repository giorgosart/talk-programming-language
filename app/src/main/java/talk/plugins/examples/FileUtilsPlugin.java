package talk.plugins.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Example plugin that provides file-related utilities.
 */
public class FileUtilsPlugin {
    
    /**
     * Count the number of lines in a file
     * @param args The file path as the first argument
     * @return The number of lines in the file
     * @throws IOException If an I/O error occurs
     */
    public static int countLinesInFile(Object... args) throws IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException("File path is required");
        }
        
        String filePath = args[0].toString();
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new IOException("File does not exist: " + filePath);
        }
        
        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.readLine() != null) {
                lineCount++;
            }
        }
        
        return lineCount;
    }
    
    /**
     * Get file extension
     * @param args The file path as the first argument
     * @return The file extension or empty string if no extension
     */
    public static String getFileExtension(Object... args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("File path is required");
        }
        
        String fileName = args[0].toString();
        int lastDotIndex = fileName.lastIndexOf(".");
        
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        
        return fileName.substring(lastDotIndex + 1);
    }
    
    /**
     * Get file size in bytes
     * @param args The file path as the first argument
     * @return The size of the file in bytes
     * @throws IOException If the file doesn't exist or can't be accessed
     */
    public static long getFileSize(Object... args) throws IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException("File path is required");
        }
        
        String filePath = args[0].toString();
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new IOException("File does not exist: " + filePath);
        }
        
        return file.length();
    }
}
