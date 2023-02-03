package com.pafolder.librarian.configuration;

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
            "The <b>LIBRARIAN</b> Application allows Users to borrow books from the Library. The main rules are:" +
                    "<li> No more than 3 books can be borrowed by one User at a time." +
                    "</li><li> Each book cannot be borrowed for more than 14 days." +
                    "</li><li> If the User does not return the book on time, this counts as violation." +
                    " Users with 2 and more violations can no longer borrow books." +
                    "</li><li> Administrators can input books and manage Users as well as User's checkouts.<br><br>" +
                    "<b>Credentials for testing:</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                    "Admin:&nbsp;<i>admin@mail.com&nbsp;/&nbsp;admin</i><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                    "&nbsp;&nbsp;&nbsp;&nbsp;User:&nbsp;<i>user@mail.com&nbsp;/&nbsp;password</i></li>" +
                    "<br><div><a href=\"https://github.com/pafolder/cbr\">Application source files (GitHub)</a></div>";

    @Bean
    public OpenAPI customOpenAPI(@Value("${cbr.version}") String appVersion) {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("basicScheme",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
                .info(new Info().title("REST API Documentation for LIBRARIAN Application")
                        .version(appVersion)
                        .description(TEST_SUMMARY));
    }
}
