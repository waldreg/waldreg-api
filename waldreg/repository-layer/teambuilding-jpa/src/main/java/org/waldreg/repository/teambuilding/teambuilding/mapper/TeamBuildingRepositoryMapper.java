package org.waldreg.repository.teambuilding.teambuilding.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.domain.teambuilding.TeamBuilding;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingDto;

@Service
public class TeamBuildingRepositoryMapper{

    private final TeamInTeamBuildingRepositoryMapper teamInTeamBuildingRepositoryMapper;

    @Autowired
    public TeamBuildingRepositoryMapper(TeamInTeamBuildingRepositoryMapper teamInTeamBuildingRepositoryMapper){
        this.teamInTeamBuildingRepositoryMapper = teamInTeamBuildingRepositoryMapper;
    }

    public TeamBuilding teamBuildingTitleToTeamBuildingDomain(String teamBuildingTitle){
        return TeamBuilding.builder()
                .teamBuildingTitle(teamBuildingTitle)
                .build();
    }

    public List<TeamBuildingDto> teamBuildingListToTeamBuildingDtoList(List<TeamBuilding> teamBuildingList){
        List<TeamBuildingDto> teamBuildingDtoList = new ArrayList<>();
        for (TeamBuilding teamBuilding : teamBuildingList){
            teamBuildingDtoList.add(teamBuildingToTeamBuildingDto(teamBuilding));
        }
        return teamBuildingDtoList;
    }

    public TeamBuildingDto teamBuildingToTeamBuildingDto(TeamBuilding teamBuilding){
        TeamBuildingDto.Builder builder = TeamBuildingDto.builder()
                .teamBuildingId(teamBuilding.getTeamBuildingId())
                .teamBuildingTitle(teamBuilding.getTeamBuildingTitle())
                .lastModifiedAt(teamBuilding.getLastModifiedAt())
                .createdAt(teamBuilding.getCreatedAt());
        if (!isTeamListEmpty(teamBuilding)){
            return builder
                    .teamDtoList(teamInTeamBuildingRepositoryMapper.teamListToTeamDtoList(teamBuilding.getTeamList()))
                    .build();
        }
        return builder.build();
    }

    private boolean isTeamListEmpty(TeamBuilding teamBuilding){
        return teamBuilding.getTeamList() == null;
    }

}
