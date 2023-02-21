package org.waldreg.teambuilding.teambuilding.management;

import java.util.List;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingRequestDto;

public interface TeamBuildingManager{

    void createTeamBuilding(TeamBuildingRequestDto teamBuildingRequestDto);

    TeamBuildingDto readTeamBuildingById(int teamBuildingId);

    List<TeamBuildingDto> readAllTeamBuilding(int startIdx, int endIdx);

    void updateTeamBuildingTitleById(int teamBuildingId, String teamBuildingTitle);

    void deleteTeamBuildingById(int teamBuildingId);

}
