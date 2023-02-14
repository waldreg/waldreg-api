package org.waldreg.controller.joiningpool;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.controller.joiningpool.mapper.ControllerJoiningPoolMapper;
import org.waldreg.controller.joiningpool.response.UserJoiningPoolResponse;
import org.waldreg.controller.joiningpool.request.UserRequest;
import org.waldreg.controller.joiningpool.response.UserJoiningPoolListResponse;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.management.PerPage;
import org.waldreg.user.management.joiningpool.JoiningPoolManager;
import org.waldreg.util.token.DecryptedTokenContextGetter;

@RestController
public class JoiningPoolController{

    private final JoiningPoolManager joiningPoolManager;
    private final ControllerJoiningPoolMapper controllerJoiningPoolMapper;
    private final DecryptedTokenContextGetter decryptedTokenContextGetter;


    @Autowired
    public JoiningPoolController(JoiningPoolManager joiningPoolManager, ControllerJoiningPoolMapper controllerJoiningPoolMapper, DecryptedTokenContextGetter decryptedTokenContextGetter){
        this.joiningPoolManager = joiningPoolManager;
        this.controllerJoiningPoolMapper = controllerJoiningPoolMapper;
        this.decryptedTokenContextGetter = decryptedTokenContextGetter;
    }

    @PostMapping("/user")
    public void createUser(@RequestBody @Validated UserRequest createRequest){
        UserDto userDto = controllerJoiningPoolMapper.userRequestToUserDto(createRequest);
        joiningPoolManager.createUser(controllerJoiningPoolMapper.userRequestToUserDto(createRequest));
    }

    @Authenticating
    @PermissionVerifying("User join manager")
    @GetMapping("/user/joiningpool")
    public UserJoiningPoolListResponse readJoiningPoolUserList(@RequestParam(value = "from", required = false) Integer from, @RequestParam(value = "to", required = false) Integer to){
        if (isInvalidRange(from, to)){
            from = 1;
            to = PerPage.PER_PAGE;
        }
        int maxIdx = joiningPoolManager.readJoiningPoolMaxIdx();
        List<UserDto> userDtoList = joiningPoolManager.readUserJoiningPool(from, to);
        List<UserJoiningPoolResponse> userJoiningPoolResponseList = controllerJoiningPoolMapper.userDtoListToUserJoiningPoolListResponse(userDtoList);
        return controllerJoiningPoolMapper.createUserJoiningPoolListResponse(maxIdx, userJoiningPoolResponseList);
    }

    private boolean isInvalidRange(Integer from, Integer to){
        return from == null || to == null;
    }

    @Authenticating
    @PermissionVerifying("User join manager")
    @GetMapping("/user/joiningpool/{user-id}")
    public void approveUserJoin(@PathVariable("user-id") String userId){
        joiningPoolManager.approveJoin(userId);
    }

    @Authenticating
    @PermissionVerifying("User join manager")
    @DeleteMapping("/user/joiningpool/{user-id}")
    public void rejectUserJoin(@PathVariable("user-id") String userId){
        joiningPoolManager.rejectJoin(userId);
    }

}
