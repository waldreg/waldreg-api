package org.waldreg.token.jwt.secret;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class Secret{

    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public SecretKey getSecretKey(){
        return secretKey;
    }

}
