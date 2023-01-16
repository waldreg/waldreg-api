package org.waldreg.controller.user;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.character.aop.behavior.VerifyingFailBehavior;
import org.waldreg.character.aop.parameter.PermissionVerifyState;
import org.waldreg.controller.user.mapper.ControllerUserMapper;
import org.waldreg.controller.user.request.UserRequest;
import org.waldreg.controller.user.response.UserListResponse;
import org.waldreg.controller.user.response.UserResponse;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.token.aop.annotation.HeaderPasswordAuthenticating;
import org.waldreg.token.aop.behavior.AuthFailBehavior;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.management.UserManager;
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

    @PostMapping("/user")
    public void createUser(@RequestBody UserRequest createRequest){
        userManager.createUser(controllerUserMapper.userRequestToUserDto(createRequest));
    }

    @Authenticating(fail = AuthFailBehavior.PASS)
    @PermissionVerifying(value = "Read other user info permission", fail = VerifyingFailBehavior.PASS)
    @GetMapping("/user/{user-id}")
    public UserResponse readSpecificUser(@PathVariable("user-id") String userId, PermissionVerifyState permissionVerifyState){
        UserDto userDto = userManager.readUserByUserId(userId);
        if (permissionVerifyState.isVerified()){
            return controllerUserMapper.userDtoToUserResponseWithPermission(userDto);
        }
        return controllerUserMapper.userDtoToUserResponseWithoutPermission(userDto);
    }

    @Authenticating(fail = AuthFailBehavior.PASS)
    @PermissionVerifying(value = "Read other user info permission", fail = VerifyingFailBehavior.PASS)
    @GetMapping("/user?from={start-id}&to={end-id}")
    public UserListResponse readAllUser(@PathVariable("start-id") int startId, @PathVariable("end-id") int endId, PermissionVerifyState permissionVerifyState){
        int maxIdx = userManager.readMaxIdx();
        List<UserDto> userDtoList = userManager.readUserList(startId, endId);
        if (permissionVerifyState.isVerified()){
            List<UserResponse> userResponseList = controllerUserMapper.userDtoListToUserListResponseWithPermission(userDtoList);
            return controllerUserMapper.createUserListResponse(maxIdx, userResponseList);
        }
        List<UserResponse> userResponseList = controllerUserMapper.userDtoListToUserListResponseWithoutPermission(userDtoList);
        return controllerUserMapper.createUserListResponse(maxIdx, userResponseList);
    }

    @Authenticating
    @GetMapping("/user")
    public UserResponse readUserOnline(){
        int id = decryptedTokenContextGetter.get();
        UserDto userDto = userManager.readUserById(id);
        return controllerUserMapper.userDtoToUserResponseWithPermission(userDto);
    }

    @HeaderPasswordAuthenticating
    @PatchMapping("/user")
    public void updateUser(UserRequest userRequest){
        int id = decryptedTokenContextGetter.get();
        UserDto update = controllerUserMapper.userRequestToUserDto(userRequest);
        userManager.updateUser(id, update);
    }

    @Authenticating
    @PermissionVerifying(value = "Fire other user permission", fail = VerifyingFailBehavior.PASS)
    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable("id") int id, PermissionVerifyState permissionVerifyState){
        if (permissionVerifyState.isVerified()){
            userManager.deleteById(id);
        }
    }

    @HeaderPasswordAuthenticating
    @DeleteMapping("/user")
    public void deleteUserOwn(){
        int id = decryptedTokenContextGetter.get();
        userManager.deleteById(id);
    }

    @Authenticating
    @PermissionVerifying(value = "Update other user's character permission", fail = VerifyingFailBehavior.PASS)
    @PutMapping("/user/character/{id}")
    public void updateUserCharacter(@PathVariable("id") int id, String character, PermissionVerifyState permissionVerifyState){
        if (permissionVerifyState.isVerified()){
            userManager.updateCharacter(id, character);
        }
    }

}
