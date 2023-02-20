package org.waldreg.team.spi;

import java.util.List;
import java.util.Optional;
import org.waldreg.team.dto.TeamDto;
import org.waldreg.team.dto.UserDto;

public interface TeamRepository{

    void createTeam(TeamDto teamDto);

    TeamDto readTeamById(int teamId);

    void updateTeamById(int teamId, TeamDto teamDto);

    void deleteTeamById(int teamId);

    List<TeamDto> readAllTeamByTeamBuildingId(int teamBuildingId);

    List<UserDto> readAllUserByTeamBuildingId(int teamBuildingId);

    boolean isExistTeam(int teamId);
}
