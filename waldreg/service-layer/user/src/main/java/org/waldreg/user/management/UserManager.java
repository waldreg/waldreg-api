package org.waldreg.user.management;

import java.util.List;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.user.dto.UserDto;

public interface UserManager{

    void createUser(UserDto userDto);

    UserDto readUserById(int id);

    UserDto readUserByUserId(String userId);

    List<UserDto> readUserList(int stIdx, int enIdx);

    void updateUser(int idx, UserDto userDto);

    void updateCharacter(UserDto userDto, String character);

    void deleteById(int id);


}
