package org.waldreg.controller.reward.users;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stage.xss.core.meta.Xss;
import org.stage.xss.core.meta.XssFiltering;
import org.waldreg.character.aop.behavior.VerifyingFailBehavior;
import org.waldreg.character.aop.parameter.PermissionVerifyState;
import org.waldreg.character.exception.NoPermissionException;
import org.waldreg.reward.users.UsersRewardManager;
import org.waldreg.reward.users.dto.UsersRewardDto;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.controller.reward.users.mapper.ControllerUsersRewardMapper;
import org.waldreg.controller.reward.users.response.UsersRewardTagResponse;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.token.aop.annotation.IdAuthenticating;
import org.waldreg.token.aop.behavior.AuthFailBehavior;
import org.waldreg.token.aop.parameter.AuthenticateVerifyState;

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

    @XssFiltering
    @Authenticating
    @PermissionVerifying("Reward manager")
    @GetMapping("/reward-tag/users")
    public void givenRewardToUsers(@Xss("string") @RequestParam("id") String userId, @RequestParam("reward-tag-id") int rewardTagId){
        List<Integer> userIdList = getUserIdList(userId);
        for (Integer id : userIdList){
            usersRewardManager.assignRewardToUser(id, rewardTagId);
        }
    }

    private List<Integer> getUserIdList(String userId){
        String[] userIds = userId.split(",");
        List<Integer> userIdList = new ArrayList<>();
        for (String element : userIds){
            userIdList.add(Integer.parseInt(element.strip()));
        }
        return userIdList;
    }

    @Authenticating
    @PermissionVerifying("Reward manager")
    @GetMapping("/reward-tag/users/reset")
    public void resetAllUsersReward(){
        usersRewardManager.resetAllUsersReward();
    }

    @IdAuthenticating(fail = AuthFailBehavior.PASS)
    @PermissionVerifying(value = "Reward manager", fail = VerifyingFailBehavior.PASS)
    @GetMapping("/reward-tag/user/{id}")
    public UsersRewardTagResponse readSpecifyUsersReward(@PathVariable("id") int id,
                                                         PermissionVerifyState permissionVerifyState,
                                                         AuthenticateVerifyState authenticateVerifyState){
        throwIfValidationFail(permissionVerifyState, authenticateVerifyState);
        UsersRewardDto usersRewardDto = usersRewardManager.readSpecifyUsersReward(id);
        return controllerUsersRewardMapper.usersRewardDtoToUsersRewardTagResponse(usersRewardDto);
    }

    private void throwIfValidationFail(PermissionVerifyState permissionVerifyState, AuthenticateVerifyState authenticateVerifyState){
        if (!permissionVerifyState.isVerified() && !authenticateVerifyState.isVerified()){
            throw new NoPermissionException();
        }
    }

    @Authenticating
    @PermissionVerifying("Reward manager")
    @DeleteMapping("/reward-tag/user")
    public void deleteSpecifyUsersReward(@RequestParam("id") int id, @RequestParam("reward-id") int rewardId){
        usersRewardManager.deleteRewardToUser(id, rewardId);
    }

}
