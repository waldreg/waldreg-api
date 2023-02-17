package org.waldreg.teambuilding.spi;

import java.util.List;
import org.waldreg.teambuilding.dto.TeamBuildingDto;

public interface TeamBuildingRepository{

    void createTeamBuilding(TeamBuildingDto teamBuildingDto);

    TeamBuildingDto readTeamBuildingById(int teamBuildingId);

    List<TeamBuildingDto> readAllTeamBuilding(int startIdx, int endIdx);

    void updateTeamBuildingTitleById(int teamBuildingId, TeamBuildingDto teamBuildingDto);

}
