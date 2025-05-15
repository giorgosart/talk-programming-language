package talk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import talk.io.FileSystem;

/**
 * A mock implementation of FileSystem for testing purposes.
 * This allows tests to run without touching the actual file system.
 */
public class MockFileSystem implements FileSystem {
    private final Map<String, String> files = new HashMap<>();
    private final Map<String, List<String>> directories = new HashMap<>();
    private final List<String> operations = new ArrayList<>();
    
    /**
     * Records all operations performed for testing verification
     */
    public List<String> getOperations() {
        return operations;
    }
    
    /**
     * Clears all recorded operations and file contents
     */
    public void reset() {
        operations.clear();
        files.clear();
        directories.clear();
    }
    
    /**
     * Get direct access to the mock file contents
     */
    public Map<String, String> getFiles() {
        return files;
    }
    
    @Override
    public boolean fileExists(String fileName) {
        operations.add("fileExists:" + fileName);
        return files.containsKey(fileName);
    }
    
    @Override
    public String readFile(String fileName) throws IOException {
        operations.add("readFile:" + fileName);
        if (!files.containsKey(fileName)) {
            throw new IOException("File not found: " + fileName);
        }
        return files.get(fileName);
    }
    
    @Override
    public void writeFile(String fileName, String content) throws IOException {
        operations.add("writeFile:" + fileName);
        files.put(fileName, content);
    }
    
    @Override
    public void appendToFile(String fileName, String content) throws IOException {
        operations.add("appendToFile:" + fileName);
        if (files.containsKey(fileName)) {
            files.put(fileName, files.get(fileName) + content);
        } else {
            files.put(fileName, content);
        }
    }
    
    @Override
    public void deleteFile(String fileName) throws IOException {
        operations.add("deleteFile:" + fileName);
        files.remove(fileName);
    }
    
    @Override
    public void copyFile(String source, String destination) throws IOException {
        operations.add("copyFile:" + source + ":" + destination);
        if (!files.containsKey(source)) {
            throw new IOException("Source file not found: " + source);
        }
        files.put(destination, files.get(source));
    }
    
    @Override
    public String[] listDirectory(String directoryPath) throws IOException {
        operations.add("listDirectory:" + directoryPath);
        if (!directories.containsKey(directoryPath)) {
            throw new IOException("Directory not found: " + directoryPath);
        }
        List<String> files = directories.get(directoryPath);
        return files.toArray(new String[0]);
    }
    
    /**
     * Setup a directory with mock files
     */
    public void setupDirectory(String directoryPath, List<String> fileNames) {
        directories.put(directoryPath, new ArrayList<>(fileNames));
    }
}
