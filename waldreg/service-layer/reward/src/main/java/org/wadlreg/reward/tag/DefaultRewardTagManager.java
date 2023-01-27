package org.wadlreg.reward.tag;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wadlreg.reward.tag.dto.RewardTagDto;
import org.wadlreg.reward.exception.UnknownRewardTagException;
import org.wadlreg.reward.tag.lib.TagExceedClipper;
import org.wadlreg.reward.tag.spi.RewardTagRepository;

@Service
public class DefaultRewardTagManager implements RewardTagManager{

    private final RewardTagRepository rewardTagRepository;
    private final TagExceedClipper tagExceedClipper;
    private final int maxTagTitleLength;
    private final int minTagPoint;
    private final int maxTagPoint;

    @Autowired
    public DefaultRewardTagManager(RewardTagRepository rewardTagRepository, TagExceedClipper tagExceedClipper){
        this.rewardTagRepository = rewardTagRepository;
        this.tagExceedClipper = tagExceedClipper;
        this.maxTagTitleLength = 100;
        this.minTagPoint = -1000;
        this.maxTagPoint = 1000;
    }

    @Override
    public void createRewardTag(RewardTagDto rewardTagDto){
        rewardTagDto = clipRewardTagDto(rewardTagDto);
        throwIfRewardTagDtoExceedRange(rewardTagDto);
        rewardTagRepository.createRewardTag(rewardTagDto);
    }

    @Override
    public void updateRewardTag(int rewardTagId, RewardTagDto rewardTagDto){
        throwIfCannotFindRewardByRewardTagId(rewardTagId);
        rewardTagDto = clipRewardTagDto(rewardTagDto);
        throwIfRewardTagDtoExceedRange(rewardTagDto);
        rewardTagRepository.updateRewardTag(rewardTagId, rewardTagDto);
    }

    @Override
    public void deleteRewardTag(int rewardTagId){
        throwIfCannotFindRewardByRewardTagId(rewardTagId);
        rewardTagRepository.deleteRewardTag(rewardTagId);
    }

    private void throwIfCannotFindRewardByRewardTagId(int rewardTagId){
        if(!rewardTagRepository.isRewardTagExist(rewardTagId)){
            throw new UnknownRewardTagException(rewardTagId);
        }
    }

    private RewardTagDto clipRewardTagDto(RewardTagDto rewardTagDto){
        return RewardTagDto.builder()
                .rewardTagTitle(tagExceedClipper.clipTagTitle(rewardTagDto.getRewardTagTitle()))
                .rewardPoint(tagExceedClipper.clipTagPoint(rewardTagDto.getRewardPoint()))
                .build();
    }

    private void throwIfRewardTagDtoExceedRange(RewardTagDto rewardTagDto){
        throwIfRewardTagDtoTitleExceedRange(rewardTagDto);
        throwIfRewardTagDtoPointExceedRange(rewardTagDto);
    }

    private void throwIfRewardTagDtoTitleExceedRange(RewardTagDto rewardTagDto){
        if(rewardTagDto.getRewardTagTitle().length() > this.maxTagTitleLength){
            throw new IllegalStateException("Reward Tag Dto's title exceed range \"" + rewardTagDto.getRewardTagTitle().length() + "\"");
        }
    }

    private void throwIfRewardTagDtoPointExceedRange(RewardTagDto rewardTagDto){
        if(rewardTagDto.getRewardPoint() < this.minTagPoint
            || rewardTagDto.getRewardPoint() > this.maxTagPoint){
            throw new IllegalStateException("Reward Tag Dto's point exceed range \"" + rewardTagDto.getRewardPoint() + "\"");
        }
    }

    @Override
    public List<RewardTagDto> readRewardTagList(){
        return rewardTagRepository.readRewardTagList();
    }

}
