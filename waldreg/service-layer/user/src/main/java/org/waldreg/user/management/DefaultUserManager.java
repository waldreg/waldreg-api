package org.waldreg.user.management;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.exception.InvalidRangeException;
import org.waldreg.user.spi.UserRepository;

public class DefaultUserManager implements UserManager{

    private final UserRepository userRepository;

    public DefaultUserManager(@Autowired UserRepository userRepository){this.userRepository = userRepository;}

    @Override
    public void createUser(UserDto userDto){
        userRepository.createUser(userDto);
    }

    @Override
    public UserDto readUserById(int id){
        return userRepository.readUserById(id);
    }

    @Override
    public UserDto readUserByName(String name){
        return userRepository.readUserByName(name);
    }

    @Override
    public List<UserDto> readUserList(int startIdx, int endIdx){
        int maxIdx = userRepository.readMaxIdx();
        throwIfInvalidRange(startIdx, endIdx, maxIdx);
        return userRepository.readUserList(startIdx, endIdx);
    }

    private void throwIfInvalidRange(int stIdx, int enIdx, int maxIdx){
        if(stIdx>maxIdx||enIdx>maxIdx||stIdx>enIdx){
            throw new InvalidRangeException(stIdx,enIdx);
        }
    }


}
