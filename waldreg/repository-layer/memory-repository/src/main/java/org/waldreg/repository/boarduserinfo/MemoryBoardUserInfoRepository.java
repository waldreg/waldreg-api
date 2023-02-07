package org.waldreg.repository.boarduserinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.board.board.spi.BoardUserRepository;
import org.waldreg.board.dto.UserDto;
import org.waldreg.board.reaction.spi.ReactionUserRepository;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryUserStorage;

@Repository
public class MemoryBoardUserInfoRepository implements BoardUserRepository, ReactionUserRepository{

    private final MemoryUserStorage memoryUserStorage;

    private final UserInfoMapper userInfoMapper;

    @Autowired
    public MemoryBoardUserInfoRepository(MemoryUserStorage memoryUserStorage, UserInfoMapper userInfoMapper){
        this.memoryUserStorage = memoryUserStorage;
        this.userInfoMapper = userInfoMapper;
    }

    @Override
    public UserDto getUserInfo(int id){
        User user = memoryUserStorage.readUserById(id);
        return userInfoMapper.userDomainToUserDto(user);
    }

    @Override
    public UserDto getUserInfoByUserId(String userId){
        User user = memoryUserStorage.readUserByUserId(userId);
        return userInfoMapper.userDomainToUserDto(user);
    }

}
