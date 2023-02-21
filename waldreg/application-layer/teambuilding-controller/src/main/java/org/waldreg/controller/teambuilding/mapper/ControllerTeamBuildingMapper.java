package org.waldreg.controller.teambuilding.mapper;

import java.util.ArrayList;
import java.util.List;
import org.waldreg.controller.teambuilding.request.TeamBuildingRequest;
import org.waldreg.controller.teambuilding.request.TeamRequest;
import org.waldreg.controller.teambuilding.request.UserWeightRequest;
import org.waldreg.controller.teambuilding.response.TeamBuildingListResponse;
import org.waldreg.controller.teambuilding.response.TeamBuildingResponse;
import org.waldreg.controller.teambuilding.response.TeamResponse;
import org.waldreg.controller.teambuilding.response.UserResponse;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.team.dto.TeamRequestDto;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingDto;
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

    public List<TeamBuildingResponse> teamBuildingDtoListToTeamBuildingResponseList(List<TeamBuildingDto> teamBuildingDtoList){
        List<TeamBuildingResponse> teamBuildingResponseList = new ArrayList<>();
        teamBuildingDtoList.stream().forEach(i -> teamBuildingResponseList.add(teamBuildingDtoToTeamBuildingResponse(i)));
        return teamBuildingResponseList;
    }

    public TeamBuildingResponse teamBuildingDtoToTeamBuildingResponse(TeamBuildingDto teamBuildingDto){
        return TeamBuildingResponse.builder()
                .teamBuildingId(teamBuildingDto.getTeamBuildingId())
                .teamBuildingTitle(teamBuildingDto.getTeamBuildingTitle())
                .teamList(teamDtoListToTeamResponseList(teamBuildingDto.getTeamList()))
                .createdAt(teamBuildingDto.getCreatedAt())
                .lastModifiedAt(teamBuildingDto.getLastModifiedAt())
                .build();
    }

    private List<TeamResponse> teamDtoListToTeamResponseList(List<TeamDto> teamDtoList){
        List<TeamResponse> teamResponseList = new ArrayList<>();
        teamDtoList.stream().forEach(i -> teamResponseList.add(teamDtoToTeamResponse(i)));
        return teamResponseList;
    }

    private TeamResponse teamDtoToTeamResponse(TeamDto teamDto){
        return TeamResponse.builder()
                .teamId(teamDto.getTeamId())
                .teamName(teamDto.getTeamName())
                .lastModified(teamDto.getLastModifiedAt())
                .memberList(userDtoListToUserResponseList(teamDto.getUserList()))
                .build();
    }

    private List<UserResponse> userDtoListToUserResponseList(List<UserDto> userDtoList){
        List<UserResponse> userResponseList = new ArrayList<>();
        userDtoList.stream().forEach(i -> userResponseList.add(userDtoToUserResponse(i)));
        return userResponseList;
    }

    private UserResponse userDtoToUserResponse(UserDto userDto){
        return UserResponse.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .userId(userDto.getUserId())
                .build();
    }

    public TeamBuildingListResponse createTeamBuildingListResponse(int maxIdx, List<TeamBuildingResponse> teamBuildingResponseList){
        return TeamBuildingListResponse.builder()
                .maxIdx(maxIdx)
                .teamBuildingList(teamBuildingResponseList)
                .build();
    }

}
