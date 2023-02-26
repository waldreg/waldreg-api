package org.waldreg.repository.user;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.spi.UserRepository;

@Repository
public class MemoryUserRepository implements UserRepository{

    @Override
    public void createUser(UserDto userDto){

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
    public void updateCharacter(int id, String character){

    }

    @Override
    public int readMaxIdx(){
        return 0;
    }

    @Override
    public void deleteById(int id){

    }

    @Override
    public boolean isExistUserId(String userId){
        return false;
    }

    @Override
    public boolean isExistId(int id){
        return false;
    }

}
