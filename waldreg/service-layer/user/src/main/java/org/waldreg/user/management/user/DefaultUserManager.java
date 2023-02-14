package org.waldreg.user.management.user;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.character.exception.UnknownCharacterException;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.exception.InvalidRangeException;
import org.waldreg.user.exception.UnknownIdException;
import org.waldreg.user.exception.UnknownUserIdException;
import org.waldreg.user.management.PerPage;
import org.waldreg.user.spi.JoiningPoolRepository;
import org.waldreg.user.spi.UserRepository;
import org.waldreg.user.spi.CharacterRepository;

@Service
public class DefaultUserManager implements UserManager{

    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;

    @Autowired
    public DefaultUserManager(UserRepository userRepository, CharacterRepository characterRepository){
        this.userRepository = userRepository;
        this.characterRepository = characterRepository;
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
        throwIfInvalidRangeDetected(startIdx, endIdx, maxIdx);
        endIdx = adjustEndIdx(startIdx, endIdx, maxIdx);
        return userRepository.readUserList(startIdx, endIdx);
    }

    @Override
    public int readMaxIdx(){
        return userRepository.readMaxIdx();
    }

    private void throwIfInvalidRangeDetected(int startIdx, int endIdx, int maxIdx){
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
        userRepository.updateUser(id, userDto);
    }

    @Override
    public void updateCharacter(int id, String character){
        throwIfUnknownId(id);
        throwIfCharacterDoesNotExist(character);
        userRepository.updateCharacter(id, character);
    }

    private void throwIfUnknownId(int id){
        if (!userRepository.isExistId(id)){
            throw new UnknownIdException("Unknown id \"" + id + "\"");
        }
    }

    public void throwIfCharacterDoesNotExist(String characterName){
        if (!characterRepository.isExistCharacterName(characterName)){
            throw new UnknownCharacterException(characterName);
        }
    }

    @Override
    public void deleteById(int id){
        userRepository.deleteById(id);
    }

}