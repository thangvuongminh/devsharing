package com.studyhard.application.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"

)
@SecurityScheme(
    name = "security_auth",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(authorizationCode =
        @OAuthFlow(authorizationUrl = "https://accounts.google.com/o/oauth2/v2/auth",tokenUrl = "https://oauth2.googleapis.com/token"
        ,scopes = {
            @OAuthScope(name = "https://www.googleapis.com/auth/userinfo.email"),
            @OAuthScope(name = "https://www.googleapis.com/auth/userinfo.profile"),
        })
    )
)
@OpenAPIDefinition( security = {@SecurityRequirement(name="Bearer Authentication"),@SecurityRequirement(name = "security_auth")})
public class OpenApiConfig {
}
