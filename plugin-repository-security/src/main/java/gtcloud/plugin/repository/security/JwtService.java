package gtcloud.plugin.repository.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${auth.token.expiresIn:15}")
    private long expiresIn;

    private final StatefulRedisConnection<String, String> redisConnection;

    @Autowired
    public JwtService(StatefulRedisConnection<String, String> redisConnection) {
        this.redisConnection = redisConnection;
    }

    private static final byte[] HMAC_SECRET_KEY = new byte[] {
            (byte) 0x74,
            (byte) 0x68,
            (byte) 0x65,
            (byte) 0x48,
            (byte) 0x6d,
            (byte) 0x61,
            (byte) 0x63,
            (byte) 0x53,
            (byte) 0x65,
            (byte) 0x63,
            (byte) 0x72,
            (byte) 0x65,
            (byte) 0x74,
            (byte) 0x4b,
            (byte) 0x65,
            (byte) 0x79,
            (byte) 0x31,
            (byte) 0x32,
            (byte) 0x33,
    };

    public String createToken(JwtSubject data) {
        Algorithm algorithm = Algorithm.HMAC256(HMAC_SECRET_KEY);
        LocalDateTime expiresAt = LocalDateTime.now(ZoneOffset.UTC).plusMinutes(expiresIn);
        Date expiresAtDate = Date.from(expiresAt.toInstant(ZoneOffset.UTC));
        String jwtId = UUID.randomUUID().toString().replace("-", "");
        redisConnection.sync().set(jwtId, ""); // 键对应的值为""
        redisConnection.sync().expireat(jwtId, expiresAtDate);
        return JWT.create()
            .withSubject(data.getUsername())
            .withExpiresAt(expiresAtDate)
            .withJWTId(jwtId)
            .sign(algorithm);
    }

    public JwtSubject verifyToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(HMAC_SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm).acceptExpiresAt(60).build();
        DecodedJWT jwt = verifier.verify(token);
        String jwtId = redisConnection.sync().get(jwt.getId()); // 键对应的值为""
        if ("".equals(jwtId)) {
            return new JwtSubject(jwt.getSubject());
        }
        return null;
    }

    public void deleteToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(HMAC_SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm).acceptExpiresAt(60).build();
        DecodedJWT jwt = verifier.verify(token);
        redisConnection.sync().del(jwt.getId());
    }
}
