package org.waldreg.repository.user;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.domain.attendance.Attendance;
import org.waldreg.domain.attendance.AttendanceUser;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.user.mapper.UserRepositoryMapper;
import org.waldreg.repository.user.repository.JpaAttendanceRepository;
import org.waldreg.repository.user.repository.JpaAttendanceUserRepository;
import org.waldreg.repository.user.repository.JpaCharacterRepository;
import org.waldreg.repository.user.repository.JpaUserRepository;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.spi.UserRepository;

@Repository
public class UserRepositoryServiceProvider implements UserRepository{

    private final JpaUserRepository jpaUserRepository;
    private final JpaCharacterRepository jpaCharacterRepository;
    private final JpaAttendanceRepository jpaAttendanceRepository;
    private final JpaAttendanceUserRepository jpaAttendanceUserRepository;

    private final UserRepositoryCommander userRepositoryCommander;
    private final UserRepositoryMapper userRepositoryMapper;

    @Autowired
    public UserRepositoryServiceProvider(JpaUserRepository jpaUserRepository,
            JpaCharacterRepository jpaCharacterRepository,
            UserRepositoryMapper userRepositoryMapper,
            JpaAttendanceRepository jpaAttendanceRepository,
            JpaAttendanceUserRepository jpaAttendanceUserRepository,
            UserRepositoryCommander userRepositoryCommander){
        this.jpaUserRepository = jpaUserRepository;
        this.jpaCharacterRepository = jpaCharacterRepository;
        this.jpaAttendanceRepository = jpaAttendanceRepository;
        this.jpaAttendanceUserRepository = jpaAttendanceUserRepository;
        this.userRepositoryCommander = userRepositoryCommander;
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
        User user = userRepositoryCommander.readUserById(id);
        return userRepositoryMapper.userToUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto readUserByUserId(String userId){
        User user = userRepositoryCommander.readUserByUserId(userId);
        return userRepositoryMapper.userToUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> readUserList(int startIdx, int endIdx){
        List<User> userList = userRepositoryCommander.readUserList(startIdx, endIdx);
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
        User user = jpaUserRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user with id \"" + id + "\"");}
        );
        deleteAttendanceUser(user);
        jpaUserRepository.deleteById(id);
    }

    private void deleteAttendanceUser(User user){
        if(!jpaAttendanceUserRepository.existsByUser(user)){
            return;
        }
        AttendanceUser attendanceUser = jpaAttendanceUserRepository.findByUser(user);
        List<Attendance> attendanceList = jpaAttendanceRepository.findByUserId(user.getId());
        if(!attendanceList.isEmpty()){
            jpaAttendanceRepository.deleteByAttendanceUserId(attendanceList.get(0).getAttendanceUser().getAttendanceUserId());
        }
        jpaAttendanceUserRepository.deleteById(attendanceUser.getAttendanceUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistUserId(String userId){
        return jpaUserRepository.existsByUserInfoUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistId(int id){
        return jpaUserRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> readSpecificUserList(List<Integer> idList){
        List<UserDto> userDtoList = new ArrayList<>();
        idList.forEach(id -> {
            userDtoList.add(userRepositoryMapper.userToUserDto(userRepositoryCommander.readUserById(id)));
        });
        return userDtoList;
    }

}
