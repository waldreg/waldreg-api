package org.waldreg.controller.user;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stage.xss.core.meta.Xss;
import org.stage.xss.core.meta.XssFiltering;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.character.aop.behavior.VerifyingFailBehavior;
import org.waldreg.character.aop.parameter.PermissionVerifyState;
import org.waldreg.controller.user.mapper.ControllerUserMapper;
import org.waldreg.controller.user.request.CharacterRequest;
import org.waldreg.controller.user.request.UpdateUserRequest;
import org.waldreg.controller.user.response.UserListResponse;
import org.waldreg.controller.user.response.UserResponse;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.token.aop.annotation.HeaderPasswordAuthenticating;
import org.waldreg.token.aop.behavior.AuthFailBehavior;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.management.PerPage;
import org.waldreg.user.management.user.UserManager;
import org.waldreg.util.token.DecryptedTokenContextGetter;

@RestController
public class UserController{

    private final UserManager userManager;
    private final ControllerUserMapper controllerUserMapper;
    private final DecryptedTokenContextGetter decryptedTokenContextGetter;

    @Autowired
    public UserController(UserManager userManager, DecryptedTokenContextGetter decryptedTokenContextGetter, ControllerUserMapper controllerUserMapper){
        this.userManager = userManager;
        this.controllerUserMapper = controllerUserMapper;
        this.decryptedTokenContextGetter = decryptedTokenContextGetter;
    }

    @Authenticating(fail = AuthFailBehavior.PASS)
    @PermissionVerifying(value = "User info read manager", fail = VerifyingFailBehavior.PASS)
    @GetMapping("/user/{user-id}")
    public UserResponse readSpecificUser(@PathVariable("user-id") String userId, @Nullable PermissionVerifyState permissionVerifyState){
        UserDto userDto = userManager.readUserByUserId(userId);
        if (permissionVerifyState.isVerified()){
            return controllerUserMapper.userDtoToUserResponseWithPermission(userDto);
        }
        return controllerUserMapper.userDtoToUserResponseWithoutPermission(userDto);
    }

    @Authenticating(fail = AuthFailBehavior.PASS)
    @PermissionVerifying(value = "User info read manager", fail = VerifyingFailBehavior.PASS)
    @RequestMapping(value = "/users")
    public UserListResponse readAllUser(@RequestParam(value = "from", required = false) Integer startIdx, @RequestParam(value = "to", required = false) Integer endIdx, @Nullable PermissionVerifyState permissionVerifyState){
        if (isInvalidRange(startIdx, endIdx)){
            startIdx = 1;
            endIdx = PerPage.PER_PAGE;
        }
        int maxIdx = userManager.readMaxIdx();
        List<UserDto> userDtoList = userManager.readUserList(startIdx, endIdx);
        if (permissionVerifyState.isVerified()){
            List<UserResponse> userResponseList = controllerUserMapper.userDtoListToUserListResponseWithPermission(userDtoList);
            return controllerUserMapper.createUserListResponse(maxIdx, userResponseList);
        }
        List<UserResponse> userResponseList = controllerUserMapper.userDtoListToUserListResponseWithoutPermission(userDtoList);
        return controllerUserMapper.createUserListResponse(maxIdx, userResponseList);
    }

    private boolean isInvalidRange(Integer from, Integer to){
        return from == null || to == null;
    }

    @Authenticating
    @GetMapping("/user")
    public UserResponse readUserOnline(){
        int id = decryptedTokenContextGetter.get();
        UserDto userDto = userManager.readUserById(id);
        return controllerUserMapper.userDtoToUserResponseWithPermission(userDto);
    }

    @XssFiltering
    @HeaderPasswordAuthenticating
    @PutMapping("/user")
    public void updateUser(@RequestBody @Validated @Xss("json") UpdateUserRequest updateUserRequest){
        int id = decryptedTokenContextGetter.get();
        UserDto update = controllerUserMapper.updateUserRequestToUserDto(updateUserRequest);
        userManager.updateUser(id, update);
    }

    @Authenticating
    @PermissionVerifying(value = "User fire manager")
    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable("id") int id){
        userManager.deleteById(id);
    }

    @HeaderPasswordAuthenticating
    @DeleteMapping("/user")
    public void deleteUserOwn(){
        int id = decryptedTokenContextGetter.get();
        userManager.deleteById(id);
    }

    @XssFiltering
    @Authenticating
    @PermissionVerifying("User's character update manager")
    @PutMapping("/user/character/{id}")
    public void updateUserCharacter(@PathVariable("id") int id, @RequestBody @Validated @Xss("json") CharacterRequest characterRequest){
        String character = characterRequest.getCharacter();
        userManager.updateCharacter(id, character);
    }

}
