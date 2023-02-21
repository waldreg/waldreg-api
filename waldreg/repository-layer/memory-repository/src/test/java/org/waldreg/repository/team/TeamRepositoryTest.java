package org.waldreg.repository.team;

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
import org.waldreg.character.spi.CharacterRepository;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryTeamBuildingStorage;
import org.waldreg.repository.MemoryTeamStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.character.CharacterMapper;
import org.waldreg.repository.character.MemoryCharacterRepository;
import org.waldreg.repository.teambuilding.MemoryTeamBuildingRepository;
import org.waldreg.repository.teambuilding.TeamBuildingMapper;
import org.waldreg.repository.teambuildinguserinfo.UserInfoMapper;
import org.waldreg.repository.user.MemoryUserRepository;
import org.waldreg.repository.user.UserMapper;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.team.spi.TeamRepository;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingRepository;
import org.waldreg.user.spi.UserRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemoryTeamRepository.class, MemoryTeamStorage.class, TeamMapper.class, MemoryUserRepository.class, MemoryUserStorage.class, UserMapper.class, MemoryCharacterStorage.class, MemoryCharacterRepository.class, CharacterMapper.class, MemoryTeamBuildingRepository.class, MemoryTeamBuildingStorage.class, TeamBuildingMapper.class, UserInfoMapper.class})
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

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_TEAM_BUILDING(){memoryTeamBuildingStorage.deleteAllTeamBuilding();}

    @Test
    @DisplayName("새로운 team 생성")
    public void CREATE_NEW_TEAM_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName = "team";

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        TeamDto teamDto = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName, List.of());
        TeamDto result = teamRepository.createTeam(teamDto);

        //then
        Assertions.assertAll(
                () -> Assertions.assertTrue(result.getTeamId() != 0),
                () -> Assertions.assertEquals(teamDto.getTeamBuildingId(), result.getTeamBuildingId()),
                () -> Assertions.assertEquals(teamDto.getTeamName(), result.getTeamName()),
                () -> Assertions.assertEquals(teamDto.getUserList(), result.getUserList()),
                () -> Assertions.assertNotNull(result.getLastModifiedAt())
        );

    }

    @Test
    @DisplayName("team_id로 조회")
    public void READ_TEAM_BY_TEAM_ID_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName = "team";

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        TeamDto teamDto = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName, List.of());
        TeamDto teamDto2 = teamRepository.createTeam(teamDto);
        TeamDto result = teamRepository.readTeamById(teamDto2.getTeamId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(teamDto2.getTeamId(), result.getTeamId()),
                () -> Assertions.assertEquals(teamDto2.getTeamBuildingId(), result.getTeamBuildingId()),
                () -> Assertions.assertEquals(teamDto2.getTeamName(), result.getTeamName()),
                () -> Assertions.assertEquals(teamDto2.getUserList(), result.getUserList()),
                () -> Assertions.assertEquals(teamDto2.getLastModifiedAt(), result.getLastModifiedAt())
        );

    }

    @Test
    @DisplayName("team_id로 team 수정")
    public void UPDATE_TEAM_BY_TEAM_ID_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName = "team";

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        TeamDto teamDto = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName, List.of());
        TeamDto teamDto2 = teamRepository.createTeam(teamDto);
        TeamDto modifiedteamDto = createTeamDto(teamBuildingDto.getTeamBuildingId(), "changing~", createUserDtoList());
        modifiedteamDto.setTeamId(teamDto2.getTeamId());
        teamRepository.updateTeamById(teamDto2.getTeamId(), modifiedteamDto);
        TeamDto result = teamRepository.readTeamById(teamDto2.getTeamId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(teamDto2.getTeamId(), result.getTeamId()),
                () -> Assertions.assertEquals(modifiedteamDto.getTeamBuildingId(), result.getTeamBuildingId()),
                () -> Assertions.assertEquals(modifiedteamDto.getTeamName(), result.getTeamName()),
                () -> Assertions.assertEquals(modifiedteamDto.getUserList().size(), result.getUserList().size()),
                () -> Assertions.assertEquals(modifiedteamDto.getLastModifiedAt(), result.getLastModifiedAt())
        );

    }

    @Test
    @DisplayName("team_id로 삭제")
    public void DELETE_TEAM_BY_TEAM_ID_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName = "team";

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        TeamDto teamDto = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName, List.of());
        TeamDto teamDto2 = teamRepository.createTeam(teamDto);
        teamRepository.deleteTeamById(teamDto2.getTeamId());

        //then
        Assertions.assertFalse(() -> teamRepository.isExistTeam(teamDto2.getTeamId()));

    }

    @Test
    @DisplayName("teambuilding_id로 전체 조회")
    public void READ_ALL_TEAM_BY_TEAM_BUILDING_ID_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName2 = "team2";
        String teamName = "team";

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        TeamDto teamDto2 = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName2, createUserDtoList());
        TeamDto teamDto = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName, List.of());
        teamRepository.createTeam(teamDto);
        teamRepository.createTeam(teamDto2);
        List<TeamDto> result = teamRepository.readAllTeamByTeamBuildingId(teamBuildingDto.getTeamBuildingId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.size()),
                () -> Assertions.assertEquals(teamDto.getTeamBuildingId(), result.get(0).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamDto2.getTeamBuildingId(), result.get(1).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamDto.getTeamName(), result.get(0).getTeamName()),
                () -> Assertions.assertEquals(teamDto2.getTeamName(), result.get(1).getTeamName()),
                () -> Assertions.assertEquals(teamDto.getUserList().size(), result.get(0).getUserList().size()),
                () -> Assertions.assertEquals(teamDto2.getUserList().size(), result.get(1).getUserList().size()),
                () -> Assertions.assertEquals(teamDto.getLastModifiedAt(), result.get(0).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamDto2.getLastModifiedAt(), result.get(1).getLastModifiedAt())
        );

    }

    @Test
    @DisplayName("teambuilding_id로 전체 유저 조회")
    public void READ_ALL_USER_IN_TEAM_BY_TEAM_BUILDING_ID_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName2 = "team2";
        String teamName = "team";

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        TeamDto teamDto2 = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName2, createUserDtoList());
        TeamDto teamDto = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName, List.of());
        teamRepository.createTeam(teamDto);
        teamRepository.createTeam(teamDto2);
        List<UserDto> result = teamRepository.readAllUserByTeamBuildingId(teamBuildingDto.getTeamBuildingId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(6, result.size()),
                () -> Assertions.assertEquals(teamDto2.getUserList().get(0).getUserId(), result.get(0).getUserId()),
                () -> Assertions.assertEquals(teamDto2.getUserList().get(1).getUserId(), result.get(1).getUserId()),
                () -> Assertions.assertEquals(teamDto2.getUserList().get(2).getUserId(), result.get(2).getUserId()),
                () -> Assertions.assertEquals(teamDto2.getUserList().get(3).getUserId(), result.get(3).getUserId()),
                () -> Assertions.assertEquals(teamDto2.getUserList().get(4).getUserId(), result.get(4).getUserId()),
                () -> Assertions.assertEquals(teamDto2.getUserList().get(5).getUserId(), result.get(5).getUserId())
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
