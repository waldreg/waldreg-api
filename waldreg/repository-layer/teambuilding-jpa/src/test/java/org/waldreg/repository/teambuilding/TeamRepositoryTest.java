package org.waldreg.repository.teambuilding;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.teambuilding.repository.JpaTeamUserRepository;
import org.waldreg.repository.teambuilding.repository.JpaTeamUserWrapperRepository;
import org.waldreg.repository.teambuilding.repository.TestJpaCharacterRepository;
import org.waldreg.repository.teambuilding.team.TeamRepositoryServiceProvider;
import org.waldreg.repository.teambuilding.team.mapper.TeamRepositoryMapper;
import org.waldreg.repository.teambuilding.teambuilding.TeamBuildingRepositoryServiceProvider;
import org.waldreg.repository.teambuilding.teambuilding.mapper.TeamBuildingRepositoryMapper;
import org.waldreg.repository.teambuilding.teambuilding.repository.JpaTeamBuildingRepository;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.team.spi.TeamInTeamBuildingRepository;
import org.waldreg.teambuilding.team.spi.TeamRepository;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingRepository;

@DataJpaTest
@ContextConfiguration(classes = {TeamRepositoryMapper.class, TeamBuildingRepositoryServiceProvider.class, TeamBuildingRepositoryMapper.class, TeamRepositoryServiceProvider.class, JpaTeamBuildingTestInitializer.class})
@TestPropertySource("classpath:h2-application.properties")
public class TeamRepositoryTest{

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamBuildingRepository teamBuildingRepository;
    @Autowired
    private TeamInTeamBuildingRepository teamInTeamBuildingRepository;
    @Autowired
    private JpaTeamBuildingRepository jpaTeamBuildingRepository;
    @Autowired
    private JpaTeamUserRepository jpaTeamUserRepository;
    @Autowired
    private JpaTeamUserWrapperRepository jpaTeamUserWrapperRepository;
    @Autowired
    private TestJpaCharacterRepository testJpaCharacterRepository;

    @Test
    @DisplayName("새로운 team 생성")
    public void CREATE_NEW_TEAM_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName = "team";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        Character savedCharacter = testJpaCharacterRepository.saveAndFlush(character);
        List<UserDto> userDtoList = createUserDtoList(savedCharacter);

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        TeamDto teamDto = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName, userDtoList);
        TeamDto result = teamRepository.createTeam(teamDto);

        //then
        Assertions.assertAll(
                () -> Assertions.assertTrue(result.getTeamId() != 0),
                () -> Assertions.assertEquals(teamDto.getTeamBuildingId(), result.getTeamBuildingId()),
                () -> Assertions.assertEquals(teamDto.getTeamName(), result.getTeamName()),
                () -> Assertions.assertEquals(teamDto.getUserList().size(), result.getUserList().size()),
                () -> Assertions.assertEquals(teamDto.getLastModifiedAt(), result.getLastModifiedAt())
        );

    }

    @Test
    @DisplayName("team_id로 조회")
    public void READ_TEAM_BY_TEAM_ID_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName = "team";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        Character savedCharacter = testJpaCharacterRepository.saveAndFlush(character);
        List<UserDto> userDtoList = createUserDtoList(savedCharacter);

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        TeamDto teamDto = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName, userDtoList);
        TeamDto storedTeamDto = teamRepository.createTeam(teamDto);
        TeamDto result = teamRepository.readTeamById(storedTeamDto.getTeamId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(storedTeamDto.getTeamId(), result.getTeamId()),
                () -> Assertions.assertEquals(storedTeamDto.getTeamBuildingId(), result.getTeamBuildingId()),
                () -> Assertions.assertEquals(storedTeamDto.getTeamName(), result.getTeamName()),
                () -> Assertions.assertEquals(storedTeamDto.getUserList().size(), result.getUserList().size()),
                () -> Assertions.assertEquals(storedTeamDto.getLastModifiedAt(), result.getLastModifiedAt())
        );

    }

    @Test
    @DisplayName("team_id로 team 수정")
    public void UPDATE_TEAM_BY_TEAM_ID_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName = "team";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        Character savedCharacter = testJpaCharacterRepository.saveAndFlush(character);
        List<UserDto> userDtoList = createUserDtoList(savedCharacter);
        List<UserDto> modifiedUserDtoList = new ArrayList<>();
        User user = User.builder()
                .userId("itsme")
                .name("itsme")
                .userPassword("2dkdkdfdfdk!")
                .phoneNumber("010-1234-1234")
                .character(character)
                .build();
        User storedUser = jpaTeamUserRepository.saveAndFlush(user);
        modifiedUserDtoList.add(UserDto.builder()
                .id(storedUser.getId())
                .name(storedUser.getName())
                .userId(storedUser.getUserId())
                .build());

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        TeamDto teamDto = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName, userDtoList);
        TeamDto storedTeamDto = teamRepository.createTeam(teamDto);
        TeamDto modifiedTeamDto = createTeamDto(teamBuildingDto.getTeamBuildingId(), "changing~", modifiedUserDtoList);
        teamRepository.updateTeamById(storedTeamDto.getTeamId(), modifiedTeamDto);
        TeamDto result = teamRepository.readTeamById(storedTeamDto.getTeamId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(storedTeamDto.getTeamId(), result.getTeamId()),
                () -> Assertions.assertEquals(modifiedTeamDto.getTeamBuildingId(), result.getTeamBuildingId()),
                () -> Assertions.assertEquals(modifiedTeamDto.getTeamName(), result.getTeamName()),
                () -> Assertions.assertEquals(modifiedTeamDto.getUserList().size(), result.getUserList().size()),
                () -> Assertions.assertNotEquals(storedTeamDto.getLastModifiedAt(), result.getLastModifiedAt())
        );

    }

    @Test
    @DisplayName("team_id로 삭제")
    public void DELETE_TEAM_BY_TEAM_ID_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName = "team";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        Character savedCharacter = testJpaCharacterRepository.saveAndFlush(character);
        List<UserDto> userDtoList = createUserDtoList(savedCharacter);

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        TeamDto teamDto = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName, userDtoList);
        TeamDto teamDto2 = createTeamDto(teamBuildingDto.getTeamBuildingId(), "~~~", List.of());
        TeamDto storedTeamDto = teamRepository.createTeam(teamDto);
        teamInTeamBuildingRepository.addTeamInTeamBuildingTeamList(storedTeamDto);
        TeamDto storedTeamDto2 = teamRepository.createTeam(teamDto2);
        teamInTeamBuildingRepository.addTeamInTeamBuildingTeamList(storedTeamDto2);
        teamRepository.deleteTeamById(storedTeamDto.getTeamId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertFalse(() -> teamRepository.isExistTeam(storedTeamDto.getTeamId())),
                () -> Assertions.assertEquals(1, jpaTeamBuildingRepository.findById(teamBuildingDto.getTeamBuildingId()).get().getTeamList().size()),
                () -> Assertions.assertTrue(() -> jpaTeamUserRepository.existsById(userDtoList.get(0).getId()))
        );

    }

    @Test
    @DisplayName("teambuilding_id로 전체 조회")
    public void READ_ALL_TEAM_BY_TEAM_BUILDING_ID_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName = "team";
        String teamName2 = "team2";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        Character savedCharacter = testJpaCharacterRepository.saveAndFlush(character);
        List<UserDto> userDtoList = createUserDtoList(savedCharacter);

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        TeamDto teamDto = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName, userDtoList);
        TeamDto teamDto2 = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName2, List.of());
        TeamDto storedTeamDto = teamRepository.createTeam(teamDto);
        teamInTeamBuildingRepository.addTeamInTeamBuildingTeamList(storedTeamDto);
        TeamDto storedTeamDto2 = teamRepository.createTeam(teamDto2);
        teamInTeamBuildingRepository.addTeamInTeamBuildingTeamList(storedTeamDto2);
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
        String teamName = "team";
        String teamName2 = "team2";
        String teamName3 = "team3";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        Character savedCharacter = testJpaCharacterRepository.saveAndFlush(character);
        List<UserDto> userDtoList = createUserDtoList(savedCharacter);
        List<UserDto> userDtoList2 = new ArrayList<>();
        User user = User.builder()
                .userId("itsme")
                .name("itsme")
                .userPassword("2dkdkdfdfdk!")
                .phoneNumber("010-1234-1234")
                .character(character)
                .build();
        User storedUser = jpaTeamUserRepository.saveAndFlush(user);
        userDtoList2.add(UserDto.builder()
                .id(storedUser.getId())
                .name(storedUser.getName())
                .userId(storedUser.getUserId())
                .build());

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        TeamDto teamDto = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName, userDtoList);
        TeamDto teamDto2 = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName2, userDtoList2);
        TeamDto teamDto3 = createTeamDto(teamBuildingDto.getTeamBuildingId(), teamName3, userDtoList);
        TeamDto storedTeamDto = teamRepository.createTeam(teamDto);
        teamInTeamBuildingRepository.addTeamInTeamBuildingTeamList(storedTeamDto);
        TeamDto storedTeamDto2 = teamRepository.createTeam(teamDto2);
        teamInTeamBuildingRepository.addTeamInTeamBuildingTeamList(storedTeamDto2);
        TeamDto storedTeamDto3 = teamRepository.createTeam(teamDto3);
        teamInTeamBuildingRepository.addTeamInTeamBuildingTeamList(storedTeamDto3);
        List<UserDto> result = teamRepository.readAllUserByTeamBuildingId(teamBuildingDto.getTeamBuildingId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(7, result.size()),
                () -> Assertions.assertEquals(userDtoList.get(0).getUserId(), result.get(0).getUserId()),
                () -> Assertions.assertEquals(userDtoList.get(1).getUserId(), result.get(1).getUserId()),
                () -> Assertions.assertEquals(userDtoList.get(2).getUserId(), result.get(2).getUserId()),
                () -> Assertions.assertEquals(userDtoList.get(3).getUserId(), result.get(3).getUserId()),
                () -> Assertions.assertEquals(userDtoList.get(4).getUserId(), result.get(4).getUserId()),
                () -> Assertions.assertEquals(userDtoList.get(5).getUserId(), result.get(5).getUserId()),
                () -> Assertions.assertEquals(userDtoList2.get(0).getUserId(), result.get(6).getUserId())
        );

    }

    private TeamDto createTeamDto(int teamBuildingId, String teamName, List<UserDto> userDtoList){
        return TeamDto.builder()
                .teamBuildingId(teamBuildingId)
                .teamName(teamName)
                .userDtoList(userDtoList)
                .build();
    }

    private List<UserDto> createUserDtoList(Character character){
        List<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(createUserDtoAndUser("alcuk_id", character));
        userDtoList.add(createUserDtoAndUser("alcuk_id2", character));
        userDtoList.add(createUserDtoAndUser("alcuk_id3", character));
        userDtoList.add(createUserDtoAndUser("alcuk_id4", character));
        userDtoList.add(createUserDtoAndUser("alcuk_id5", character));
        userDtoList.add(createUserDtoAndUser("alcuk_id6", character));
        return userDtoList;
    }

    private UserDto createUserDtoAndUser(String userId, Character character){
        User user = User.builder()
                .userId(userId)
                .name(userId)
                .userPassword("2dkdkdfdfdk!")
                .phoneNumber("010-1234-1234")
                .character(character)
                .build();
        User storedUser = jpaTeamUserRepository.saveAndFlush(user);
        return UserDto.builder()
                .id(storedUser.getId())
                .name(storedUser.getName())
                .userId(storedUser.getUserId())
                .build();
    }

}
