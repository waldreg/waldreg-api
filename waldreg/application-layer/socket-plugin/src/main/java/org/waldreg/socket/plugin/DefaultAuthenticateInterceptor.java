package org.waldreg.socket.plugin;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;
import org.waldreg.core.socket.sessionstage.SessionStageHoldable;
import org.waldreg.token.aop.TokenUserFindable;
import org.waldreg.token.authenticator.TokenAuthenticator;
import org.waldreg.token.exception.AuthenticateFailException;

@Component
public class DefaultAuthenticateInterceptor implements HandshakeInterceptor{

    private final TokenAuthenticator tokenAuthenticator;
    private final TokenUserFindable tokenUserFindable;
    private final SessionStageHoldable sessionStageHoldable;

    @Autowired
    public DefaultAuthenticateInterceptor(TokenAuthenticator tokenAuthenticator,
                                            TokenUserFindable tokenUserFindable,
                                            SessionStageHoldable sessionStageHoldable){
        this.tokenAuthenticator = tokenAuthenticator;
        this.tokenUserFindable = tokenUserFindable;
        this.sessionStageHoldable = sessionStageHoldable;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes){
        try{
            auth(request);
        } catch (AuthenticateFailException authenticateFailException){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        return true;
    }

    private void auth(ServerHttpRequest request){
        try{
            int id = tokenAuthenticator.authenticate(getToken(request));
            tokenUserFindable.findUserById(id);
            sessionStageHoldable.holdId(getSessionId(request), id);
        } catch (Exception e){
            e.printStackTrace();
            throw new AuthenticateFailException();
        }
    }

    private String getToken(ServerHttpRequest request){
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(request.getURI());
        return uriComponentsBuilder.build().getQueryParams().get("token").get(0).replace("%20", " ");
    }

    private String getSessionId(ServerHttpRequest request){
        String path = request.getURI().getPath();
        String[] pathSnippets = path.split("/");
        return pathSnippets[pathSnippets.length-2];
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception){
    }

}