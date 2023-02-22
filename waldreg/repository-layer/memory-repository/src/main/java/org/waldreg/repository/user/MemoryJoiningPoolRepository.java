package org.waldreg.repository.user;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryJoiningPoolStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.spi.JoiningPoolRepository;

@Repository
public class MemoryJoiningPoolRepository implements JoiningPoolRepository{

    private final UserMapper userMapper;
    private final MemoryUserStorage memoryUserStorage;
    private final MemoryJoiningPoolStorage memoryJoiningPoolStorage;
    private final MemoryCharacterStorage memoryCharacterStorage;


    public MemoryJoiningPoolRepository(UserMapper userMapper, MemoryUserStorage memoryUserStorage, MemoryJoiningPoolStorage memoryJoiningPoolStorage, MemoryCharacterStorage memoryCharacterStorage){
        this.userMapper = userMapper;
        this.memoryUserStorage = memoryUserStorage;
        this.memoryJoiningPoolStorage = memoryJoiningPoolStorage;
        this.memoryCharacterStorage = memoryCharacterStorage;
    }

    @Override
    public void createUser(UserDto userDto){
        User user = userMapper.userDtoToUserDomain(userDto);
        user.setCharacter(getCharacterByName(userDto.getCharacter()));
        memoryJoiningPoolStorage.createUser(user);
    }

    private Character getCharacterByName(String characterName){
        return memoryCharacterStorage.readCharacterByName(characterName);
    }

    @Override
    public int readJoiningPoolMaxIdx(){
        return memoryJoiningPoolStorage.readMaxIdx();
    }

    @Override
    public List<UserDto> readUserJoiningPool(int startIdx, int endIdx){
        return userMapper.userDomainListToUserDtoList(memoryJoiningPoolStorage.readJoiningPoolList(startIdx,endIdx));
    }

    @Override
    public void approveJoin(String userId){
        User user = memoryJoiningPoolStorage.getUserByUserId(userId);
        memoryUserStorage.createUser(user);
        memoryJoiningPoolStorage.deleteUserByUserId(userId);
    }

    @Override
    public void rejectJoin(String userId){
        memoryJoiningPoolStorage.deleteUserByUserId(userId);
    }

    @Override
    public boolean isExistUserId(String userId){
        return memoryJoiningPoolStorage.isExistUserId(userId);
    }

}
