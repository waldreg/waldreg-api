package org.waldreg.repository.team;

import java.util.List;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.repository.MemoryTeamStorage;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.team.spi.TeamRepository;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingsTeamRepository;

public class MemoryTeamRepository implements TeamRepository, TeamBuildingsTeamRepository{

    private final TeamMapper teamMapper;

    private final MemoryTeamStorage memoryTeamStorage;

    public MemoryTeamRepository(TeamMapper teamMapper, MemoryTeamStorage memoryTeamStorage){
        this.teamMapper = teamMapper;
        this.memoryTeamStorage = memoryTeamStorage;
    }

    @Override
    public TeamDto createTeam(TeamDto teamDto){
        Team team = teamMapper.teamDtoToTeamDomain(teamDto);
        team = memoryTeamStorage.createTeam(team);
        return teamMapper.teamDomainToTeamDto(team);
    }

    @Override
    public TeamDto readTeamById(int teamId){
        Team team = memoryTeamStorage.readTeamById(teamId);
        return teamMapper.teamDomainToTeamDto(team);
    }

    @Override
    public void updateTeamById(int teamId, TeamDto teamDto){

    }

    @Override
    public void deleteTeamById(int teamId){

    }

    @Override
    public List<TeamDto> readAllTeamByTeamBuildingId(int teamBuildingId){
        return null;
    }

    @Override
    public List<UserDto> readAllUserByTeamBuildingId(int teamBuildingId){
        return null;
    }

    @Override
    public boolean isExistTeam(int teamId){
        return false;
    }

}
