package org.waldreg.teambuilding.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.dto.TeamBuildingRequestDto;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.dto.UserRequestDto;
import org.waldreg.teambuilding.management.teamcreator.TeamCreator;
import org.waldreg.teambuilding.management.teamcreator.TeamCreator.Team;
import org.waldreg.teambuilding.spi.TeamBuildingRepository;
import org.waldreg.teambuilding.spi.TeamBuildingUserRepository;
import org.waldreg.teambuilding.spi.TeamRepository;

@Service
public class DefaultTeamBuildingManager implements TeamBuildingManager{

    private final TeamBuildingRepository teamBuildingRepository;
    private final TeamRepository teamRepository;
    private final TeamBuildingUserRepository teamBuildingUserRepository;
    private final TeamCreator teamCreator;

    @Autowired
    public DefaultTeamBuildingManager(TeamBuildingRepository teamBuildingRepository, TeamRepository teamRepository, TeamBuildingUserRepository teamBuildingUserRepository, TeamCreator teamCreator){
        this.teamBuildingRepository = teamBuildingRepository;
        this.teamRepository = teamRepository;
        this.teamBuildingUserRepository = teamBuildingUserRepository;
        this.teamCreator = teamCreator;
    }

    @Override
    public TeamBuildingDto readTeamBuildingById(int teamBuildingId){
        return teamBuildingRepository.readTeamBuildingById(teamBuildingId);
    }

    @Override
    public void createTeamBuilding(TeamBuildingRequestDto teamBuildingRequestDto){
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingRequestDto.getUserList(), teamBuildingRequestDto.getTeamCount());
        TeamBuildingDto teamBuildingDto = buildTeamBuildingDto(teamBuildingRequestDto.getTeamBuildingTitle(), teamDtoList);
        teamBuildingRepository.createTeamBuilding(teamBuildingDto);
    }

    @Override
    public List<TeamBuildingDto> readAllTeamBuilding(int startIdx, int endIdx){
        return teamBuildingRepository.readAllTeamBuilding(startIdx, endIdx);
    }

    private List<TeamDto> createTeamDtoList(List<UserRequestDto> userRequestDtoList, int teamCount){
        List<TeamDto> teamDtoList = new ArrayList<>();
        Collections.sort(userRequestDtoList);
        List<Team> teamMemberList = teamCreator.createRandomTeamList(userRequestDtoList, teamCount);
        int teamNumber = 1;
        for (Team team : teamMemberList){
            teamDtoList.add(buildTeamDto(team.getMemberList(), teamNumber));
            teamNumber++;
        }
        return teamDtoList;
    }

    private TeamDto buildTeamDto(List<String> memberList, int teamNumber){
        return TeamDto.builder()
                .teamName("Team " + teamNumber)
                .userDtoList(buildUserDtoList(memberList))
                .build();
    }

    private List<UserDto> buildUserDtoList(List<String> memberList){
        List<UserDto> userDtoList = new ArrayList<>();
        for (String member : memberList){
            userDtoList.add(teamBuildingUserRepository.getUserInfoByUserId(member));
        }
        return userDtoList;
    }

    private TeamBuildingDto buildTeamBuildingDto(String title, List<TeamDto> teamDtoList){
        return TeamBuildingDto.builder()
                .teamBuildingTitle(title)
                .teamDtoList(teamDtoList)
                .build();
    }

}
