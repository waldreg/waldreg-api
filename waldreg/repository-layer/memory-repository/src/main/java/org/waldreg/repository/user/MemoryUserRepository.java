package org.waldreg.repository.user;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.character.exception.UnknownCharacterException;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.exception.DuplicatedUserIdException;
import org.waldreg.user.spi.UserRepository;

@Repository
public class MemoryUserRepository implements UserRepository{

    private final UserMapper userMapper;

    private final MemoryUserStorage memoryUserStorage;

    private final MemoryCharacterStorage memoryCharacterStorage;

    @Autowired
    public MemoryUserRepository(UserMapper userMapper, MemoryUserStorage memoryUserStorage, MemoryCharacterStorage memoryCharacterStorage){
        this.userMapper = userMapper;
        this.memoryUserStorage = memoryUserStorage;
        this.memoryCharacterStorage = memoryCharacterStorage;
    }

    @Override
    public void createUser(UserDto userDto){
        throwIfDuplicatedUserId(userDto.getUserId());
        User user = userMapper.userDtoToUserDomain(userDto);
        memoryUserStorage.createUser(user);
    }

    public void throwIfDuplicatedUserId(String userId){
        try{
            memoryUserStorage.readUserByUserId(userId);
        } catch (RuntimeException isNotDuplicated){
            return;
        }
        throw new DuplicatedUserIdException("Duplicated user_id \""+userId+"\"");
    }

    @Override
    public UserDto readUserById(int id){
        return userMapper.userDomainToUserDto(memoryUserStorage.readUserById(id));
    }

    @Override
    public UserDto readUserByUserId(String userId){
        User user = memoryUserStorage.readUserByUserId(userId);
        return userMapper.userDomainToUserDto(user);
    }

    @Override
    public List<UserDto> readUserList(int startIdx, int endIdx){
        return userMapper.userDomainListToUserDtoList(memoryUserStorage.readUserList(startIdx, endIdx));
    }

    @Override
    public void updateUser(int id, UserDto userDto){
        User user = memoryUserStorage.readUserById(id);
        user.setUserPassword(userDto.getUserPassword());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setName(userDto.getName());
        memoryUserStorage.updateUser(id, user);
    }

    @Override
    public int readMaxIdx(){
        return memoryUserStorage.readMaxIdx();
    }

    @Override
    public void deleteById(int id){
        memoryUserStorage.deleteById(id);
    }

    @Override
    public void updateCharacter(int id, String characterName){
        User user = memoryUserStorage.readUserById(id);
        throwIfCharacterDoesNotExist(characterName);
        memoryUserStorage.updateCharacterOfUser(id, characterName);
    }

    public void throwIfCharacterDoesNotExist(String characterName){
        if (memoryCharacterStorage.readCharacterByName(characterName) == null){
            throw new UnknownCharacterException(characterName);
        }
    }

}
