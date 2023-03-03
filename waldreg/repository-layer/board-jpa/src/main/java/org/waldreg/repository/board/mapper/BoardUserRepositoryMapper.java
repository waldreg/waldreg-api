package org.waldreg.repository.board.mapper;

import org.springframework.stereotype.Component;
import org.waldreg.board.dto.UserDto;
import org.waldreg.domain.user.User;

@Component
public class BoardUserRepositoryMapper{

    public UserDto userToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();
    }
}
