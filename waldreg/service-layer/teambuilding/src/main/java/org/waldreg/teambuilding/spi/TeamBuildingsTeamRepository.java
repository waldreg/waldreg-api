package org.waldreg.teambuilding.spi;

import org.waldreg.teambuilding.dto.TeamDto;

public interface TeamBuildingsTeamRepository{

    TeamDto createTeam(TeamDto teamDto);

}
