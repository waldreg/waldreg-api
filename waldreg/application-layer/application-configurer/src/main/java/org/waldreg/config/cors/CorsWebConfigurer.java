package org.waldreg.config.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsWebConfigurer implements WebMvcConfigurer{

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
		corsRegistry.addMapping("/**")
				.allowedOrigins("*")
				.allowedMethods("GET", "POST", "HEAD", "PUT", "DELETE")
				.allowCredentials(false)
				.maxAge(3600);
    }

}
