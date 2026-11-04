package altn72.project.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI webRainOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Webrain API")
                        .version("v1")
                        .description("API REST de consultation et de mise à jour des incidents."));
    }
}
