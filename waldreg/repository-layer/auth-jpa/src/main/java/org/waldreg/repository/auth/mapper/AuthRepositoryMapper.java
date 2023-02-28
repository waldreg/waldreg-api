package org.waldreg.repository.auth.mapper;

import org.springframework.stereotype.Component;
import org.waldreg.domain.user.User;
import org.waldreg.token.dto.TokenUserDto;

@Component
public class AuthRepositoryMapper{

    public TokenUserDto userToTokenUserDto(User user){
        return TokenUserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .userPassword(user.getUserPassword())
                .build();
    }


}
