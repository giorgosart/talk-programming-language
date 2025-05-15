package talk.io;

import java.io.IOException;

/**
 * Defines the file operations interface for the Talk programming language.
 * This interface enables dependency injection and better testability 
 * by abstracting away the actual file system implementation.
 */
public interface FileSystem {
    /**
     * Checks if a file exists
     * @param fileName The name of the file to check
     * @return true if the file exists, false otherwise
     */
    boolean fileExists(String fileName);
    
    /**
     * Reads the content of a file as a string
     * @param fileName The name of the file to read
     * @return The content of the file
     * @throws IOException If an I/O error occurs
     */
    String readFile(String fileName) throws IOException;
    
    /**
     * Writes content to a file, creating it if it doesn't exist and 
     * overwriting it if it does
     * @param fileName The name of the file to write to
     * @param content The content to write
     * @throws IOException If an I/O error occurs
     */
    void writeFile(String fileName, String content) throws IOException;
    
    /**
     * Appends content to a file, creating it if it doesn't exist
     * @param fileName The name of the file to append to
     * @param content The content to append
     * @throws IOException If an I/O error occurs
     */
    void appendToFile(String fileName, String content) throws IOException;
    
    /**
     * Deletes a file if it exists
     * @param fileName The name of the file to delete
     * @throws IOException If an I/O error occurs
     */
    void deleteFile(String fileName) throws IOException;
    
    /**
     * Copies a file from source to destination
     * @param source The source file
     * @param destination The destination file
     * @throws IOException If an I/O error occurs
     */
    void copyFile(String source, String destination) throws IOException;
    
    /**
     * Lists files in a directory
     * @param directoryPath The directory to list
     * @return Array of file names in the directory
     * @throws IOException If an I/O error occurs
     */
    String[] listDirectory(String directoryPath) throws IOException;
}
