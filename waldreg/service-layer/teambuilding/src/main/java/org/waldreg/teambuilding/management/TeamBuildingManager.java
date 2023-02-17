package org.waldreg.teambuilding.management;

import java.util.List;
import org.waldreg.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.dto.TeamBuildingRequestDto;

public interface TeamBuildingManager{

    TeamBuildingDto readTeamBuildingById(int teamBuildingId);

    void createTeamBuilding(TeamBuildingRequestDto teamBuildingRequestDto);

    List<TeamBuildingDto> readAllTeamBuilding(int startIdx, int endIdx);

}
