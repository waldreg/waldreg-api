package org.waldreg.teambuilding.team.spi;

import java.util.List;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;

public interface TeamRepository{

    TeamDto createTeam(TeamDto teamDto);

    TeamDto readTeamById(int teamId);

    void updateTeamById(int teamId, TeamDto teamDto);

    void deleteTeamById(int teamId);

    List<TeamDto> readAllTeamByTeamBuildingId(int teamBuildingId);

    List<UserDto> readAllUserByTeamBuildingId(int teamBuildingId);

    boolean isExistTeam(int teamId);

}
