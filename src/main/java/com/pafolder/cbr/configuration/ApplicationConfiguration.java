package com.pafolder.cbr.configuration;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@EnableCaching
public class ApplicationConfiguration {
    public static final String TEST_SUMMARY =
            "<b>CBR Test application</b><br>Fulfilling the task...<br><br>" +
                    "<b>Credentials for testing:</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                    "Admin:&nbsp;<i>admin@mail.com&nbsp;/&nbsp;admin</i><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                    "&nbsp;&nbsp;&nbsp;&nbsp;User:&nbsp;<i>user@mail.com&nbsp;/&nbsp;password</i>" +
                    "<br><br><div><a href=\"https://github.com/pafolder/rva\">Application source files (GitHub)</a></div>";

    @Bean
    public OpenAPI customOpenAPI(@Value("${cbr.version}") String appVersion) {
        Contact contact = new Contact();
        contact.name("Sergei Pastukhov");
        contact.email("pafolder@gmail.com");
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("basicScheme",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
                .info(new Info().title("REST API Documentation for CBR Test Application")
                        .version(appVersion)
                        .description(TEST_SUMMARY)
                        .contact(contact));
    }
}
