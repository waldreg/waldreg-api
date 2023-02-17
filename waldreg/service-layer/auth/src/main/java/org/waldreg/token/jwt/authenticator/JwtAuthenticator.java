package org.waldreg.token.jwt.authenticator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
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
        int id = getIdByToken(token);
        decryptedTokenContextHolder.hold(id);
        return id;
    }

    private int getIdByToken(String token){
        try{
            Jws<Claims> claim = Jwts.parserBuilder().setSigningKey(secret.getSecretKey()).build().parseClaimsJws(token.split(" ")[1]);
            return Integer.parseInt(claim.getBody().getSubject());
        } catch (ExpiredJwtException EJE){
            throw new TokenExpiredException(EJE.getMessage(), EJE.getCause());
        }
    }

}
