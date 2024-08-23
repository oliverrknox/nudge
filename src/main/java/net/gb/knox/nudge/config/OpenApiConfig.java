package net.gb.knox.nudge.config;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import net.gb.knox.nudge.domain.Error;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Configuration
public class OpenApiConfig {

    private final Schema<?> errorSchema = ModelConverters.getInstance().resolveAsResolvedSchema(new AnnotatedType(Error.class)).schema;

    private ApiResponse baseErrorResponse() {
        var errorContent = new Content().addMediaType(APPLICATION_JSON_VALUE, new MediaType().schema(errorSchema));
        return new ApiResponse().content(errorContent);
    }

    @Bean
    OpenAPI openApi() {
        var info = new Info().title("Nudge API").version("0.0.1-SNAPSHOT");

        var productionServer = new Server().url("https://api.knox.gb.net/nudge-gateway").description("Live server");
        var localServer = new Server().url("http://localhost:8080").description("Local development server");
        var servers = Arrays.asList(productionServer, localServer);

        var securityRequirements = new SecurityRequirement().addList("bearerAuth");
        var securityScheme = new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT");

        var badRequestResponse = baseErrorResponse().description("Bad request");
        var unauthenticatedResponse = baseErrorResponse().description("Unauthenticated");
        var forbiddenResponse = baseErrorResponse().description("Forbidden");
        var notFoundResponse = baseErrorResponse().description("Not found");

        var components = new Components().addSecuritySchemes("bearerAuth", securityScheme)
                .addResponses("badRequest", badRequestResponse)
                .addResponses("unauthenticated", unauthenticatedResponse)
                .addResponses("forbidden", forbiddenResponse)
                .addResponses("notFound", notFoundResponse);

        return new OpenAPI().info(info).addSecurityItem(securityRequirements).components(components).servers(servers);
    }
}
