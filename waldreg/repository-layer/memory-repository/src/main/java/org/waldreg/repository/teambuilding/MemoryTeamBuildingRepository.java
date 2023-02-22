package org.waldreg.repository.teambuilding;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.teambuilding.TeamBuilding;
import org.waldreg.repository.MemoryTeamBuildingStorage;
import org.waldreg.repository.MemoryTeamStorage;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.team.spi.TeamInTeamBuildingRepository;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingRepository;

@Repository
public class MemoryTeamBuildingRepository implements TeamBuildingRepository, TeamInTeamBuildingRepository{

    private final TeamBuildingMapper teamBuildingMapper;

    private final TeamInTeamBuildingMapper teamInTeamBuildingMapper;

    private final MemoryTeamBuildingStorage memoryTeamBuildingStorage;

    private final MemoryTeamStorage memoryTeamStorage;

    @Autowired
    public MemoryTeamBuildingRepository(TeamBuildingMapper teamBuildingMapper, TeamInTeamBuildingMapper teamInTeamBuildingMapper, MemoryTeamBuildingStorage memoryTeamBuildingStorage, MemoryTeamStorage memoryTeamStorage){
        this.teamBuildingMapper = teamBuildingMapper;
        this.teamInTeamBuildingMapper = teamInTeamBuildingMapper;
        this.memoryTeamBuildingStorage = memoryTeamBuildingStorage;
        this.memoryTeamStorage = memoryTeamStorage;
    }

    @Override
    public TeamBuildingDto createTeamBuilding(String teamBuildingTitle){
        TeamBuilding teamBuilding = teamBuildingMapper.teamBuildingTitleToTeamBuildingDomain(teamBuildingTitle);
        teamBuilding = memoryTeamBuildingStorage.createTeamBuilding(teamBuilding);
        return teamBuildingMapper.teamBuildingDomainToTeamBuildingDto(teamBuilding);
    }

    @Override
    public void updateTeamListInTeamBuilding(TeamBuildingDto teamBuildingDto){
        TeamBuilding teamBuilding = teamBuildingMapper.teamBuildingDtoToTeamBuildingDomain(teamBuildingDto);
        memoryTeamBuildingStorage.updateTeamBuilding(teamBuilding);
    }

    @Override
    public TeamBuildingDto readTeamBuildingById(int teamBuildingId){
        TeamBuilding teamBuilding = memoryTeamBuildingStorage.readTeamBuildingById(teamBuildingId);
        return teamBuildingMapper.teamBuildingDomainToTeamBuildingDto(teamBuilding);
    }

    @Override
    public List<TeamBuildingDto> readAllTeamBuilding(int startIdx, int endIdx){
        List<TeamBuilding> teamBuildingList = memoryTeamBuildingStorage.readAllTeamBuilding(startIdx - 1, endIdx - 1);
        return teamBuildingMapper.teamBuildingDomainListToTeamBuildingDtoList(teamBuildingList);
    }

    @Override
    public void updateTeamBuildingTitleById(int teamBuildingId, String teamBuildingTitle){
        TeamBuilding teamBuilding = memoryTeamBuildingStorage.readTeamBuildingById(teamBuildingId);
        teamBuilding.setTeamBuildingTitle(teamBuildingTitle);
        memoryTeamBuildingStorage.updateTeamBuilding(teamBuilding);
    }

    @Override
    public void deleteTeamBuildingById(int teamBuildingId){
        TeamBuildingDto teamBuildingDto = readTeamBuildingById(teamBuildingId);
        deleteTeamDueToDeleteTeamBuilding(teamBuildingDto.getTeamList());
        memoryTeamBuildingStorage.deleteTeamBuildingById(teamBuildingId);
    }

    private void deleteTeamDueToDeleteTeamBuilding(List<TeamDto> teamDtoList){
        teamDtoList.stream().forEach(i -> memoryTeamStorage.deleteTeamById(i.getTeamId()));
    }

    @Override
    public void addTeamInTeamBuildingTeamList(TeamDto teamDto){
        Team team = teamInTeamBuildingMapper.teamDtoToTeamDomain(teamDto);
        memoryTeamBuildingStorage.addTeamInTeamBuildingTeamList(team);
    }

    @Override
    public boolean isExistTeamBuilding(int teamBuildingId){
        return memoryTeamBuildingStorage.readTeamBuildingById(teamBuildingId) != null;
    }

    @Override
    public int readMaxIdx(){
        return memoryTeamBuildingStorage.getTeamBuildingMaxIdx();
    }

}
