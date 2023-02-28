package org.waldreg.token.jwt.authenticator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.token.authenticator.TokenAuthenticator;
import org.waldreg.token.dto.TokenUserDto;
import org.waldreg.token.exception.PasswordMissMatchException;
import org.waldreg.token.exception.TokenExpiredException;
import org.waldreg.token.exception.UnknownUserIdException;
import org.waldreg.token.jwt.secret.Secret;
import org.waldreg.token.spi.AuthRepository;
import org.waldreg.util.token.DecryptedTokenContextHolder;

@Service
public class JwtAuthenticator implements TokenAuthenticator{

    private final Secret secret;
    private final DecryptedTokenContextHolder decryptedTokenContextHolder;
    private final AuthRepository authRepository;

    @Autowired
    public JwtAuthenticator(Secret secret, DecryptedTokenContextHolder decryptedTokenContextHolder, AuthRepository authRepository){
        this.secret = secret;
        this.decryptedTokenContextHolder = decryptedTokenContextHolder;
        this.authRepository = authRepository;
    }

    @Override
    public TokenUserDto getTokenUserDtoByUserIdAndPassword(String userId, String userPassword){
        throwIfUnknownUserIdException(userId);
        TokenUserDto tokenUserDto = authRepository.findUserByUserId(userId);
        throwIfPasswordMissMatch(tokenUserDto.getUserPassword(), userPassword, userId);
        return tokenUserDto;
    }

    private void throwIfUnknownUserIdException(String userId){
        if (!authRepository.isExistUserId(userId)){
            throw new UnknownUserIdException(userId);
        }
    }
    private void throwIfPasswordMissMatch(String storedPassword, String userPassword, String userId){
        if(storedPassword!= userPassword){
            throw new PasswordMissMatchException(userId, userPassword);
        }
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
