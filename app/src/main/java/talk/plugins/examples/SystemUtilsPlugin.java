package talk.plugins.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Example plugin that provides system-related utilities.
 */
public class SystemUtilsPlugin {
    /**
     * Get the computer's hostname
     * @param args No arguments expected
     * @return The hostname of the computer
     */
    public static String getComputerName(Object... args) {
        try {
            java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
            return localMachine.getHostName();
        } catch (Exception e) {
            return "Unknown host: " + e.getMessage();
        }
    }
    
    /**
     * List running processes on the system
     * @param args Optional: maximum number of processes to return
     * @return List of process names
     */
    public static List<String> listProcesses(Object... args) throws IOException {
        List<String> processes = new ArrayList<>();
        Process p;
        String osName = System.getProperty("os.name").toLowerCase();
        
        // Determine the right command based on OS
        if (osName.contains("win")) {
            p = Runtime.getRuntime().exec("tasklist");
        } else if (osName.contains("mac")) {
            p = Runtime.getRuntime().exec("ps -e");
        } else {
            p = Runtime.getRuntime().exec("ps -e");
        }
        
        // Read the process output
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            // Skip the header line
            reader.readLine();
            
            // Determine the limit
            int limit = Integer.MAX_VALUE;
            if (args.length > 0) {
                try {
                    limit = Integer.parseInt(args[0].toString());
                } catch (NumberFormatException e) {
                    // Ignore, use default
                }
            }
            
            // Read processes up to the limit
            int count = 0;
            while (count < limit && (line = reader.readLine()) != null) {
                String process = extractProcessName(line, osName);
                if (process != null && !process.isEmpty()) {
                    processes.add(process);
                    count++;
                }
            }
        }
        
        return processes;
    }
    
    /**
     * Extract the process name from a line of output based on OS
     */
    private static String extractProcessName(String line, String osName) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        if (osName.contains("win")) {
            // Windows format: name is the first part
            String[] parts = line.trim().split("\\s+");
            if (parts.length > 0) {
                return parts[0];
            }
        } else {
            // Unix-like format: name is typically the last part
            String[] parts = line.trim().split("\\s+");
            if (parts.length > 0) {
                return parts[parts.length - 1];
            }
        }
        
        return line.trim();
    }
}
