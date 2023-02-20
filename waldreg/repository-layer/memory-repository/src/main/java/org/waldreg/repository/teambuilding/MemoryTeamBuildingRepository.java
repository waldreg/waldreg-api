package org.waldreg.repository.teambuilding;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.teambuilding.TeamBuilding;
import org.waldreg.repository.MemoryTeamBuildingStorage;
import org.waldreg.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.spi.TeamBuildingRepository;

@Repository
public class MemoryTeamBuildingRepository implements TeamBuildingRepository{

    private final TeamBuildingMapper teamBuildingMapper;

    private final MemoryTeamBuildingStorage memoryTeamBuildingStorage;

    @Autowired
    public MemoryTeamBuildingRepository(TeamBuildingMapper teamBuildingMapper, MemoryTeamBuildingStorage memoryTeamBuildingStorage){
        this.teamBuildingMapper = teamBuildingMapper;
        this.memoryTeamBuildingStorage = memoryTeamBuildingStorage;
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
        return null;
    }

    @Override
    public List<TeamBuildingDto> readAllTeamBuilding(int startIdx, int endIdx){
        return null;
    }

    @Override
    public void updateTeamBuildingTitleById(int teamBuildingId, TeamBuildingDto teamBuildingDto){

    }

    @Override
    public void deleteTeamBuildingById(int teamBuildingId){

    }

    @Override
    public boolean isExistTeamBuilding(int teamBuildingId){
        return false;
    }

    @Override
    public int readMaxIdx(){
        return 0;
    }

}
