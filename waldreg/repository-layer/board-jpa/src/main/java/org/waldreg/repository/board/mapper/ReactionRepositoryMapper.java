package org.waldreg.repository.board.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.waldreg.board.dto.UserDto;
import org.waldreg.domain.board.reaction.ReactionUser;
import org.waldreg.domain.user.User;

@Component
public class ReactionRepositoryMapper{


    public List<UserDto> reactionUserListToUserDtoList(List<ReactionUser> reactionUserList){
        List<UserDto> userDtoList = new ArrayList<>();
        for (ReactionUser reactionUser : reactionUserList){
            userDtoList.add(userToUserDto(reactionUser.getUser()));
        }
        return userDtoList;
    }

    public UserDto userToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();
    }

}
