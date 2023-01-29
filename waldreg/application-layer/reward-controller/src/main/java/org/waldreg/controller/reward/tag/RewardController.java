package org.waldreg.controller.reward.tag;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.wadlreg.reward.tag.RewardTagManager;
import org.wadlreg.reward.tag.dto.RewardTagDto;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.controller.reward.tag.mapper.ControllerRewardTagMapper;
import org.waldreg.controller.reward.tag.request.RewardTagRequest;
import org.waldreg.controller.reward.tag.response.RewardTagResponse;
import org.waldreg.token.aop.annotation.Authenticating;

@RestController
public class RewardController{

    private final RewardTagManager rewardTagManager;
    private final ControllerRewardTagMapper controllerRewardTagMapper;

    @Autowired
    public RewardController(RewardTagManager rewardTagManager,
            ControllerRewardTagMapper controllerRewardTagMapper){
        this.rewardTagManager = rewardTagManager;
        this.controllerRewardTagMapper = controllerRewardTagMapper;
    }

    @Authenticating
    @PermissionVerifying("Reward manager")
    @PostMapping("/reward-tag")
    public void createRewardTag(@RequestBody @Validated RewardTagRequest rewardTagRequest){
        rewardTagManager.createRewardTag(
                controllerRewardTagMapper.rewardTagRequestToRewardTagDto(rewardTagRequest)
        );
    }

    @Authenticating
    @PermissionVerifying("Reward manager")
    @GetMapping("/reward-tag")
    public Map<String, List<RewardTagResponse>> readRewardTagMap(){
        List<RewardTagDto> rewardTagDtoList = rewardTagManager.readRewardTagList();
        return Map.of("reward_tags", controllerRewardTagMapper.rewardTagDtoListToRewardTagResponseList(rewardTagDtoList));
    }

    @Authenticating
    @PermissionVerifying("Reward manager")
    @PutMapping("/reward-tag/{reward-tag-id}")
    public void updateRewardTag(@PathVariable("reward-tag-id") int rewardTagId,
            @RequestBody @Validated RewardTagRequest rewardTagRequest){
        rewardTagManager.updateRewardTag(
                rewardTagId, controllerRewardTagMapper.rewardTagRequestToRewardTagDto(rewardTagRequest)
        );
    }

    @Authenticating
    @PermissionVerifying("Reward manager")
    @DeleteMapping("/reward-tag/{reward-tag-id}")
    public void deleteRewardTag(@PathVariable("reward-tag-id") int rewardTagId){
        rewardTagManager.deleteRewardTag(rewardTagId);
    }

}
