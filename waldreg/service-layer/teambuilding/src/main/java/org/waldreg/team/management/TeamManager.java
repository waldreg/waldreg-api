package org.waldreg.team.management;

import org.waldreg.team.dto.TeamDto;
import org.waldreg.team.dto.TeamRequestDto;

public interface TeamManager{

    void createTeam(int teamBuildingId, TeamRequestDto teamRequestDto);

    void updateTeamById(int teamId, TeamRequestDto teamRequestDto);

    TeamDto readTeamById(int teamId);

    void updateTeamNameById(int teamId, String teamName);

    void deleteTeamById(int teamId);

}
