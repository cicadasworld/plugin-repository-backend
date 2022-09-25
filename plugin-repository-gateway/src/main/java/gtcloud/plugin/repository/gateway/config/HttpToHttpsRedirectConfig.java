package gtcloud.plugin.repository.gateway.config;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import reactor.core.publisher.Mono;

@Configuration
@ConditionalOnProperty(prefix = "server.http", name = "port")
public class HttpToHttpsRedirectConfig {

    @Value("${server.http.port}")
    private int httpPort;

    @Value("${server.port}")
    private int serverPort;

    @PostConstruct
    public void startRedirectServer() {
        final NettyReactiveWebServerFactory serverFactory = new NettyReactiveWebServerFactory(httpPort);
        final WebServer webServer = serverFactory.getWebServer((request, response) -> {
            final URI uri = request.getURI();
            URI httpsUri;
            try {
                httpsUri = new URI("https", uri.getUserInfo(), uri.getHost(), serverPort, uri.getPath(), uri.getQuery(),
                    uri.getFragment());
            }
            catch (final URISyntaxException e) {
                return Mono.error(e);
            }
            response.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            response.getHeaders()
                .setLocation(httpsUri);
            return response.setComplete();
        });
        webServer.start();
    }
}
