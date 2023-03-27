package org.waldreg.config.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.waldreg.util.token.DecryptedTokenContextResolver;

@Component
public class DecryptedContextResolveInterceptor implements HandlerInterceptor{

    private final DecryptedTokenContextResolver decryptedTokenContextResolver;

    @Autowired
    public DecryptedContextResolveInterceptor(DecryptedTokenContextResolver decryptedTokenContextResolver){
        this.decryptedTokenContextResolver = decryptedTokenContextResolver;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        decryptedTokenContextResolver.resolve();
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        decryptedTokenContextResolver.resolve();
    }

}
