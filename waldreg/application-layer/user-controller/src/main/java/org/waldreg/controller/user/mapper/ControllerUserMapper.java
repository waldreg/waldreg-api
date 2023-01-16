package org.waldreg.controller.user.mapper;

import java.util.ArrayList;
import java.util.List;
import org.waldreg.controller.user.response.UserListResponse;
import org.waldreg.user.dto.UserDto;
import org.waldreg.controller.user.request.UserRequest;
import org.waldreg.controller.user.response.UserResponse;

public class ControllerUserMapper{

    public UserDto userRequestToUserDto(UserRequest createRequest){
        return UserDto.builder()
                .userId(createRequest.getUserId())
                .name(createRequest.getName())
                .userPassword(createRequest.getUserPassword())
                .phoneNumber(createRequest.getPhoneNumber())
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
                .advantage(userDto.getAdvantage())
                .penalty(userDto.getPenalty())
                .socialLogin(userDto.getSocialLogin())
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
                    .advantage(userDto.getAdvantage())
                    .penalty(userDto.getPenalty())
                    .socialLogin(userDto.getSocialLogin())
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
