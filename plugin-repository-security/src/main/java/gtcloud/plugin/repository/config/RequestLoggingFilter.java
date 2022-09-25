package gtcloud.plugin.repository.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingFilter {

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter logFilter = new CommonsRequestLoggingFilter();
        logFilter.setIncludeQueryString(true);
        logFilter.setIncludeClientInfo(true);
        logFilter.setIncludeHeaders(true);
        logFilter.setIncludePayload(true);
        logFilter.setMaxPayloadLength(10000);
        return logFilter;
    }
}
