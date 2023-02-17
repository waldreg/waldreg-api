package org.waldreg.team.management;

import java.util.ArrayList;
import java.util.List;
import org.waldreg.team.dto.TeamDto;
import org.waldreg.team.dto.TeamRequestDto;
import org.waldreg.team.spi.TeamRepository;
import org.waldreg.team.spi.TeamUserRepository;
import org.waldreg.teambuilding.dto.UserDto;

public class DefaultTeamManager implements TeamManager{

    private final TeamRepository teamRepository;

    private final TeamUserRepository teamUserRepository;

    public DefaultTeamManager(TeamRepository teamRepository, TeamUserRepository teamUserRepository){
        this.teamRepository = teamRepository;
        this.teamUserRepository = teamUserRepository;
    }

    @Override
    public void createTeam(int teamBuildingId, TeamRequestDto teamRequestDto){
        TeamDto teamDto = buildTeamDto(teamBuildingId, teamRequestDto);
        teamRepository.createTeam(teamDto);
    }

    private TeamDto buildTeamDto(int teamBuildingId, TeamRequestDto teamRequestDto){
        return TeamDto.builder()
                .teamBuildingId(teamBuildingId)
                .teamName(teamRequestDto.getTeamName())
                .userDtoList(buildUserDtoList(teamRequestDto.getMemberList()))
                .build();
    }

    private List<UserDto> buildUserDtoList(List<String> memberList){
        List<UserDto> userDtoList = new ArrayList<>();
        for (String member : memberList){
            userDtoList.add(teamUserRepository.getUserInfoByUserId(member));
        }
        return userDtoList;
    }

}
