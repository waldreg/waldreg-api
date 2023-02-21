package org.waldreg.repository.teambuilding;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.teambuilding.TeamBuilding;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingDto;

public class TeamBuildingMapper{

    private final TeamInTeamBuildingMapper teamInTeamBuildingMapper;

    @Autowired
    public TeamBuildingMapper(TeamInTeamBuildingMapper teamInTeamBuildingMapper){this.teamInTeamBuildingMapper = teamInTeamBuildingMapper;}

    public TeamBuilding teamBuildingTitleToTeamBuildingDomain(String teamBuildingTitle){
        return TeamBuilding.builder()
                .teamBuildingTitle(teamBuildingTitle)
                .build();
    }

    public TeamBuilding teamBuildingDtoToTeamBuildingDomain(TeamBuildingDto teamBuildingDto){
        return TeamBuilding.builder()
                .teamBuildingTitle(teamBuildingDto.getTeamBuildingTitle())
                .teamBuildingId(teamBuildingDto.getTeamBuildingId())
                .teamList(teamDtoListToTeamDomainList(teamBuildingDto.getTeamList()))
                .lastModifiedAt(teamBuildingDto.getLastModifiedAt())
                .createdAt(teamBuildingDto.getCreatedAt())
                .build();
    }

    private boolean isUpdateTeamBuilding(TeamBuildingDto teamBuildingDto){
        return teamBuildingDto.getTeamList() != null;
    }

    private List<Team> teamDtoListToTeamDomainList(List<TeamDto> teamDtoList){
        List<Team> teamList = new ArrayList<>();
        for (TeamDto teamDto : teamDtoList){
            teamList.add(teamInTeamBuildingMapper.teamDtoToTeamDomain(teamDto));
        }
        return teamList;
    }



    public List<TeamBuildingDto> teamBuildingDomainListToTeamBuildingDtoList(List<TeamBuilding> teamBuildingList){
        List<TeamBuildingDto> teamBuildingDtoList = new ArrayList<>();
        for (TeamBuilding teamBuilding : teamBuildingList){
            teamBuildingDtoList.add(teamBuildingDomainToTeamBuildingDto(teamBuilding));
        }
        return teamBuildingDtoList;
    }

    public TeamBuildingDto teamBuildingDomainToTeamBuildingDto(TeamBuilding teamBuilding){
        TeamBuildingDto.Builder builder = TeamBuildingDto.builder()
                .teamBuildingId(teamBuilding.getTeamBuildingId())
                .teamBuildingTitle(teamBuilding.getTeamBuildingTitle())
                .lastModifiedAt(teamBuilding.getLastModifiedAt())
                .createdAt(teamBuilding.getCreatedAt());
        if (!isTeamListEmpty(teamBuilding)){
            return builder
                    .teamDtoList(teamDomainListToTeamDtoList(teamBuilding.getTeamList()))
                    .build();
        }
        return builder.build();
    }

    private boolean isTeamListEmpty(TeamBuilding teamBuilding){
        return teamBuilding.getTeamList() == null;
    }

    private List<TeamDto> teamDomainListToTeamDtoList(List<Team> teamList){
        List<TeamDto> teamDtoList = new ArrayList<>();
        for (Team team : teamList){
            teamDtoList.add(teamInTeamBuildingMapper.teamDomainToTeamDto(team));
        }
        return teamDtoList;
    }

}
