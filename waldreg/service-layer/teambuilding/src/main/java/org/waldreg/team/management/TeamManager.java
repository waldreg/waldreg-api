package org.waldreg.team.management;

import org.waldreg.team.dto.TeamRequestDto;

public interface TeamManager{

    void createTeam(int teamBuildingId, TeamRequestDto teamRequestDto);

}
