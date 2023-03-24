package org.waldreg.user.management.user;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.character.exception.NoPermissionException;
import org.waldreg.character.exception.UnknownCharacterException;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.exception.ContentOverflowException;
import org.waldreg.user.exception.DuplicatedUserIdException;
import org.waldreg.user.exception.InvalidRangeException;
import org.waldreg.user.exception.UnknownIdException;
import org.waldreg.user.exception.UnknownUserIdException;
import org.waldreg.user.management.PerPage;
import org.waldreg.user.spi.JoiningPoolRepository;
import org.waldreg.user.spi.UserRepository;
import org.waldreg.user.spi.UsersCharacterRepository;

@Service
public class DefaultUserManager implements UserManager{

    private final UserRepository userRepository;
    private final JoiningPoolRepository joiningPoolRepository;
    private final UsersCharacterRepository usersCharacterRepository;

    @Autowired
    public DefaultUserManager(UserRepository userRepository, JoiningPoolRepository joiningPoolRepository, UsersCharacterRepository usersCharacterRepository){
        this.userRepository = userRepository;
        this.joiningPoolRepository = joiningPoolRepository;
        this.usersCharacterRepository = usersCharacterRepository;
    }

    @Override
    public void createUser(UserDto userDto){
        throwIfDuplicatedUserId(userDto.getUserId());
        throwIfUserIdOverflowDetected(userDto);
        throwIfUserNameOverflowDetected(userDto);
        setDefaultCharacter(userDto);
        userRepository.createUser(userDto);
    }

    public void throwIfDuplicatedUserId(String userId){
        if (userRepository.isExistUserId(userId) || joiningPoolRepository.isExistUserId(userId)){
            throw new DuplicatedUserIdException("Duplicated user_id \"" + userId + "\"");
        }
    }

    private void throwIfUserIdOverflowDetected(UserDto userDto){
        if(userDto.getUserId().length()>50){
            throw new ContentOverflowException("USER-411","User_id length cannot be over 50 current length \""+userDto.getUserId().length()+"\"");
        }
    }

    private void setDefaultCharacter(UserDto userDto){
        if (userDto.getUserId().equals("Admin")){
            throwIfUnknownCharacter("Admin");
            userDto.setCharacter("Admin");
            return;
        }
        throwIfUnknownCharacter("Guest");
        userDto.setCharacter("Guest");
    }

    private void throwIfUnknownCharacter(String characterName){
        if (!usersCharacterRepository.isExistCharacterName(characterName)){
            throw new UnknownCharacterException(characterName);
        }
    }

    @Override
    public UserDto readUserById(int id){
        throwIfUnknownId(id);
        return userRepository.readUserById(id);
    }

    @Override
    public UserDto readUserByUserId(String userId){
        throwIfUnknownUserId(userId);
        return userRepository.readUserByUserId(userId);
    }

    private void throwIfUnknownUserId(String userId){
        if (!userRepository.isExistUserId(userId)){
            throw new UnknownUserIdException("Unknown user_id \"" + userId + "\"");
        }
    }

    @Override
    public List<UserDto> readUserList(int startIdx, int endIdx){
        int maxIdx = readMaxIdx();
        throwIfInvalidRangeDetected(startIdx, endIdx);
        endIdx = adjustEndIdx(startIdx, endIdx, maxIdx);
        return userRepository.readUserList(startIdx, endIdx);
    }

    @Override
    public int readMaxIdx(){
        return userRepository.readMaxIdx();
    }

    @Override
    public List<UserDto> readSpecificUserList(List<Integer> idList){
        idList.stream().forEach(id -> throwIfUnknownId(id));
        return userRepository.readSpecificUserList(idList);
    }

    private void throwIfInvalidRangeDetected(int startIdx, int endIdx){
        if (startIdx > endIdx || 1 > endIdx){
            throw new InvalidRangeException("Invalid range start-idx \"" + startIdx + "\", end-idx \"" + endIdx + "\"");
        }
    }

    private int adjustEndIdx(int startIdx, int endIdx, int maxIdx){
        endIdx = adjustEndIdxToMaxIdx(endIdx, maxIdx);
        endIdx = adjustEndIdxToPerPage(startIdx, endIdx);
        return endIdx;
    }

    private int adjustEndIdxToPerPage(int startIdx, int endIdx){
        if (endIdx - startIdx + 1 > PerPage.PER_PAGE){
            return startIdx + PerPage.PER_PAGE - 1;
        }
        return endIdx;
    }

    private int adjustEndIdxToMaxIdx(int endIdx, int maxIdx){
        if (endIdx > maxIdx){
            return maxIdx;
        }
        return endIdx;
    }

    @Override
    public void updateUser(int id, UserDto userDto){
        throwIfUnknownId(id);
        throwIfUserNameOverflowDetected(userDto);
        userRepository.updateUser(id, userDto);
    }

    @Override
    public void updateCharacter(int id, String character){
        throwIfUnknownId(id);
        throwIfAdminId(id);
        throwIfCharacterDoesNotExist(character);
        userRepository.updateCharacter(id, character);
    }

    private void throwIfUnknownId(int id){
        if (!userRepository.isExistId(id)){
            throw new UnknownIdException("Unknown id \"" + id + "\"");
        }
    }

    private void throwIfUserNameOverflowDetected(UserDto userDto){
        if(userDto.getName().length()>50){
            throw new ContentOverflowException("USER-412","User name length cannot be over 50 current length \""+userDto.getName().length()+"\"");
        }
    }

    public void throwIfCharacterDoesNotExist(String characterName){
        if (!usersCharacterRepository.isExistCharacterName(characterName)){
            throw new UnknownCharacterException(characterName);
        }
    }

    @Override
    public void deleteById(int id){
        throwIfUnknownId(id);
        throwIfAdminId(id);
        userRepository.deleteById(id);
    }

    private void throwIfAdminId(int id){
        UserDto userDto = userRepository.readUserById(id);
        if(userDto.getUserId().equals("Admin")){
            throw new NoPermissionException();
        }
    }

}
