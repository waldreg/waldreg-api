package org.waldreg.controller.user.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.waldreg.controller.user.request.UpdateUserRequest;
import org.waldreg.controller.user.response.UserListResponse;
import org.waldreg.user.dto.UserDto;
import org.waldreg.controller.joiningpool.request.UserRequest;
import org.waldreg.controller.user.response.UserResponse;

@Service
public class ControllerUserMapper{

    public UserDto updateUserRequestToUserDto(UpdateUserRequest updateUserRequest){
        return UserDto.builder()
                .name(updateUserRequest.getName())
                .userPassword(updateUserRequest.getUserPassword())
                .phoneNumber(updateUserRequest.getPhoneNumber())
                .build();
    }

    public UserResponse userDtoToUserResponseWithPermission(UserDto userDto){
        return UserResponse.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .userId(userDto.getUserId())
                .phoneNumber(userDto.getPhoneNumber())
                .character(userDto.getCharacter())
                .createdAt(userDto.getCreatedAt())
                .rewardPoint(userDto.getRewardPoint())
                .build();
    }

    public UserResponse userDtoToUserResponseWithoutPermission(UserDto userDto){
        return UserResponse.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .userId(userDto.getUserId())
                .character(userDto.getCharacter())
                .createdAt(userDto.getCreatedAt())
                .build();
    }

    public List<UserResponse> userDtoListToUserListResponseWithPermission(List<UserDto> userDtoList){
        List<UserResponse> userResponseList = new ArrayList<>();
        for (UserDto userDto : userDtoList){
            userResponseList.add(UserResponse.builder()
                    .id(userDto.getId())
                    .name(userDto.getName())
                    .userId(userDto.getUserId())
                    .phoneNumber(userDto.getPhoneNumber())
                    .character(userDto.getCharacter())
                    .createdAt(userDto.getCreatedAt())
                    .rewardPoint(userDto.getRewardPoint())
                    .build());
        }
        return userResponseList;
    }

    public List<UserResponse> userDtoListToUserListResponseWithoutPermission(List<UserDto> userDtoList){
        List<UserResponse> userResponseList = new ArrayList<>();
        for (UserDto userDto : userDtoList){
            userResponseList.add(UserResponse.builder()
                    .id(userDto.getId())
                    .name(userDto.getName())
                    .userId(userDto.getUserId())
                    .character(userDto.getCharacter())
                    .createdAt(userDto.getCreatedAt())
                    .build());
        }
        return userResponseList;
    }

    public UserListResponse createUserListResponse(int maxIdx, List<UserResponse> userResponseList){
        return UserListResponse.builder()
                .maxId(maxIdx)
                .userList(userResponseList)
                .build();
    }

}
