package dev.salt.Ring20.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("local")
public class LocalStorageWebConfig implements WebMvcConfigurer {

    //TODO: constants need to be separate, this will make it easies to edit and understand later.
    // in this case: file:./ringsatranarvi-files/ -> this could be another name and could be then easily changed

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/local-storage/**")
                .addResourceLocations("file:./ringsatranarvi_files/");
    }
}
