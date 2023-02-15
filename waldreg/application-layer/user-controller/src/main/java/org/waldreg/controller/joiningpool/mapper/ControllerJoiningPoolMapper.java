package org.waldreg.controller.joiningpool.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.waldreg.controller.joiningpool.request.UserRequest;
import org.waldreg.controller.joiningpool.response.UserJoiningPoolListResponse;
import org.waldreg.controller.joiningpool.response.UserJoiningPoolResponse;
import org.waldreg.user.dto.UserDto;

@Service
public class ControllerJoiningPoolMapper{
    public UserDto userRequestToUserDto(UserRequest createRequest){
        return UserDto.builder()
                .userId(createRequest.getUserId())
                .name(createRequest.getName())
                .userPassword(createRequest.getUserPassword())
                .phoneNumber(createRequest.getPhoneNumber())
                .build();
    }

    public List<UserJoiningPoolResponse> userDtoListToUserJoiningPoolListResponse(List<UserDto> userDtoList){
        List<UserJoiningPoolResponse> userJoiningPoolResponseList = new ArrayList<>();
        for(UserDto userDto : userDtoList){
            userJoiningPoolResponseList.add(UserJoiningPoolResponse.builder()
                                                    .id(userDto.getId())
                                                    .name(userDto.getName())
                                                    .userId(userDto.getUserId())
                                                    .phoneNumber(userDto.getPhoneNumber())
                                                    .build());
        }
        return userJoiningPoolResponseList;
    }

    public UserJoiningPoolListResponse createUserJoiningPoolListResponse(int maxIdx, List<UserJoiningPoolResponse> userJoiningPoolResponseList){
        return UserJoiningPoolListResponse.builder()
                .maxId(maxIdx)
                .userList(userJoiningPoolResponseList)
                .build();
    }

}
