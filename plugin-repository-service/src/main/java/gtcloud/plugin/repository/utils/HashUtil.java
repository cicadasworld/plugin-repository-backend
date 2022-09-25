package gtcloud.plugin.repository.utils;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class HashUtil {
    
    public static String sha256(String original, String salt) throws Exception {
        String originalWithSalt = StringUtils.join(salt, original);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(originalWithSalt.getBytes(StandardCharsets.UTF_8));
        return new String(Hex.encode(hash));
    }

    public static boolean isSha256Match(String original, String salt, String hashValue) throws Exception {
        String reHashValue = sha256(original, salt);
        return StringUtils.equals(hashValue, reHashValue);
    }
}
