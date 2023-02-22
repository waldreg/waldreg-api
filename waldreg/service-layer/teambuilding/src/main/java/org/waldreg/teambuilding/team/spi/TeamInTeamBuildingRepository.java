package org.waldreg.teambuilding.team.spi;

import org.waldreg.teambuilding.dto.TeamDto;

public interface TeamInTeamBuildingRepository{

    void addTeamInTeamBuildingTeamList(TeamDto teamDto);
    boolean isExistTeamBuilding(int teamBuildingId);

}
