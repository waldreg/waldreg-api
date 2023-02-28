package org.waldreg.repository.teambuilding.teamuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.domain.user.User;
import org.waldreg.repository.teambuilding.teamuser.mapper.TeamUserRepositoryMapper;
import org.waldreg.repository.teambuilding.teamuser.repository.JpaTeamUserRepository;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.team.spi.TeamUserRepository;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingUserRepository;

@Repository
public class TeamUserRepositoryServiceProvider implements TeamBuildingUserRepository, TeamUserRepository{

    private final JpaTeamUserRepository jpaTeamUserRepository;
    private final TeamUserRepositoryMapper teamUserRepositoryMapper;

    @Autowired
    public TeamUserRepositoryServiceProvider(JpaTeamUserRepository jpaTeamUserRepository, TeamUserRepositoryMapper teamUserRepositoryMapper){
        this.jpaTeamUserRepository = jpaTeamUserRepository;
        this.teamUserRepositoryMapper = teamUserRepositoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserInfoByUserId(String userId){
        User user = jpaTeamUserRepository.findByUserId(userId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user with id \"" + userId + "\"");}
        );
        return teamUserRepositoryMapper.userToUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistUserByUserId(String userId){
        return jpaTeamUserRepository.existsByUserId(userId);
    }

}
