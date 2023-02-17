package org.waldreg.team.spi;

import org.waldreg.team.dto.TeamDto;

public interface TeamRepository{

    void createTeam(TeamDto teamDto);

    TeamDto readTeamById(int teamId);

    void updateTeamById(int teamId, TeamDto teamDto);

    void deleteTeamById(int teamId);

}
