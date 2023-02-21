package org.waldreg.repository.teambuildinguserinfo;

import org.springframework.stereotype.Service;
import org.waldreg.domain.user.User;
import org.waldreg.repository.team.UserInTeamMapper;
import org.waldreg.teambuilding.dto.UserDto;

@Service
public class TeamBuildingUserInfoMapper implements UserInTeamMapper{

    @Override
    public UserDto userDomainToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();
    }

}
