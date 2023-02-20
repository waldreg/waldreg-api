package org.waldreg.user.management.joiningpool;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.character.exception.UnknownCharacterException;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.exception.DuplicatedUserIdException;
import org.waldreg.user.exception.InvalidRangeException;
import org.waldreg.user.exception.UnknownUserIdException;
import org.waldreg.user.management.PerPage;
import org.waldreg.user.spi.CharacterRepository;
import org.waldreg.user.spi.JoiningPoolRepository;
import org.waldreg.user.spi.UserRepository;

@Service
public class DefaultJoiningPoolManager implements JoiningPoolManager{

    private final JoiningPoolRepository joiningPoolRepository;
    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;

    @Autowired
    public DefaultJoiningPoolManager(JoiningPoolRepository joiningPoolRepository, UserRepository userRepository, CharacterRepository characterRepository){
        this.joiningPoolRepository = joiningPoolRepository;
        this.userRepository = userRepository;
        this.characterRepository = characterRepository;
    }

    @Override
    public void createUser(UserDto userDto){
        throwIfDuplicatedUserId(userDto.getUserId());
        setDefaultCharacter(userDto);
        joiningPoolRepository.createUser(userDto);
    }

    public void throwIfDuplicatedUserId(String userId){
        if (userRepository.isExistUserId(userId) || joiningPoolRepository.isExistUserId(userId)){
            throw new DuplicatedUserIdException("Duplicated user_id \"" + userId + "\"");
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
        if (!characterRepository.isExistCharacterName(characterName)){
            throw new UnknownCharacterException(characterName);
        }
    }

    @Override
    public List<UserDto> readUserJoiningPool(int startIdx, int endIdx){
        int maxIdx = readJoiningPoolMaxIdx();
        throwIfInvalidRangeDetected(startIdx, endIdx);
        endIdx = adjustEndIdx(startIdx, endIdx, maxIdx);
        return joiningPoolRepository.readUserJoiningPool(startIdx, endIdx);
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
    public int readJoiningPoolMaxIdx(){
        return joiningPoolRepository.readJoiningPoolMaxIdx();
    }


    @Override
    public void approveJoin(String userId){
        throwIfUnknownUserId(userId);
        joiningPoolRepository.approveJoin(userId);
    }

    @Override
    public void rejectJoin(String userId){
        throwIfUnknownUserId(userId);
        joiningPoolRepository.rejectJoin(userId);
    }

    private void throwIfUnknownUserId(String userId){
        if (!joiningPoolRepository.isExistUserId(userId)){
            throw new UnknownUserIdException("Unknown user_id \"" + userId + "\"");
        }
    }

}
