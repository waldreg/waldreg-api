package org.waldreg.repository.team;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.domain.teambuilding.TeamBuilding;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryTeamBuildingStorage;
import org.waldreg.repository.MemoryTeamStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.character.CharacterMapper;
import org.waldreg.repository.character.MemoryCharacterRepository;
import org.waldreg.repository.teambuilding.MemoryTeamBuildingRepository;
import org.waldreg.repository.teambuilding.TeamBuildingMapper;
import org.waldreg.repository.user.MemoryUserRepository;
import org.waldreg.repository.user.UserMapper;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.team.spi.TeamRepository;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingRepository;
import org.waldreg.user.spi.CharacterRepository;
import org.waldreg.user.spi.UserRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemoryTeamRepository.class, MemoryTeamStorage.class, TeamMapper.class, MemoryUserRepository.class, MemoryUserStorage.class, UserMapper.class, MemoryCharacterStorage.class, MemoryCharacterRepository.class, CharacterMapper.class, MemoryTeamBuildingRepository.class, MemoryTeamBuildingStorage.class, TeamBuildingMapper.class})
public class TeamRepositoryTest{

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MemoryTeamStorage memoryTeamStorage;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MemoryUserStorage memoryUserStorage;

    @Autowired
    MemoryCharacterStorage memoryCharacterStorage;

    @Autowired
    CharacterRepository characterRepository;

    @Autowired
    TeamBuildingRepository teamBuildingRepository;

    @Autowired
    MemoryTeamBuildingStorage memoryTeamBuildingStorage;

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_USER(){memoryUserStorage.deleteAllUser();}

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_TEAM(){memoryTeamStorage.deleteAllTeam();}

    @Test
    @DisplayName("새로운 team 생성")
    public void CREATE_NEW_TEAM_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName = "team";

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        TeamDto teamDto= createTeamDto(teamBuildingDto.getTeamBuildingId(),teamName,List.of());
        TeamDto result = teamRepository.createTeam(teamDto);

        //then
        Assertions.assertAll(
                () -> Assertions.assertTrue(result.getTeamId()!=0),
                () -> Assertions.assertEquals(teamDto.getTeamBuildingId(),result.getTeamBuildingId()),
                () -> Assertions.assertEquals(teamDto.getTeamName(), result.getTeamName()),
                () -> Assertions.assertEquals(teamDto.getUserList(), result.getUserList()),
                () -> Assertions.assertNotNull(result.getLastModifiedAt())
        );

    }

    private TeamBuildingDto createTeamBuildingDto(String title){
        return TeamBuildingDto.builder()
                .teamBuildingTitle(title)
                .build();
    }

    private TeamDto createTeamDto(int teamBuildingId, String teamName, List<UserDto> userDtoList){
        return TeamDto.builder()
                .teamBuildingId(teamBuildingId)
                .teamName(teamName)
                .userDtoList(userDtoList)
                .build();
    }

    private List<UserDto> createUserDtoList(){
        List<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(createUserDtoAndUser("alcuk_id"));
        userDtoList.add(createUserDtoAndUser("alcuk_id2"));
        userDtoList.add(createUserDtoAndUser("alcuk_id3"));
        userDtoList.add(createUserDtoAndUser("alcuk_id4"));
        userDtoList.add(createUserDtoAndUser("alcuk_id5"));
        userDtoList.add(createUserDtoAndUser("alcuk_id6"));
        return userDtoList;
    }

    private UserDto createUserDtoAndUser(String userId){
        org.waldreg.user.dto.UserDto userDto = org.waldreg.user.dto.UserDto
                .builder()
                .userId(userId)
                .name("name")
                .userPassword("2dkdkdfdfdk!")
                .phoneNumber("010-1234-1234")
                .build();
        userRepository.createUser(userDto);
        return UserDto.builder()
                .userId(userId)
                .name("name")
                .build();
    }

}
