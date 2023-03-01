package org.waldreg.repository.joiningpool.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.waldreg.domain.user.UserJoiningPool;
import org.waldreg.user.dto.UserDto;

@Component
public class JoiningPoolMapper{

    public UserJoiningPool userDtoToUserJoiningPool(UserDto userDto){
        return UserJoiningPool.builder()
                .userId(userDto.getUserId())
                .userPassword(userDto.getUserPassword())
                .name(userDto.getName())
                .phoneNumber(userDto.getPhoneNumber())
                .build();
    }

    public List<UserDto> userJoiningPoolListToUserDtoList(List<UserJoiningPool> userJoiningPoolList){
        List<UserDto> userDtoList = new ArrayList<>();
        for (UserJoiningPool userJoiningPool :userJoiningPoolList){
            userDtoList.add(userJoiningPoolToUserDto(userJoiningPool));
        }
        return userDtoList;
    }

    public UserDto userJoiningPoolToUserDto(UserJoiningPool userJoiningPool){
        return UserDto.builder()
                .userId(userJoiningPool.getUserId())
                .userPassword(userJoiningPool.getUserPassword())
                .character("Guest")
                .name(userJoiningPool.getName())
                .phoneNumber(userJoiningPool.getPhoneNumber())
                .build();
    }
}
