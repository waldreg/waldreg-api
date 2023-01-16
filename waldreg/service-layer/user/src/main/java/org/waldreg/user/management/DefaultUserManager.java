package org.waldreg.user.management;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.waldreg.character.dto.CharacterDto;
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
    public UserDto readUserByUserId(String userId){
        return userRepository.readUserByUserId(userId);
    }

    @Override
    public List<UserDto> readUserList(int startIdx, int endIdx){
        int maxIdx = userRepository.readMaxIdx();
        throwIfInvalidRangeDetected(startIdx, endIdx, maxIdx);
        return userRepository.readUserList(startIdx, endIdx);
    }

    @Override
    public void updateUser(int id, UserDto userDto){
        userRepository.updateUser(id, userDto);
    }

    @Override
    public void updateCharacter(UserDto userDto, CharacterDto characterDto){
        UserDto characterUserDto = UserDto.builder()
                .character(characterDto)
                .build();
        userRepository.updateUser(userDto.getId(), characterUserDto);
    }

    @Override
    public void deleteById(int id){
        userRepository.deleteById(id);
    }

    private void throwIfInvalidRangeDetected(int startIdx, int endIdx, int maxIdx){
        if (startIdx > maxIdx || endIdx > maxIdx || startIdx > endIdx){
            throw new InvalidRangeException(startIdx, endIdx);
        }
    }

}
