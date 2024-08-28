package entities;
import java.io.BufferedReader;
import java.io.FileReader;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject; 

public class ExamTest {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java SimpleHashGenerator <PRN> <JSON file path>");
            return;
        }

        String prn = args[0].toLowerCase();
        String jsonFilePath = args[1];
        String destinationValue = "";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(jsonFilePath));
            StringBuilder jsonContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();
            
            JSONObject jsonObject = new JSONObject(jsonContent.toString());

            destinationValue = findDestinationValue(jsonObject);
            if (destinationValue.isEmpty()) {
                System.out.println("No destination key found in the JSON file.");
                return;
            }

            String randomString = generateRandomString(8);

            String toHash = prn + destinationValue + randomString;

            String md5Hash = generateMD5(toHash);

            
            System.out.println(md5Hash + ";" + randomString);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static String findDestinationValue(JSONObject jsonObject) throws JSONException {
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);
            if (key.equals("destination")) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String result = findDestinationValue((JSONObject) value);
                if (!result.isEmpty()) {
                    return result;
                }
            }
        }
        return "";
    }


    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static String generateMD5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : messageDigest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
