package org.waldreg.config.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ContextConfigurer implements WebMvcConfigurer{

    private final DecryptedContextResolveInterceptor decryptedContextResolveInterceptor;

    @Autowired
    public ContextConfigurer(DecryptedContextResolveInterceptor decryptedContextResolveInterceptor){
        this.decryptedContextResolveInterceptor = decryptedContextResolveInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(decryptedContextResolveInterceptor)
                .addPathPatterns("**")
                .addPathPatterns("*")
                .addPathPatterns("/**")
                .addPathPatterns("/*");
    }


}
