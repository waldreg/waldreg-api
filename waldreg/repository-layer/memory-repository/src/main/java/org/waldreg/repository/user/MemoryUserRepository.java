package org.waldreg.repository.user;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.spi.UserRepository;

@Repository
public class MemoryUserRepository implements UserRepository{

    private final UserMapper userMapper;

    private final MemoryUserStorage memoryUserStorage;

    @Autowired
    public MemoryUserRepository(UserMapper userMapper, MemoryUserStorage memoryUserStorage){
        this.userMapper = userMapper;
        this.memoryUserStorage = memoryUserStorage;
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
    public boolean isExistUserId(String userId){
        return memoryUserStorage.isExistUserId(userId);
    }

    @Override
    public boolean isExistId(int id){
        return memoryUserStorage.isExistId(id);
    }

    @Override
    public void updateCharacter(int id, String characterName){
        User user = memoryUserStorage.readUserById(id);
        memoryUserStorage.updateCharacterOfUser(id, characterName);
    }


}
