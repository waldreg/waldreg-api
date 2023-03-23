package org.waldreg.token.jwt.publisher;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.token.jwt.secret.Secret;
import org.waldreg.token.publisher.TokenPublisher;
import org.waldreg.token.dto.TokenDto;

@Service
public class JwtTokenPublisher implements TokenPublisher{

    public final Secret secret;

    @Autowired
    public JwtTokenPublisher(Secret secret){
        this.secret = secret;
    }

    @Override
    public String publish(TokenDto tokenDto){
        String id = "" + tokenDto.getId();
        Date createdAt = tokenDto.getCreatedAt();
        Date expiredAt = tokenDto.getExpiredAt();

        return Jwts.builder().setSubject(id).setIssuedAt(createdAt).setExpiration(expiredAt).signWith(secret.getSecretKey(), SignatureAlgorithm.HS256).compact();

    }

}
