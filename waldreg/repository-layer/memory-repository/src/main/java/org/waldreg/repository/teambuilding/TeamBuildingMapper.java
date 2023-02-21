package org.waldreg.repository.teambuilding;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.domain.teambuilding.TeamBuilding;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingDto;

@Service
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
                .teamList(teamInTeamBuildingMapper.teamDtoListToTeamDomainList(teamBuildingDto.getTeamList()))
                .lastModifiedAt(teamBuildingDto.getLastModifiedAt())
                .createdAt(teamBuildingDto.getCreatedAt())
                .build();
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
                    .teamDtoList(teamInTeamBuildingMapper.teamDomainListToTeamDtoList(teamBuilding.getTeamList()))
                    .build();
        }
        return builder.build();
    }

    private boolean isTeamListEmpty(TeamBuilding teamBuilding){
        return teamBuilding.getTeamList() == null;
    }


}
