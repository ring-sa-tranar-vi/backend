package dev.salt.Ring20.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("local")
public class LocalStorageWebConfig implements WebMvcConfigurer {

    private final static String PATH_PATTERN = "/local-storage/**";
    private final static String LOCATION = "file:./ringsatranarvi_files/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(PATH_PATTERN)
                .addResourceLocations(LOCATION);
    }
}
