package org.waldreg.user.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.waldreg.user.dto.UserDto;
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


}
