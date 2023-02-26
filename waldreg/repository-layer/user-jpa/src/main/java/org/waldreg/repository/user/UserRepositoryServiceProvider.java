package org.waldreg.repository.user;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.user.mapper.UserRepositoryMapper;
import org.waldreg.repository.user.repository.JpaCharacterRepository;
import org.waldreg.repository.user.repository.JpaUserRepository;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.spi.UserRepository;

@Repository
public class UserRepositoryServiceProvider implements UserRepository{

    private final JpaUserRepository jpaUserRepository;
    private final JpaCharacterRepository jpaCharacterRepository;
    private final UserRepositoryMapper userRepositoryMapper;

    @Autowired
    public UserRepositoryServiceProvider(JpaUserRepository jpaUserRepository, JpaCharacterRepository jpaCharacterRepository, UserRepositoryMapper userRepositoryMapper){
        this.jpaUserRepository = jpaUserRepository;
        this.jpaCharacterRepository = jpaCharacterRepository;
        this.userRepositoryMapper = userRepositoryMapper;
    }

    @Override
    @Transactional
    public void createUser(UserDto userDto){
        Character character = jpaCharacterRepository.getCharacterByCharacterName(userDto.getCharacter()).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find character with name \"" + userDto.getCharacter() + "\"");}
        );
        User user = userRepositoryMapper.userDtoToUser(userDto);
        user.setCharacter(character);
        jpaUserRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto readUserById(int id){
        User user = jpaUserRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user with id \"" + id + "\"");}
        );
        return userRepositoryMapper.userToUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto readUserByUserId(String userId){
        User user = jpaUserRepository.findByUserId(userId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user with user_id \"" + userId + "\"");}
        );
        return userRepositoryMapper.userToUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> readUserList(int startIdx, int endIdx){
        int count = endIdx - startIdx + 1;
        List<User> userList = jpaUserRepository.findAll(startIdx - 1, count);
        return userRepositoryMapper.userListToUserDtoList(userList);
    }

    @Override
    @Transactional
    public void updateUser(int id, UserDto userDto){
        User user = jpaUserRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user with id \"" + id + "\"");}
        );
        user.setName(userDto.getName());
        user.setUserPassword(userDto.getUserPassword());
        user.setPhoneNumber(userDto.getPhoneNumber());
    }

    @Override
    @Transactional
    public void updateCharacter(int id, String characterName){
        User user = jpaUserRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user with id \"" + id + "\"");}
        );
        Character character = jpaCharacterRepository.getCharacterByCharacterName(characterName).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find character with name \"" + characterName + "\"");}
        );
        user.setCharacter(character);
    }

    @Override
    @Transactional(readOnly = true)
    public int readMaxIdx(){
        return (int) jpaUserRepository.count();
    }

    @Override
    @Transactional
    public void deleteById(int id){
        jpaUserRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user with id \"" + id + "\"");}
        );
        jpaUserRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistUserId(String userId){
        return jpaUserRepository.existsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistId(int id){
        return jpaUserRepository.existsById(id);
    }

}
