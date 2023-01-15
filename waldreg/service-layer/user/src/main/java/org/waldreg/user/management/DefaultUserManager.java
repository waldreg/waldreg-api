package org.waldreg.user.management;

import org.waldreg.user.dto.UserDto;
import org.waldreg.user.spi.UserRepository;

public class DefaultUserManager implements UserManager{

    private final UserRepository userRepository;

    public DefaultUserManager(UserRepository userRepository){this.userRepository = userRepository;}

    @Override
    public void createUser(UserDto userDto){
        userRepository.createUser(userDto);
    }


}
