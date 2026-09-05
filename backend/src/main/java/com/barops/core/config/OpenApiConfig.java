package com.barops.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Khai báo 1 "security scheme" tên "bearerAuth" — nhờ đó Swagger UI hiện nút Authorize
 * ở góc phải trên. Bấm vào, dán access token (KHÔNG cần gõ chữ "Bearer " phía trước,
 * Swagger tự thêm) — từ đó mọi nút "Try it out" trong trang đều tự động gắn kèm token này,
 * không phải copy/paste header thủ công như dùng Postman.
 */
@Configuration
public class OpenApiConfig {

    private static final String SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI barOpsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("BarOps API")
                        .description("POS + Inventory Core (Module 1)")
                        .version("v1"))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SCHEME_NAME, new SecurityScheme()
                                .name(SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
