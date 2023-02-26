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

    @Override
    public TeamDto createTeam(TeamDto teamDto){
        return null;
    }

    @Override
    public TeamDto readTeamById(int teamId){
        return null;
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
