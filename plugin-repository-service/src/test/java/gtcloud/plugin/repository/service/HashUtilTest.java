package gtcloud.plugin.repository.service;

import gtcloud.plugin.repository.utils.HashUtil;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class HashUtilTest {

    @Test
    public void sha256() throws Exception {
        String salt = UUID.randomUUID().toString().replace("-", "");
        System.out.println(salt);
        String hashValue = HashUtil.sha256("admin12345678", salt);
        System.out.println(hashValue);
    }

}
