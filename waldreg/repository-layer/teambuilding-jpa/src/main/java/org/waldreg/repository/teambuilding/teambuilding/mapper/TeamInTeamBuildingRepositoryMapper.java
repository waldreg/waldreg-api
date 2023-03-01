package org.waldreg.repository.teambuilding.teambuilding.mapper;

import java.util.List;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.teambuilding.dto.TeamDto;

public interface TeamInTeamBuildingRepositoryMapper{

    List<TeamDto> teamListToTeamDtoList(List<Team> teamList);

    TeamDto teamToTeamDto(Team team);

}
