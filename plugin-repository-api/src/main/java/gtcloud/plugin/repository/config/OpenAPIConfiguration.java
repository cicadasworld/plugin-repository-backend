package gtcloud.plugin.repository.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "插件仓库 REST API 参考手册",
        version = "1.0.0",
        contact = @Contact(
            name = "GTCloud", email = "gt@gtgis.cn", url = "http://www.gtgis.cn/"
        ),
        description = "插件仓库 REST API"
    ),
    servers = @Server(
        url = "${repository.url}",
        description = "生产环境"
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class OpenAPIConfiguration {
}
