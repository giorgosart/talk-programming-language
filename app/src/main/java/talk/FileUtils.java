package talk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileUtils {
    public static boolean fileExists(String fileName) {
        return Files.exists(Path.of(fileName));
    }

    public static String readFile(String fileName) throws IOException {
        return Files.readString(Path.of(fileName));
    }

    public static void writeFile(String fileName, String content) throws IOException {
        Files.writeString(Path.of(fileName), content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void appendToFile(String fileName, String content) throws IOException {
        Files.writeString(Path.of(fileName), content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public static void deleteFile(String fileName) throws IOException {
        Files.deleteIfExists(Path.of(fileName));
    }

    public static void copyFile(String source, String destination) throws IOException {
        Files.copy(Path.of(source), Path.of(destination), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }
}
