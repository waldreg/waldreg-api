package org.waldreg.controller.user;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.character.aop.behavior.VerifyingFailBehavior;
import org.waldreg.character.aop.parameter.PermissionVerifyState;
import org.waldreg.controller.user.mapper.ControllerUserMapper;
import org.waldreg.controller.user.request.CharacterRequest;
import org.waldreg.controller.user.request.UpdateUserRequest;
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
    public void createUser(@RequestBody @Validated UserRequest createRequest){
        UserDto userDto = controllerUserMapper.userRequestToUserDto(createRequest);
        userManager.createUser(controllerUserMapper.userRequestToUserDto(createRequest));
    }

    @Authenticating(fail = AuthFailBehavior.PASS)
    @PermissionVerifying(value = "Read other user info permission", fail = VerifyingFailBehavior.PASS)
    @GetMapping("/user/{user-id}")
    public UserResponse readSpecificUser(@PathVariable("user-id") String userId, @Nullable PermissionVerifyState permissionVerifyState){
        UserDto userDto = userManager.readUserByUserId(userId);
        if (permissionVerifyState.isVerified()){
            return controllerUserMapper.userDtoToUserResponseWithPermission(userDto);
        }
        return controllerUserMapper.userDtoToUserResponseWithoutPermission(userDto);
    }

    @Authenticating(fail = AuthFailBehavior.PASS)
    @PermissionVerifying(value = "Read other user info permission", fail = VerifyingFailBehavior.PASS)
    @RequestMapping(value = "/users")
    public UserListResponse readAllUser(@RequestParam("from") int startIdx, @RequestParam("to") int endIdx, @Nullable PermissionVerifyState permissionVerifyState){
        int maxIdx = userManager.readMaxIdx();
        List<UserDto> userDtoList = userManager.readUserList(startIdx, endIdx);
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
    @PutMapping("/user")
    public void updateUser(@RequestBody @Validated UpdateUserRequest updateUserRequest){
        int id = decryptedTokenContextGetter.get();
        UserDto update = controllerUserMapper.updateUserRequestToUserDto(updateUserRequest);
        userManager.updateUser(id, update);
    }

    @Authenticating
    @PermissionVerifying(value = "Fire other user permission")
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

    @Authenticating
    @PermissionVerifying("Update other user's character permission")
    @PutMapping("/user/character/{id}")
    public void updateUserCharacter(@PathVariable("id") int id, @RequestBody @Validated CharacterRequest characterRequest){
        String character = characterRequest.getCharacter();
        userManager.updateCharacter(id, character);
    }

}
