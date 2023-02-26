package org.waldreg.repository.user.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.waldreg.domain.rewardtag.RewardTagWrapper;
import org.waldreg.domain.user.User;
import org.waldreg.user.dto.UserDto;

@Service
public class UserRepositoryMapper{

    public List<UserDto> userListToUserDtoList(List<User> userList){
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userList){
            userDtoList.add(userToUserDto(user));
        }
        return userDtoList;
    }

    public User userDtoToUser(UserDto userDto){
        return User.builder()
                .name(userDto.getName())
                .userId(userDto.getUserId())
                .userPassword(userDto.getUserPassword())
                .phoneNumber(userDto.getPhoneNumber())
                .build();
    }

    public UserDto userToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .userId(user.getUserId())
                .userPassword(user.getUserPassword())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .rewardPoint(addUpRewardTagWrapperListPoint(user.getRewardTagWrapperList()))
                .character(user.getCharacter().getCharacterName())
                .build();
    }

    private int addUpRewardTagWrapperListPoint(List<RewardTagWrapper> rewardTagWrapperList){
        int sum = 0;
        for (RewardTagWrapper rewardTagWrapper : rewardTagWrapperList){
            sum += rewardTagWrapper.getRewardTag().getRewardPoint();
        }
        return sum;
    }

}
