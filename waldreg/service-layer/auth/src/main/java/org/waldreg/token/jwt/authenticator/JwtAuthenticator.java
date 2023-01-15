package org.waldreg.token.jwt.authenticator;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.token.authenticator.TokenAuthenticator;
import org.waldreg.token.exception.TokenExpiredException;
import org.waldreg.token.jwt.secret.Secret;

@Service
public class JwtAuthenticator implements TokenAuthenticator{

    private final Secret secret;

    @Autowired
    public JwtAuthenticator(Secret secret){
        this.secret = secret;
    }

    @Override
    public boolean authenticate(String token) throws JwtException{
        try{
            Jwts.parserBuilder().setSigningKey(secret.getSecretKey()).build().parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException EJE){
            throw new TokenExpiredException(EJE.getMessage(), EJE.getCause());
        }
    }

}
