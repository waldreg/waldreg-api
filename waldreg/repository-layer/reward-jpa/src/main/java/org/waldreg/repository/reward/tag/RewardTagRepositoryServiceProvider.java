package org.waldreg.repository.reward.tag;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.repository.reward.repository.JpaRewardTagRepository;
import org.waldreg.repository.reward.repository.JpaRewardTagWrapperRepository;
import org.waldreg.repository.reward.tag.mapper.RewardTagMapper;
import org.waldreg.reward.tag.dto.RewardTagDto;
import org.waldreg.reward.tag.spi.RewardTagRepository;

@Repository
public class RewardTagRepositoryServiceProvider implements RewardTagRepository{

    private final JpaRewardTagRepository jpaRewardTagRepository;
    private final JpaRewardTagWrapperRepository jpaRewardTagWrapperRepository;
    private final RewardTagMapper rewardTagMapper;

    @Autowired
    RewardTagRepositoryServiceProvider(JpaRewardTagRepository jpaRewardTagRepository,
                                        JpaRewardTagWrapperRepository jpaRewardTagWrapperRepository,
                                        RewardTagMapper rewardTagMapper){
        this.jpaRewardTagRepository = jpaRewardTagRepository;
        this.jpaRewardTagWrapperRepository = jpaRewardTagWrapperRepository;
        this.rewardTagMapper = rewardTagMapper;
    }

    @Override
    @Transactional
    public void createRewardTag(RewardTagDto rewardTagDto){
        RewardTag rewardTag = rewardTagMapper.rewardTagDtoToRewardTag(rewardTagDto);
        jpaRewardTagRepository.save(rewardTag);
    }

    @Override
    @Transactional
    public void updateRewardTag(int rewardTagId, RewardTagDto rewardTagDto){
        RewardTag rewardTag = rewardTagMapper.rewardTagDtoToRewardTag(rewardTagDto);
        RewardTag updateTarget = jpaRewardTagRepository.findById(rewardTagId)
                .orElseThrow(() -> {throw new IllegalStateException("Cannot find RewardTag id \"" + rewardTagId + "\"");});
        updateTarget.updateRewardTag(rewardTag);
    }

    @Override
    @Transactional
    public void deleteRewardTag(int rewardTagId){
        jpaRewardTagWrapperRepository.deleteByRewardTagId(rewardTagId);
        jpaRewardTagRepository.deleteById(rewardTagId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RewardTagDto> readRewardTagList(){
        List<RewardTag> rewardTagList = jpaRewardTagRepository.findAll();
        return rewardTagList.stream()
                .map(rewardTagMapper::rewardTagToRewardTagDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRewardTagExist(int rewardTagId){
        return jpaRewardTagRepository.existsById(rewardTagId);
    }

}
