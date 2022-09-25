package gtcloud.plugin.repository.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gtcloud.plugin.repository.interceptor.CrosInterceptor;
import gtcloud.plugin.repository.interceptor.JwtInterceptor;
import gtcloud.plugin.repository.interceptor.OptionsInterceptor;
import gtcloud.plugin.repository.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final Environment environment;

    @Autowired
    public WebMvcConfig(JwtService jwtService, ObjectMapper objectMapper, Environment environment) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
        this.environment = environment;
    }

    @Bean
    public CrosInterceptor crosInterceptor() {
        return new CrosInterceptor();
    }

    @Bean
    public OptionsInterceptor optionsInterceptor() {
        return new OptionsInterceptor();
    }

    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor(jwtService, objectMapper);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(crosInterceptor())
                .addPathPatterns("/**");

        registry.addInterceptor(optionsInterceptor())
                .addPathPatterns("/**");

        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/user/**");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(false);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = environment.getProperty("static.resources.location");
        if (location != null) {
            registry.addResourceHandler("/**").addResourceLocations("file:" + location);
        }
    }
}
