package org.waldreg.repository.teambuilding;

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
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryTeamBuildingStorage;
import org.waldreg.repository.MemoryTeamStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.character.CharacterMapper;
import org.waldreg.repository.character.MemoryCharacterRepository;
import org.waldreg.repository.user.MemoryUserRepository;
import org.waldreg.repository.user.UserMapper;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.spi.TeamBuildingRepository;
import org.waldreg.user.spi.CharacterRepository;
import org.waldreg.user.spi.UserRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemoryTeamBuildingRepository.class, MemoryTeamBuildingStorage.class, TeamBuildingMapper.class, MemoryUserRepository.class, MemoryUserStorage.class, UserMapper.class, MemoryCharacterStorage.class, MemoryCharacterRepository.class, CharacterMapper.class})
public class TeamBuildingRepositoryTest{

    @Autowired
    TeamBuildingRepository teamBuildingRepository;

    @Autowired
    MemoryTeamBuildingStorage memoryTeamBuildingStorage;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MemoryUserStorage memoryUserStorage;

    @Autowired
    MemoryCharacterStorage memoryCharacterStorage;

    @Autowired
    CharacterRepository characterRepository;


    @BeforeEach
    @AfterEach
    private void DELETE_ALL_USER(){memoryUserStorage.deleteAllUser();}

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_TEAM_BUILDING(){memoryTeamBuildingStorage.deleteAllTeamBuilding();}


    @Test
    @DisplayName("새로운 팀빌딩 그룹 생성")
    public void CREATE_NEW_TEAM_BUILDING_SUCCESS_TEST(){
        //given
        String title = "hirhirhir";

        //when
        TeamBuildingDto result = teamBuildingRepository.createTeamBuilding(title);

        //then
       Assertions.assertAll(
               () -> Assertions.assertTrue(result.getTeamBuildingId() != 0),
               () -> Assertions.assertEquals(title, result.getTeamBuildingTitle()),
               () -> Assertions.assertNull(result.getTeamList()),
               () -> Assertions.assertNotNull(result.getCreatedAt()),
               () -> Assertions.assertNotNull(result.getLastModifiedAt())
       );

    }

    @Test
    @DisplayName("새로 생성된 팀빌딩 그룹에 팀 목록 저장")
    public void ADD_TEAM_LIST_IN_NEW_TEAM_BUILDING_SUCCESS_TEST(){
        //given
        String title = "hirhrirhr";

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingDto.getTeamBuildingId());
        TeamBuildingDto teamBuildingDto2 = createTeamBuildingDto(title,teamDtoList);

        //then
        Assertions.assertDoesNotThrow(() -> teamBuildingRepository.updateTeamListInTeamBuilding(teamBuildingDto2));

    }

    private TeamBuildingDto createTeamBuildingDto(String title, List<TeamDto> teamDtoList){
        return TeamBuildingDto.builder()
                .teamBuildingTitle(title)
                .teamDtoList(teamDtoList)
                .build();
    }

    private List<TeamDto> createTeamDtoList(int teambuildingId){
        List<TeamDto> teamDtoList = new ArrayList<>();
        List<UserDto> userDtoList = createUserDtoList();
        teamDtoList.add(createTeamDto(teambuildingId, 1,"team 1", List.of(userDtoList.get(0), userDtoList.get(2))));
        teamDtoList.add(createTeamDto(teambuildingId,2, "team 2", List.of(userDtoList.get(3), userDtoList.get(4))));
        teamDtoList.add(createTeamDto(teambuildingId,3, "team 3", List.of(userDtoList.get(1), userDtoList.get(5))));
        return teamDtoList;
    }

    private TeamDto createTeamDto(int teamBuildingId, int teamId, String teamName, List<org.waldreg.teambuilding.dto.UserDto> userDtoList){
        return TeamDto.builder()
                .teamBuildingId(teamBuildingId)
                .teamId(teamId)
                .lastModifiedAt(LocalDateTime.now())
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
