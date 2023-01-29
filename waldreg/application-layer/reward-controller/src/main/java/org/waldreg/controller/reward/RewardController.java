package org.waldreg.controller.reward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.wadlreg.reward.tag.RewardTagManager;
import org.wadlreg.reward.users.UsersRewardManager;
import org.waldreg.controller.reward.mapper.ControllerRewardTagMapper;
import org.waldreg.controller.reward.request.RewardTagRequest;

@RestController
public class RewardController{

    private final RewardTagManager rewardTagManager;
    private final UsersRewardManager usersRewardManager;
    private final ControllerRewardTagMapper controllerRewardTagMapper;

    @Autowired
    public RewardController(RewardTagManager rewardTagManager,
            UsersRewardManager usersRewardManager,
            ControllerRewardTagMapper controllerRewardTagMapper){
        this.rewardTagManager = rewardTagManager;
        this.usersRewardManager = usersRewardManager;
        this.controllerRewardTagMapper = controllerRewardTagMapper;
    }

    @PostMapping("/reward-tag")
    public void createRewardTag(@RequestBody @Validated RewardTagRequest rewardTagRequest){
        rewardTagManager.createRewardTag(
                controllerRewardTagMapper.rewardTagRequestToRewardTagDto(rewardTagRequest)
        );
    }

}
