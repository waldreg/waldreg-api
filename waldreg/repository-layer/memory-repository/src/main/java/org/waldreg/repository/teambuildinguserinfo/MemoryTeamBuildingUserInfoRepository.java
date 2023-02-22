package org.waldreg.repository.teambuildinguserinfo;

import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.team.spi.TeamUserRepository;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingUserRepository;

@Repository
public class MemoryTeamBuildingUserInfoRepository implements TeamUserRepository, TeamBuildingUserRepository{

    private final MemoryUserStorage memoryUserStorage;

    private final TeamBuildingUserInfoMapper teamBuildingUserInfoMapper;

    public MemoryTeamBuildingUserInfoRepository(MemoryUserStorage memoryUserStorage, TeamBuildingUserInfoMapper teamBuildingUserInfoMapper){
        this.memoryUserStorage = memoryUserStorage;
        this.teamBuildingUserInfoMapper = teamBuildingUserInfoMapper;
    }

    @Override
    public UserDto getUserInfoByUserId(String userId){
        User user = memoryUserStorage.readUserByUserId(userId);
        return teamBuildingUserInfoMapper.userDomainToUserDto(user);
    }

    @Override
    public boolean isExistUserByUserId(String userId){
        return memoryUserStorage.isExistUserId(userId);
    }

}
