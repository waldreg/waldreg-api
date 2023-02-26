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

    @Override
    public void addTeamInTeamBuildingTeamList(TeamDto teamDto){

    }

    @Override
    public TeamBuildingDto createTeamBuilding(String title){
        return null;
    }

    @Override
    public void updateTeamListInTeamBuilding(TeamBuildingDto teamBuildingDto){

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
    public void updateTeamBuildingTitleById(int teamBuildingId, String teamBuildingTitle){

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
