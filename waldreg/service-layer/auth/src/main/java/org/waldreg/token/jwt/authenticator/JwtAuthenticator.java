package org.waldreg.token.jwt.authenticator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.token.authenticator.TokenAuthenticator;
import org.waldreg.token.exception.TokenExpiredException;
import org.waldreg.token.jwt.secret.Secret;
import org.waldreg.util.token.DecryptedTokenContextHolder;

@Service
public class JwtAuthenticator implements TokenAuthenticator{

    private final Secret secret;
    private final DecryptedTokenContextHolder decryptedTokenContextHolder;
    @Autowired
    public JwtAuthenticator(Secret secret, DecryptedTokenContextHolder decryptedTokenContextHolder){
        this.secret = secret;
        this.decryptedTokenContextHolder = decryptedTokenContextHolder;
    }

    @Override
    public int authenticate(String token) throws JwtException{
        try{
            Jws<Claims> claim = Jwts.parserBuilder().setSigningKey(secret.getSecretKey()).build().parseClaimsJws(token);
            int id = Integer.parseInt(claim.getBody().getSubject());
            decryptedTokenContextHolder.hold(id);
            return id;
        } catch (ExpiredJwtException EJE){
            throw new TokenExpiredException(EJE.getMessage(), EJE.getCause());
        }
    }

}
