package mariwrnk.tasktracker.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

    @Value("${tasktracker.openapi.dev-url}")
    private String devUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Development Environment");

        Contact contact = new Contact();
        contact.setEmail("MariWrnk@gmail.com");
        contact.setName("Maria Voronko");
        contact.setUrl("https://https://github.com/MariWrnk");

        Info info = new Info()
                .title("Task Tracker service API")
                .version("1.0")
                .contact(contact);

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}