package gtcloud.plugin.repository.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PluginRepositoryGatewayApp {

    public static void main(String[] args) {
        SpringApplication.run(PluginRepositoryGatewayApp.class, args);
    }
}
