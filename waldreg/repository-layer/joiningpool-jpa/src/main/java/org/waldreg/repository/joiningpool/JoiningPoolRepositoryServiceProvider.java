package org.waldreg.repository.joiningpool;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.domain.user.UserJoiningPool;
import org.waldreg.repository.joiningpool.mapper.JoiningPoolMapper;
import org.waldreg.repository.joiningpool.repository.JpaJoiningPoolRepository;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.spi.JoiningPoolRepository;

@Repository
public class JoiningPoolRepositoryServiceProvider implements JoiningPoolRepository{

    @Autowired
    private JpaJoiningPoolRepository jpaJoiningPoolRepository;
    @Autowired
    private JoiningPoolMapper joiningPoolMapper;

    @Override
    @Transactional
    public void createUser(UserDto userDto){
        UserJoiningPool userJoiningPool = joiningPoolMapper.userDtoToUserJoiningPool(userDto);
        jpaJoiningPoolRepository.save(userJoiningPool);
    }

    @Override
    public int readJoiningPoolMaxIdx(){
        return (int) jpaJoiningPoolRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> readUserJoiningPool(int startIdx, int endIdx){
        List<UserJoiningPool> userJoiningPoolList = jpaJoiningPoolRepository.readAllJoiningPoolUser(startIdx - 1, endIdx - startIdx + 1);
        return joiningPoolMapper.userJoiningPoolListToUserDtoList(userJoiningPoolList);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByUserId(String userId){
        UserJoiningPool userJoiningPool = getUserJoiningPoolByUserId(userId);
        return joiningPoolMapper.userJoiningPoolToUserDto(userJoiningPool);
    }

    private UserJoiningPool getUserJoiningPoolByUserId(String userId){
        return jpaJoiningPoolRepository.findByUserId(userId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find userJoiningPool user id \"" + userId + "\"");}
        );
    }

    @Override
    @Transactional
    public void deleteUserByUserId(String userId){
        jpaJoiningPoolRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistUserId(String userId){
        return jpaJoiningPoolRepository.existsByUserInfoUserId(userId);
    }

}
