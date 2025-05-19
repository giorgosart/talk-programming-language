package talk.plugins.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Example plugin that provides network-related utilities.
 */
public class NetworkUtilsPlugin {
    private static final int DEFAULT_TIMEOUT = 5000; // 5 seconds
    
    /**
     * Read content from a URL
     * @param args The URL as the first argument, optional timeout in ms as second argument
     * @return The content of the URL as string
     * @throws IOException If an I/O error occurs
     */
    public static String readUrl(Object... args) throws IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException("URL is required");
        }
        
        String urlStr = args[0].toString();
        int timeout = DEFAULT_TIMEOUT;
        
        if (args.length > 1) {
            try {
                timeout = Integer.parseInt(args[1].toString());
            } catch (NumberFormatException e) {
                // Use default timeout
            }
        }
        
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("HTTP error code: " + responseCode);
        }
        
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } finally {
            connection.disconnect();
        }
        
        return content.toString();
    }
    
    /**
     * Encode a string for use in URLs
     * @param args The string to encode as the first argument
     * @return The URL-encoded string
     */
    public static String urlEncode(Object... args) throws IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException("String to encode is required");
        }
        
        String input = args[0].toString();
        return URLEncoder.encode(input, StandardCharsets.UTF_8.toString());
    }
    
    /**
     * Ping a host to check if it's reachable
     * @param args The hostname as the first argument, optional timeout in ms as second argument
     * @return A map containing 'reachable' (boolean) and 'time' (milliseconds)
     */
    public static Map<String, Object> pingHost(Object... args) throws IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Hostname is required");
        }
        
        String hostname = args[0].toString();
        int timeout = 1000; // Default 1 second
        
        if (args.length > 1) {
            try {
                timeout = Integer.parseInt(args[1].toString());
            } catch (NumberFormatException e) {
                // Use default timeout
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        
        long startTime = System.currentTimeMillis();
        boolean reachable = java.net.InetAddress.getByName(hostname).isReachable(timeout);
        long endTime = System.currentTimeMillis();
        
        result.put("reachable", reachable);
        result.put("time", endTime - startTime);
        
        return result;
    }
    
    /**
     * Get the IP address for a hostname
     * @param args The hostname as the first argument
     * @return The IP address as string
     */
    public static String resolveHostname(Object... args) throws IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Hostname is required");
        }
        
        String hostname = args[0].toString();
        return java.net.InetAddress.getByName(hostname).getHostAddress();
    }
}
