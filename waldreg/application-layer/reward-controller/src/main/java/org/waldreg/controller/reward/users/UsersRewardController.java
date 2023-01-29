package org.waldreg.controller.reward.users;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wadlreg.reward.users.UsersRewardManager;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.token.aop.annotation.Authenticating;

@RestController
public class UsersRewardController{

    private final UsersRewardManager usersRewardManager;

    @Autowired
    public UsersRewardController(UsersRewardManager usersRewardManager){
        this.usersRewardManager = usersRewardManager;
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

    private List<Integer> getUserIdList(String userId){
        String[] userIds = userId.split(",");
        List<Integer> userIdList = new ArrayList<>();
        for(String element : userIds){
            userIdList.add(Integer.parseInt(element.strip()));
        }
        return userIdList;
    }

}
