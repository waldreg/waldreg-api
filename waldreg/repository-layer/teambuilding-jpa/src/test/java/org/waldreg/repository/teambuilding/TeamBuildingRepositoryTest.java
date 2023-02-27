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
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.teambuilding.TeamUser;
import org.waldreg.domain.user.User;
import org.waldreg.repository.teambuilding.mapper.TeamBuildingRepositoryMapper;
import org.waldreg.repository.teambuilding.repository.JpaTeamBuildingRepository;
import org.waldreg.repository.teambuilding.repository.JpaTeamRepository;
import org.waldreg.repository.teambuilding.repository.JpaTeamUserRepository;
import org.waldreg.repository.teambuilding.repository.TestJpaCharacterRepository;
import org.waldreg.repository.teambuilding.repository.TestJpaUserRepository;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingRepository;

@DataJpaTest
@ContextConfiguration(classes = {TeamBuildingRepositoryMapper.class, TeamBuildingRepositoryServiceProvider.class, JpaTeamBuildingTestInitializer.class})
@TestPropertySource("classpath:h2-application.properties")
public class TeamBuildingRepositoryTest{

    @Autowired
    private TeamBuildingRepository teamBuildingRepository;
    @Autowired
    private JpaTeamBuildingRepository jpaTeamBuildingRepository;
    @Autowired
    private JpaTeamRepository jpaTeamRepository;
    @Autowired
    private TestJpaUserRepository testJpaUserRepository;
    @Autowired
    private JpaTeamUserRepository jpaTeamUserRepository;
    @Autowired
    private TestJpaCharacterRepository testJpaCharacterRepository;

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
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        Character savedCharacter = testJpaCharacterRepository.saveAndFlush(character);
        List<UserDto> userDtoList = createUserDtoList(savedCharacter);

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingDto.getTeamBuildingId(), userDtoList);
        teamBuildingDto.setTeamDtoList(teamDtoList);

        //then
        Assertions.assertDoesNotThrow(() -> teamBuildingRepository.updateTeamListInTeamBuilding(teamBuildingDto));

    }

    @Test
    @DisplayName("teamBuildingId로 팀 빌딩 조회")
    public void READ_TEAM_BUILDING_BY_TEAM_BUILDING_ID_SUCCESS_TEST(){
        //given
        String title = "hirhrirhr";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        Character savedCharacter = testJpaCharacterRepository.saveAndFlush(character);
        List<UserDto> userDtoList = createUserDtoList(savedCharacter);

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingDto.getTeamBuildingId(), userDtoList);
        teamBuildingDto.setTeamDtoList(teamDtoList);
        teamBuildingRepository.updateTeamListInTeamBuilding(teamBuildingDto);
        TeamBuildingDto result = teamBuildingRepository.readTeamBuildingById(teamBuildingDto.getTeamBuildingId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(teamBuildingDto.getTeamBuildingId(), result.getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamBuildingTitle(), result.getTeamBuildingTitle()),
                () -> Assertions.assertEquals(teamBuildingDto.getLastModifiedAt(), result.getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto.getCreatedAt(), result.getCreatedAt()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getTeamId(), result.getTeamList().get(0).getTeamId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getTeamBuildingId(), result.getTeamList().get(0).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getLastModifiedAt(), result.getTeamList().get(0).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getTeamName(), result.getTeamList().get(0).getTeamName()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getUserList().get(0).getUserId(), result.getTeamList().get(0).getUserList().get(0).getUserId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getUserList().size(), result.getTeamList().get(0).getUserList().size()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getTeamId(), result.getTeamList().get(1).getTeamId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getTeamBuildingId(), result.getTeamList().get(1).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getLastModifiedAt(), result.getTeamList().get(1).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getTeamName(), result.getTeamList().get(1).getTeamName()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getUserList().get(0).getUserId(), result.getTeamList().get(1).getUserList().get(0).getUserId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getUserList().size(), result.getTeamList().get(1).getUserList().size()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getTeamId(), result.getTeamList().get(2).getTeamId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getTeamBuildingId(), result.getTeamList().get(2).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getLastModifiedAt(), result.getTeamList().get(2).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getTeamName(), result.getTeamList().get(2).getTeamName()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getUserList().get(0).getUserId(), result.getTeamList().get(2).getUserList().get(0).getUserId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getUserList().size(), result.getTeamList().get(2).getUserList().size())
        );

    }

    @Test
    @DisplayName("팀 빌딩 전체 조회")
    public void READ_ALL_TEAM_BUILDING_SUCCESS_TEST(){
        //given
        String title = "hirhrirhr";
        String title2 = "fjfjfjfjff";
        String title3 = "tydufcvn";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        Character savedCharacter = testJpaCharacterRepository.saveAndFlush(character);
        List<UserDto> userDtoList = createUserDtoList(savedCharacter);
        int startIdx = 1;
        int endIdx = 2;

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingDto.getTeamBuildingId(), userDtoList);
        teamBuildingDto.setTeamDtoList(teamDtoList);
        teamBuildingRepository.updateTeamListInTeamBuilding(teamBuildingDto);
        TeamBuildingDto teamBuildingDto2 = teamBuildingRepository.createTeamBuilding(title2);
        List<TeamDto> teamDtoList2 = createTeamDtoList(teamBuildingDto2.getTeamBuildingId(), userDtoList);
        teamBuildingDto2.setTeamDtoList(teamDtoList2);
        teamBuildingRepository.updateTeamListInTeamBuilding(teamBuildingDto2);
        TeamBuildingDto teamBuildingDto3 = teamBuildingRepository.createTeamBuilding(title3);
        List<TeamDto> teamDtoList3 = createTeamDtoList(teamBuildingDto3.getTeamBuildingId(), userDtoList);
        teamBuildingDto3.setTeamDtoList(teamDtoList3);
        teamBuildingRepository.updateTeamListInTeamBuilding(teamBuildingDto3);
        List<TeamBuildingDto> result = teamBuildingRepository.readAllTeamBuilding(startIdx, endIdx);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.size()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamBuildingId(), result.get(0).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamBuildingTitle(), result.get(0).getTeamBuildingTitle()),
                () -> Assertions.assertEquals(teamBuildingDto3.getLastModifiedAt(), result.get(0).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto3.getCreatedAt(), result.get(0).getCreatedAt()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(0).getTeamId(), result.get(0).getTeamList().get(0).getTeamId()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(0).getTeamBuildingId(), result.get(0).getTeamList().get(0).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(0).getLastModifiedAt(), result.get(0).getTeamList().get(0).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(0).getTeamName(), result.get(0).getTeamList().get(0).getTeamName()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(0).getUserList().get(0).getUserId(), result.get(0).getTeamList().get(0).getUserList().get(0).getUserId()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(0).getUserList().size(), result.get(0).getTeamList().get(0).getUserList().size()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(1).getTeamId(), result.get(0).getTeamList().get(1).getTeamId()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(1).getTeamBuildingId(), result.get(0).getTeamList().get(1).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(1).getLastModifiedAt(), result.get(0).getTeamList().get(1).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(1).getTeamName(), result.get(0).getTeamList().get(1).getTeamName()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(1).getUserList().get(0).getUserId(), result.get(0).getTeamList().get(1).getUserList().get(0).getUserId()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(1).getUserList().size(), result.get(0).getTeamList().get(1).getUserList().size()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(2).getTeamId(), result.get(0).getTeamList().get(2).getTeamId()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(2).getTeamBuildingId(), result.get(0).getTeamList().get(2).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(2).getLastModifiedAt(), result.get(0).getTeamList().get(2).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(2).getTeamName(), result.get(0).getTeamList().get(2).getTeamName()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(2).getUserList().get(0).getUserId(), result.get(0).getTeamList().get(2).getUserList().get(0).getUserId()),
                () -> Assertions.assertEquals(teamBuildingDto3.getTeamList().get(2).getUserList().size(), result.get(0).getTeamList().get(2).getUserList().size()),
                () -> Assertions.assertEquals(teamBuildingDto2.getTeamBuildingId(), result.get(1).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto2.getTeamBuildingTitle(), result.get(1).getTeamBuildingTitle()),
                () -> Assertions.assertEquals(teamBuildingDto2.getLastModifiedAt(), result.get(1).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto2.getCreatedAt(), result.get(1).getCreatedAt()),
                () -> Assertions.assertEquals(teamBuildingDto2.getTeamList().size(), result.get(0).getTeamList().size())
        );

    }

    @Test
    @DisplayName("팀 빌딩 제목 수정")
    public void UPDATE_TEAM_BUILDING_TITLE_BY_TEAM_BUILDING_ID_SUCCESS_TEST(){
        //given
        String title = "hirhrirhr";
        String modifiedTitle = "merong";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        Character savedCharacter = testJpaCharacterRepository.saveAndFlush(character);
        List<UserDto> userDtoList = createUserDtoList(savedCharacter);

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingDto.getTeamBuildingId(), userDtoList);
        teamBuildingDto.setTeamDtoList(teamDtoList);
        teamBuildingRepository.updateTeamListInTeamBuilding(teamBuildingDto);
        teamBuildingRepository.updateTeamBuildingTitleById(teamBuildingDto.getTeamBuildingId(), modifiedTitle);
        teamBuildingDto.setTeamBuildingTitle(modifiedTitle);
        TeamBuildingDto result = teamBuildingRepository.readTeamBuildingById(teamBuildingDto.getTeamBuildingId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(teamBuildingDto.getTeamBuildingId(), result.getTeamBuildingId()),
                () -> Assertions.assertEquals(modifiedTitle, result.getTeamBuildingTitle()),
                () -> Assertions.assertEquals(teamBuildingDto.getLastModifiedAt(), result.getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto.getCreatedAt(), result.getCreatedAt()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getTeamId(), result.getTeamList().get(0).getTeamId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getTeamBuildingId(), result.getTeamList().get(0).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getLastModifiedAt(), result.getTeamList().get(0).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getTeamName(), result.getTeamList().get(0).getTeamName()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getUserList().get(0).getUserId(), result.getTeamList().get(0).getUserList().get(0).getUserId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getUserList().size(), result.getTeamList().get(0).getUserList().size()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getTeamId(), result.getTeamList().get(1).getTeamId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getTeamBuildingId(), result.getTeamList().get(1).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getLastModifiedAt(), result.getTeamList().get(1).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getTeamName(), result.getTeamList().get(1).getTeamName()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getUserList().get(0).getUserId(), result.getTeamList().get(1).getUserList().get(0).getUserId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getUserList().size(), result.getTeamList().get(1).getUserList().size()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getTeamId(), result.getTeamList().get(2).getTeamId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getTeamBuildingId(), result.getTeamList().get(2).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getLastModifiedAt(), result.getTeamList().get(2).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getTeamName(), result.getTeamList().get(2).getTeamName()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getUserList().get(0).getUserId(), result.getTeamList().get(2).getUserList().get(0).getUserId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getUserList().size(), result.getTeamList().get(2).getUserList().size())
        );

    }

    @Test
    @DisplayName("teamBuilding id로 팀빌딩 삭제")
    public void DELETE_TEAM_LIST_IN_NEW_TEAM_BUILDING_SUCCESS_TEST(){
        //given
        String title = "hirhrirhr";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        Character savedCharacter = testJpaCharacterRepository.saveAndFlush(character);
        List<UserDto> userDtoList = createUserDtoList(savedCharacter);

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingDto.getTeamBuildingId(), userDtoList);
        teamBuildingDto.setTeamDtoList(teamDtoList);
        teamBuildingRepository.updateTeamListInTeamBuilding(teamBuildingDto);
        teamBuildingRepository.deleteTeamBuildingById(teamBuildingDto.getTeamBuildingId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertFalse(teamBuildingRepository.isExistTeamBuilding(teamBuildingDto.getTeamBuildingId())),
                () -> Assertions.assertFalse(jpaTeamRepository.existsById(teamDtoList.get(0).getTeamId())),
                () -> Assertions.assertTrue(testJpaUserRepository.existsById(userDtoList.get(0).getId()))
        );

    }


    private List<TeamDto> createTeamDtoList(int teambuildingId, List<UserDto> userDtoList){
        List<TeamDto> teamDtoList = new ArrayList<>();
        teamDtoList.add(createTeamAndTeamDto(teambuildingId, "team 1", List.of(userDtoList.get(0), userDtoList.get(2))));
        teamDtoList.add(createTeamAndTeamDto(teambuildingId, "team 2", List.of(userDtoList.get(3), userDtoList.get(4))));
        teamDtoList.add(createTeamAndTeamDto(teambuildingId, "team 3", List.of(userDtoList.get(1), userDtoList.get(5))));
        return teamDtoList;
    }

    private TeamDto createTeamAndTeamDto(int teamBuildingId, String teamName, List<org.waldreg.teambuilding.dto.UserDto> userDtoList){
        Team team = Team.builder()
                .teamBuilding(jpaTeamBuildingRepository.findById(teamBuildingId).get())
                .teamName(teamName)
                .build();
        team = jpaTeamRepository.saveAndFlush(team);
        List<User> userList = new ArrayList<>();
        userDtoList.stream().forEach(u -> userList.add(testJpaUserRepository.findById(u.getId()).get()));
        team.setTeamUserList(createTeamUserList(team, userList));
        Team storedTeam = jpaTeamRepository.saveAndFlush(team);
        return TeamDto.builder()
                .teamBuildingId(teamBuildingId)
                .teamId(storedTeam.getTeamId())
                .lastModifiedAt(storedTeam.getLastModifiedAt())
                .teamName(storedTeam.getTeamName())
                .userDtoList(userDtoList)
                .build();
    }

    private List<TeamUser> createTeamUserList(Team team, List<User> userList){
        List<TeamUser> teamUserList = new ArrayList<>();
        userList.stream().forEach(u -> teamUserList.add(jpaTeamUserRepository.saveAndFlush(TeamUser.builder()
                .team(team)
                .user(u)
                .build())));
        return teamUserList;
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
        User storedUser = testJpaUserRepository.saveAndFlush(user);
        return UserDto.builder()
                .id(storedUser.getId())
                .name(storedUser.getName())
                .userId(storedUser.getUserId())
                .build();
    }

}
