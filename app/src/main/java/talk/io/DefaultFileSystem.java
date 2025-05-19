package talk.io;

import java.io.File;
import java.io.IOException;

/**
 * Default implementation of the FileSystem interface that uses
 * the existing FileUtils static methods for file operations.
 */
public class DefaultFileSystem implements FileSystem {
    
    @Override
    public boolean fileExists(String fileName) {
        return FileUtils.fileExists(fileName);
    }
    
    @Override
    public String readFile(String fileName) throws IOException {
        return FileUtils.readFile(fileName);
    }
    
    @Override
    public java.util.List<String> readAllLines(String fileName) throws IOException {
        return FileUtils.readAllLines(fileName);
    }
    
    @Override
    public void writeFile(String fileName, String content) throws IOException {
        FileUtils.writeFile(fileName, content);
    }
    
    @Override
    public void appendToFile(String fileName, String content) throws IOException {
        FileUtils.appendToFile(fileName, content);
    }
    
    @Override
    public void deleteFile(String fileName) throws IOException {
        FileUtils.deleteFile(fileName);
    }
    
    @Override
    public void copyFile(String source, String destination) throws IOException {
        FileUtils.copyFile(source, destination);
    }
    
    @Override
    public String[] listDirectory(String directoryPath) throws IOException {
        File dir = new File(directoryPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("Directory '" + directoryPath + "' does not exist or is not a directory");
        }
        String[] files = dir.list();
        return files != null ? files : new String[0];
    }
}
