package org.waldreg.repository.teambuilding.teamuser.mapper;

import org.springframework.stereotype.Service;
import org.waldreg.domain.user.User;
import org.waldreg.teambuilding.dto.UserDto;

@Service
public class TeamUserRepositoryMapper{

    public UserDto userToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();
    }

}
