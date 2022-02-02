import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Security {

    static private final String HASHING_ALGORITHM = "SHA-256";
    static private final Charset TEXT_ENCODING = StandardCharsets.UTF_8;

    public static String generateHash(String dataToHash, String salt) throws NoSuchAlgorithmException {
        MessageDigest msgDigest = MessageDigest.getInstance(HASHING_ALGORITHM);
        String fullData = dataToHash + salt;
        byte[] dataBytes = fullData.getBytes(TEXT_ENCODING);
        byte[] hash = msgDigest.digest(dataBytes);
        String hashString = bytesToHex(hash);
        return hashString;
    }

    public static String generateSalt(int numBytes) {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[numBytes];
        random.nextBytes(saltBytes);
        String saltStr = bytesToHex(saltBytes);
        return saltStr;
    }

    public static String bytesToHex(byte[] byteData) {
        String hexValue = "";
        for (int i=0; i<byteData.length; i++) {
            byte currentByte = byteData[i];
            String currentByteString = String.format("%02X", currentByte);
            hexValue += currentByteString;
        }
        return hexValue;
    }
}
