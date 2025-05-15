package talk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import talk.io.DefaultFileSystem;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

class DefaultFileSystemTest {

    @TempDir
    Path tempDir;
    
    private DefaultFileSystem fileSystem;
    private Path testFile;
    private Path sourceFile;
    private Path destFile;
    private Path nonExistentFile;
    private Path subDir;
    
    @BeforeEach
    void setUp() throws IOException {
        fileSystem = new DefaultFileSystem();
        testFile = tempDir.resolve("test.txt");
        sourceFile = tempDir.resolve("source.txt");
        destFile = tempDir.resolve("dest.txt");
        nonExistentFile = tempDir.resolve("nonexistent.txt");
        subDir = tempDir.resolve("subdir");
        
        Files.createDirectory(subDir);
        Files.writeString(testFile, "test content");
        Files.writeString(sourceFile, "source content");
        
        // Create a few files in subdir for listDirectory test
        Files.writeString(subDir.resolve("file1.txt"), "file1 content");
        Files.writeString(subDir.resolve("file2.txt"), "file2 content");
    }
    
    @Test
    void testFileExists() {
        assertTrue(fileSystem.fileExists(testFile.toString()));
        assertFalse(fileSystem.fileExists(nonExistentFile.toString()));
    }
    
    @Test
    void testReadFile() throws IOException {
        String content = fileSystem.readFile(testFile.toString());
        assertEquals("test content", content);
    }
    
    @Test
    void testReadNonExistentFile() {
        assertThrows(IOException.class, () -> 
            fileSystem.readFile(nonExistentFile.toString())
        );
    }
    
    @Test
    void testWriteFile() throws IOException {
        String newContent = "new content";
        fileSystem.writeFile(testFile.toString(), newContent);
        
        // Verify the content was written
        String content = Files.readString(testFile);
        assertEquals(newContent, content);
    }
    
    @Test
    void testWriteToNewFile() throws IOException {
        String content = "new file content";
        fileSystem.writeFile(nonExistentFile.toString(), content);
        
        // Verify the file was created with the correct content
        assertTrue(Files.exists(nonExistentFile));
        assertEquals(content, Files.readString(nonExistentFile));
    }
    
    @Test
    void testAppendToFile() throws IOException {
        String appendedContent = "\nappended content";
        fileSystem.appendToFile(testFile.toString(), appendedContent);
        
        // Verify the content was appended
        String content = Files.readString(testFile);
        assertEquals("test content" + appendedContent, content);
    }
    
    @Test
    void testAppendToNonExistentFile() throws IOException {
        String content = "new content";
        fileSystem.appendToFile(nonExistentFile.toString(), content);
        
        // Verify the file was created with the content
        assertTrue(Files.exists(nonExistentFile));
        assertEquals(content, Files.readString(nonExistentFile));
    }
    
    @Test
    void testDeleteFile() throws IOException {
        assertTrue(Files.exists(testFile));
        fileSystem.deleteFile(testFile.toString());
        assertFalse(Files.exists(testFile));
    }
    
    @Test
    void testDeleteNonExistentFile() throws IOException {
        // Should not throw an exception
        fileSystem.deleteFile(nonExistentFile.toString());
    }
    
    @Test
    void testCopyFile() throws IOException {
        fileSystem.copyFile(sourceFile.toString(), destFile.toString());
        
        // Verify the file was copied correctly
        assertTrue(Files.exists(destFile));
        assertEquals("source content", Files.readString(destFile));
    }
    
    @Test
    void testCopyNonExistentFile() {
        assertThrows(IOException.class, () -> 
            fileSystem.copyFile(nonExistentFile.toString(), destFile.toString())
        );
    }
    
    @Test
    void testListDirectory() throws IOException {
        String[] files = fileSystem.listDirectory(subDir.toString());
        
        // Should contain exactly two files
        assertEquals(2, files.length);
        List<String> fileList = Arrays.asList(files);
        assertTrue(fileList.contains("file1.txt"));
        assertTrue(fileList.contains("file2.txt"));
    }
    
    @Test
    void testListNonExistentDirectory() {
        Path nonExistentDir = tempDir.resolve("nonexistentdir");
        assertThrows(IOException.class, () -> 
            fileSystem.listDirectory(nonExistentDir.toString())
        );
    }
    
    @Test
    void testListFile() {
        // Trying to list a file instead of a directory should throw IOException
        assertThrows(IOException.class, () -> 
            fileSystem.listDirectory(testFile.toString())
        );
    }
}
