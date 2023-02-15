package org.waldreg.teambuilding.spi;

import org.waldreg.teambuilding.dto.TeamBuildingDto;

public interface TeamBuildingRepository{

    void createTeamBuilding(TeamBuildingDto teamBuildingDto);

}
