package org.waldreg.repository.teambuildinguserinfo;

import org.springframework.stereotype.Repository;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.team.spi.TeamUserRepository;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingUserRepository;

@Repository
public class MemoryTeamBuildingUserInfoRepository implements TeamBuildingUserRepository, TeamUserRepository{

    @Override
    public UserDto getUserInfoByUserId(String userId){
        return null;
    }

    @Override
    public boolean isExistUserByUserId(String userId){
        return false;
    }

}