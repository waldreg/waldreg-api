package org.waldreg.repository.user;

import java.util.List;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.exception.DuplicatedUserIdException;
import org.waldreg.user.spi.UserRepository;

public class MemoryUserRepository implements UserRepository{

    private final MemoryUserStorage memoryUserStorage;

    public MemoryUserRepository(MemoryUserStorage memoryUserStorage){this.memoryUserStorage = memoryUserStorage;}

    @Override
    public void createUser(UserDto userDto){
        throwIfDuplicatedUserId(userDto.getUserId());
        User user = User.builder()
                .name(userDto.getName())
                .userId(userDto.getUserId())
                .userPassword(userDto.getUserPassword())
                .phoneNumber(userDto.getPhoneNumber())
                .build();
        memoryUserStorage.createUser(user);
    }

    @Override
    public UserDto readUserById(int id){
        return null;
    }

    @Override
    public UserDto readUserByUserId(String userId){
        return null;
    }

    @Override
    public List<UserDto> readUserList(int startIdx, int endIdx){
        return null;
    }

    @Override
    public void updateUser(int idx, UserDto userDto){

    }

    @Override
    public int readMaxIdx(){
        return 0;
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
