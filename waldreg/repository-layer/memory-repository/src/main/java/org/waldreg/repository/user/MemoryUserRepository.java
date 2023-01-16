package org.waldreg.repository.user;

import java.util.ArrayList;
import java.util.List;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.exception.DuplicatedUserIdException;
import org.waldreg.user.spi.UserRepository;

public class MemoryUserRepository implements UserRepository{

    private final UserMapper userMapper;

    private final MemoryUserStorage memoryUserStorage;

    public MemoryUserRepository(UserMapper userMapper, MemoryUserStorage memoryUserStorage){
        this.userMapper = userMapper;
        this.memoryUserStorage = memoryUserStorage;
    }

    @Override
    public void createUser(UserDto userDto){
        throwIfDuplicatedUserId(userDto.getUserId());
        User user = userMapper.userDtoToUserDomain(userDto);
        memoryUserStorage.createUser(user);
    }

    @Override
    public UserDto readUserById(int id){
        return null;
    }

    @Override
    public UserDto readUserByUserId(String userId){
        User user = memoryUserStorage.readUserByUserId(userId);
        return userMapper.userDomainToUserDto(user);
    }

    @Override
    public List<UserDto> readUserList(int startIdx, int endIdx){
        List<User> userList = memoryUserStorage.readUserList(startIdx, endIdx);
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userList){
            userDtoList.add(userMapper.userDomainToUserDto(user));
        }
        return userDtoList;
    }

    @Override
    public void updateUser(int idx, UserDto userDto){

    }

    @Override
    public int readMaxIdx(){
        return memoryUserStorage.readMaxIdx();
    }

    @Override
    public void deleteById(int id){

    }

    public void throwIfDuplicatedUserId(String userId){
        try{
            memoryUserStorage.readUserByUserId(userId);
        } catch (RuntimeException isNotDuplicated){
            return;
        }
        throw new DuplicatedUserIdException(userId);
    }

}
