package org.waldreg.repository.team;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.teambuilding.TeamBuilding;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryTeamBuildingStorage;
import org.waldreg.repository.MemoryTeamStorage;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.team.spi.TeamRepository;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingsTeamRepository;

@Repository
public class MemoryTeamRepository implements TeamRepository, TeamBuildingsTeamRepository{

    private final TeamMapper teamMapper;

    private final MemoryTeamStorage memoryTeamStorage;

    private final MemoryTeamBuildingStorage memoryTeamBuildingStorage;

    public MemoryTeamRepository(TeamMapper teamMapper, MemoryTeamStorage memoryTeamStorage, MemoryTeamBuildingStorage memoryTeamBuildingStorage){
        this.teamMapper = teamMapper;
        this.memoryTeamStorage = memoryTeamStorage;
        this.memoryTeamBuildingStorage = memoryTeamBuildingStorage;
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
        Team team = teamMapper.teamDtoToTeamDomain(teamDto);
        memoryTeamStorage.updateTeam(team);
        modifyTeamBuildingTeamList(teamDto.getTeamBuildingId());
    }

    @Override
    public void deleteTeamById(int teamId){
        TeamDto teamDto = readTeamById(teamId);
        modifyTeamBuildingTeamList(teamDto.getTeamBuildingId());
        memoryTeamStorage.deleteTeamById(teamId);
    }

    private void modifyTeamBuildingTeamList(int teamBuildingId){
        List<TeamDto> teamDtoList = readAllTeamByTeamBuildingId(teamBuildingId);
        TeamBuilding teamBuilding = memoryTeamBuildingStorage.readTeamBuildingById(teamBuildingId);
        teamBuilding.setTeamList(teamMapper.teamDtoListToTeamDomainList(teamDtoList));

    }

    @Override
    public List<TeamDto> readAllTeamByTeamBuildingId(int teamBuildingId){
        List<Team> teamList = memoryTeamStorage.readAllTeamByTeamBuildingId(teamBuildingId);
        return teamMapper.teamDomainListToTeamDtoList(teamList);
    }

    @Override
    public List<UserDto> readAllUserByTeamBuildingId(int teamBuildingId){
        List<User> userList = memoryTeamStorage.readAllUserByTeamBuildingId(teamBuildingId);
        return teamMapper.userDomainListToUserDtoList(userList);
    }

    @Override
    public boolean isExistTeam(int teamId){
        return memoryTeamStorage.readTeamById(teamId) != null;
    }

}
