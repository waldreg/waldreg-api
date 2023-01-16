package org.waldreg.repository.user;

import java.util.ArrayList;
import java.util.List;
import org.waldreg.domain.user.User;
import org.waldreg.user.dto.UserDto;

public class UserMapper{

    public User userDtoToUserDomain(UserDto userDto){
        return User.builder()
                .name(userDto.getName())
                .userId(userDto.getUserId())
                .userPassword(userDto.getUserPassword())
                .phoneNumber(userDto.getPhoneNumber())
                .build();
    }

    public UserDto userDomainToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .userId(user.getUserId())
                .userPassword(user.getUserPassword())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .advantage(user.getAdvantage())
                .penalty(user.getPenalty())
                .character(user.getCharacter().getCharacterName())
                .socialLogin(user.getSocialLogin())
                .build();
    }

    public List<UserDto> userDomainListToUserDtoList(List<User> userList){
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userList){
            userDtoList.add(userDomainToUserDto(user));
        }
        return userDtoList;
    }

}
