package org.waldreg.controller.reward.users;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wadlreg.reward.users.UsersRewardManager;
import org.wadlreg.reward.users.dto.UsersRewardDto;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.controller.reward.users.mapper.ControllerUsersRewardMapper;
import org.waldreg.controller.reward.users.response.RewardTagWrapperResponse;
import org.waldreg.controller.reward.users.response.UsersRewardTagResponse;
import org.waldreg.token.aop.annotation.Authenticating;

@RestController
public class UsersRewardController{

    private final UsersRewardManager usersRewardManager;
    private final ControllerUsersRewardMapper controllerUsersRewardMapper;

    @Autowired
    public UsersRewardController(UsersRewardManager usersRewardManager,
            ControllerUsersRewardMapper controllerUsersRewardMapper){
        this.usersRewardManager = usersRewardManager;
        this.controllerUsersRewardMapper = controllerUsersRewardMapper;
    }

    @Authenticating
    @PermissionVerifying("Reward manager")
    @GetMapping("/reward-tag/users")
    public void givenRewardToUsers(@RequestParam("id") String userId, @RequestParam("reward-tag-id") int rewardTagId){
        List<Integer> userIdList = getUserIdList(userId);
        for(Integer id : userIdList){
            usersRewardManager.assignRewardToUser(id, rewardTagId);
        }
    }

    @Authenticating
    @PermissionVerifying("Reward manager")
    @GetMapping("/reward-tag/reset")
    public void resetAllUsersReward(){
        usersRewardManager.resetAllUsersReward();
    }

    @Authenticating
    @PermissionVerifying("Reward manager")
    @GetMapping("/reward-tag/user/{id}")
    public UsersRewardTagResponse readSpecifyUsersReward(@PathVariable("id") int id){
        UsersRewardDto usersRewardDto = usersRewardManager.readSpecifyUsersReward(id);
        return controllerUsersRewardMapper.usersRewardDtoToUsersRewardTagResponse(usersRewardDto);
    }

    private List<Integer> getUserIdList(String userId){
        String[] userIds = userId.split(",");
        List<Integer> userIdList = new ArrayList<>();
        for(String element : userIds){
            userIdList.add(Integer.parseInt(element.strip()));
        }
        return userIdList;
    }

}
