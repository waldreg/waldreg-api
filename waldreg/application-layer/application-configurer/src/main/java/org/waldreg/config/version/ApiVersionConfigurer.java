package org.waldreg.config.version;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.waldreg.config.context.DecryptedContextResolveInterceptor;

@Configuration
public class ApiVersionConfigurer implements WebMvcConfigurer{

    private final ApiVersionSetInterceptor apiVersionSetInterceptor;

    @Autowired
    public ApiVersionConfigurer(ApiVersionSetInterceptor apiVersionSetInterceptor){
        this.apiVersionSetInterceptor = apiVersionSetInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(apiVersionSetInterceptor)
                .addPathPatterns("**")
                .addPathPatterns("*")
                .addPathPatterns("/**")
                .addPathPatterns("/*");
    }


}