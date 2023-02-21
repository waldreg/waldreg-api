package org.waldreg.controller.teambuilding.mapper;

import java.util.ArrayList;
import java.util.List;
import org.waldreg.controller.teambuilding.request.TeamBuildingRequest;
import org.waldreg.controller.teambuilding.request.TeamRequest;
import org.waldreg.controller.teambuilding.request.UserWeightRequest;
import org.waldreg.teambuilding.team.dto.TeamRequestDto;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingRequestDto;
import org.waldreg.teambuilding.teambuilding.dto.UserRequestDto;

public class ControllerTeamBuildingMapper{

    public TeamBuildingRequestDto teamBuildingRequestToTeamBuildingDto(TeamBuildingRequest teamBuildingRequest){
        return TeamBuildingRequestDto.builder()
                .teamBuildingTitle(teamBuildingRequest.getTeamBuildingTitle())
                .teamCount(teamBuildingRequest.getTeamCount())
                .userRequestDtoList(userRequestListToUserRequestDtoList(teamBuildingRequest.getUserList()))
                .build();
    }

    private List<UserRequestDto> userRequestListToUserRequestDtoList(List<UserWeightRequest> userList){
        List<UserRequestDto> userRequestDtoList = new ArrayList<>();
        userList.stream().forEach(i -> userRequestDtoList.add(userRequestToUserRequestDto(i)));
        return userRequestDtoList;
    }

    private UserRequestDto userRequestToUserRequestDto(UserWeightRequest userWeightRequest){
        return UserRequestDto.builder()
                .userId(userWeightRequest.getUserId())
                .weight(userWeightRequest.getWeight())
                .build();
    }


    public TeamRequestDto teamRequestToTeamRequestDto(TeamRequest teamRequest){
        return TeamRequestDto.builder()
                .teamName(teamRequest.getTeamName())
                .memberList(teamRequest.getMembers())
                .build();
    }

}
