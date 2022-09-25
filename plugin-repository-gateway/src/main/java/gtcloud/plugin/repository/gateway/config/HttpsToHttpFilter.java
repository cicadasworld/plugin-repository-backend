package gtcloud.plugin.repository.gateway.config;

import java.net.URI;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
@ConditionalOnProperty(prefix = "server.http", name = "port")
public class HttpsToHttpFilter implements GlobalFilter, Ordered {

    private static final int HTTPS_TO_HTTP_FILTER_ORDER = 10099;

    @Override
    public int getOrder() {
        return HTTPS_TO_HTTP_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final URI originalUri = exchange.getRequest()
            .getURI();
        final ServerHttpRequest request = exchange.getRequest();
        final ServerHttpRequest.Builder mutate = request.mutate();
        final String forwardedUri = request.getURI()
            .toString();
        if (forwardedUri != null && forwardedUri.startsWith("https")) {
            try {
                final URI mutateUri = new URI("http", originalUri.getUserInfo(), originalUri.getHost(),
                    originalUri.getPort(), originalUri.getPath(), originalUri.getQuery(), originalUri.getFragment());
                mutate.uri(mutateUri);
            }
            catch (final Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        final ServerHttpRequest build = mutate.build();
        return chain.filter(exchange.mutate()
            .request(build)
            .build());
    }

}
