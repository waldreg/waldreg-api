package org.waldreg.repository.user;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryJoiningPoolStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.spi.JoiningPoolRepository;

@Repository
public class MemoryJoiningPoolRepository implements JoiningPoolRepository{

    @Override
    public void createUser(UserDto userDto){

    }

    @Override
    public int readJoiningPoolMaxIdx(){
        return 0;
    }

    @Override
    public List<UserDto> readUserJoiningPool(int startIdx, int endIdx){
        return null;
    }

    @Override
    public void approveJoin(String userId){

    }

    @Override
    public void rejectJoin(String userId){

    }

    @Override
    public boolean isExistUserId(String userId){
        return false;
    }

}
