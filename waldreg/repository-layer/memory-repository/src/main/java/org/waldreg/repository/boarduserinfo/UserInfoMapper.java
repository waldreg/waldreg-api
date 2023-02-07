package org.waldreg.repository.boarduserinfo;

import org.waldreg.board.dto.UserDto;
import org.waldreg.domain.user.User;
import org.waldreg.repository.board.UserInBoardMapper;

public class UserInfoMapper implements UserInBoardMapper{

    @Override
    public UserDto userDomainToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();
    }

}
