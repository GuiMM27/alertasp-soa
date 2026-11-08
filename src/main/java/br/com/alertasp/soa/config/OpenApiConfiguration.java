package br.com.alertasp.soa.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ALERTASP API",
                version = "1.0.0",
                description = "API acadêmica para cadastro de pontos e acompanhamento de ocorrências de alagamento em São Paulo.",
                contact = @Contact(name = "Equipe ALERTASP - FIAP Global Solution 2026.2")
        )
)
public class OpenApiConfiguration {
}
