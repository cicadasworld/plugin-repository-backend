package gtcloud.plugin.repository.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisConnectionException;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
public class RedisConfig {

    private final Environment environment;

    @Autowired
    public RedisConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public StatefulRedisConnection<String, String> statefulRedisConnection() {
        String redisUrl = environment.getProperty("redis.url");
        if (redisUrl != null) {
            RedisClient redisClient = RedisClient.create(redisUrl);
            return redisClient.connect();
        }
        throw new RedisConnectionException("No connection is available to " + redisUrl);
    }
}
