package org.waldreg.user.management.joiningpool;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.exception.InvalidRangeException;
import org.waldreg.user.management.PerPage;
import org.waldreg.user.spi.JoiningPoolRepository;

@Service
public class DefaultJoiningPoolManager implements JoiningPoolManager{

    private final JoiningPoolRepository joiningPoolRepository;

    @Autowired
    public DefaultJoiningPoolManager(JoiningPoolRepository joiningPoolRepository){
        this.joiningPoolRepository = joiningPoolRepository;
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
        joiningPoolRepository.approveJoin(userId);
    }

    @Override
    public void rejectJoin(String userId){
        joiningPoolRepository.rejectJoin(userId);
    }

}
