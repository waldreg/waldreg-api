package org.waldreg.repository.teambuilding.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.teambuilding.TeamBuilding;
import org.waldreg.domain.teambuilding.TeamUser;
import org.waldreg.domain.user.User;
import org.waldreg.repository.teambuilding.team.repository.JpaTeamRepository;
import org.waldreg.repository.teambuilding.teambuilding.repository.JpaTeamBuildingRepository;

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
public class JpaTeamRepositoryTest{

    @Autowired
    private JpaTeamRepository jpaTeamRepository;
    @Autowired
    private TestJpaCharacterRepository testJpaCharacterRepository;
    @Autowired
    private JpaTeamUserWrapperRepository jpaTeamUserWrapperRepository;
    @Autowired
    private JpaTeamUserRepository jpaTeamUserRepository;
    @Autowired
    private JpaTeamBuildingRepository jpaTeamBuildingRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("새로운 team 생성 성공 테스트")
    public void CREATE_NEW_TEAM_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName = "team";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();

        //when
        TeamBuilding teamBuilding = createTeamBuilding(title);
        List<User> userList = createUserList(character);
        Team team = createTeam(teamBuilding, teamName, userList);
        Team result = jpaTeamRepository.saveAndFlush(team);

        //then
        assertAll(team, result);

    }

    @Test
    @DisplayName("team_id로 조회 성공 테스트")
    public void READ_TEAM_BY_ID_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName = "team";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();

        //when
        TeamBuilding teamBuilding = createTeamBuilding(title);
        List<User> userList = createUserList(character);
        Team team = createTeam(teamBuilding, teamName, userList);
        Team result = jpaTeamRepository.findById(team.getTeamId()).get();

        //then
        assertAll(team, result);

    }

    @Test
    @DisplayName("team_id로 수정 성공 테스트")
    public void UPDATE_TEAM_BY_ID_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName = "team";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();

        //when
        TeamBuilding teamBuilding = createTeamBuilding(title);
        List<User> userList = createUserList(character);
        Team team = createTeam(teamBuilding, teamName, userList);
        team.setTeamUserList(List.of());
        team.setLastModifiedAt(LocalDateTime.now());
        team.setTeamName("blabla");
        entityManager.flush();
        entityManager.clear();
        jpaTeamRepository.save(team);
        Team result = jpaTeamRepository.findById(team.getTeamId()).get();

        //then
        assertAll(team, result);

    }

    @Test
    @DisplayName("teambuilding_id로 전체 조회 성공 테스트")
    public void READ_ALL_TEAM_BY_TEAM_BUILDING_ID_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName = "team";
        String teamName2 = "team2";
        String teamName3 = "team3";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();

        //when
        TeamBuilding teamBuilding = createTeamBuilding(title);
        List<User> userList = createUserList(character);
        Team team = createTeam(teamBuilding, teamName, userList);
        Team team2 = createTeam(teamBuilding, teamName2, List.of());
        Team team3 = createTeam(teamBuilding, teamName3, userList);
        List<Team> result = jpaTeamRepository.findAllByTeamBuildingId(teamBuilding.getTeamBuildingId());

        //then
        assertAll(team, result.get(0));
        assertAll(team2, result.get(1));
        assertAll(team3, result.get(2));

    }

    @Test
    @DisplayName("teambuilding_id로 전체 조회 성공 테스트")
    public void READ_ALL_TEAM_USER_BY_TEAM_BUILDING_ID_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName = "team";
        String teamName2 = "team2";
        String teamName3 = "team3";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();

        //when
        TeamBuilding teamBuilding = createTeamBuilding(title);
        List<User> userList = createUserList(character);
        createTeam(teamBuilding, teamName, userList);
        createTeam(teamBuilding, teamName2, List.of());
        createTeam(teamBuilding, teamName3, userList);
        List<Integer> result = jpaTeamRepository.findAllUserIdByTeamBuildingId(teamBuilding.getTeamBuildingId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(6, result.size()),
                () -> Assertions.assertEquals(userList.get(0).getId(), result.get(0)),
                () -> Assertions.assertEquals(userList.get(1).getId(), result.get(1)),
                () -> Assertions.assertEquals(userList.get(2).getId(), result.get(2)),
                () -> Assertions.assertEquals(userList.get(3).getId(), result.get(3)),
                () -> Assertions.assertEquals(userList.get(4).getId(), result.get(4)),
                () -> Assertions.assertEquals(userList.get(5).getId(), result.get(5))
        );

    }

    @Test
    @DisplayName("team_id로 삭제 성공 테스트")
    public void DELETE_TEAM_BY_ID_SUCCESS_TEST(){
        //given
        String title = "teamBuilding";
        String teamName = "team";
        String teamName2 = "team2";
        String teamName3 = "team3";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();

        //when
        TeamBuilding teamBuilding = createTeamBuilding(title);
        List<User> userList = createUserList(character);
        Team team = createTeam(teamBuilding, teamName, userList);
        Team team2 = createTeam(teamBuilding, teamName2, List.of());
        Team team3 = createTeam(teamBuilding, teamName3, userList);
        List<Team> teamList = new ArrayList<>();
        teamList.add(team);
        teamList.add(team2);
        teamList.add(team3);
        teamBuilding.setTeamList(teamList);
        jpaTeamBuildingRepository.saveAndFlush(teamBuilding);
        entityManager.clear();
        jpaTeamRepository.deleteById(team.getTeamId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertFalse(() -> jpaTeamRepository.existsById(team.getTeamId())),
                () -> Assertions.assertEquals(2, jpaTeamBuildingRepository.findById(teamBuilding.getTeamBuildingId()).get().getTeamList().size()),
                () -> Assertions.assertTrue(() -> jpaTeamUserRepository.existsById(userList.get(0).getId()))
        );

    }

    private void assertAll(Team expected, Team actual){
        Assertions.assertAll(
                () -> Assertions.assertEquals(expected.getTeamId(), actual.getTeamId()),
                () -> Assertions.assertEquals(expected.getTeamBuilding().getTeamBuildingId(), actual.getTeamBuilding().getTeamBuildingId()),
                () -> Assertions.assertEquals(expected.getTeamBuilding().getTeamBuildingTitle(), actual.getTeamBuilding().getTeamBuildingTitle()),
                () -> Assertions.assertEquals(expected.getTeamName(), actual.getTeamName()),
                () -> Assertions.assertEquals(expected.getTeamUserList().size(), actual.getTeamUserList().size()),
                () -> Assertions.assertEquals(expected.getLastModifiedAt().withNano(0), actual.getLastModifiedAt().withNano(0))
        );
    }

    private TeamBuilding createTeamBuilding(String title){
        TeamBuilding teamBuilding = jpaTeamBuildingRepository.saveAndFlush(TeamBuilding.builder()
                .teamBuildingTitle(title)
                .build());
        entityManager.clear();
        return teamBuilding;
    }

    private Team createTeam(TeamBuilding teamBuilding, String teamName, List<User> userList){
        Team team = Team.builder()
                .teamBuilding(teamBuilding)
                .teamName(teamName)
                .build();
        Team storedTeam = jpaTeamRepository.saveAndFlush(team);
        storedTeam.setTeamUserList(createTeamUserList(storedTeam, userList));
        entityManager.flush();
        entityManager.clear();
        return storedTeam;
    }

    private List<TeamUser> createTeamUserList(Team team, List<User> userList){
        List<TeamUser> teamUserList = new ArrayList<>();
        userList.stream().forEach(u -> teamUserList.add(jpaTeamUserWrapperRepository.saveAndFlush(TeamUser.builder()
                .team(team)
                .user(u)
                .build())));
        return teamUserList;
    }

    private List<User> createUserList(Character character){
        testJpaCharacterRepository.saveAndFlush(character);
        List<User> userList = new ArrayList<>();
        userList.add(createUser("alcuk_id", character));
        userList.add(createUser("alcuk_id2", character));
        userList.add(createUser("alcuk_id3", character));
        userList.add(createUser("alcuk_id4", character));
        userList.add(createUser("alcuk_id5", character));
        userList.add(createUser("alcuk_id6", character));
        return userList;
    }

    private User createUser(String userId, Character character){
        User user = User.builder()
                .userId(userId)
                .name(userId)
                .userPassword("2dkdkdfdfdk!")
                .phoneNumber("010-1234-1234")
                .character(character)
                .build();
        User storedUser = jpaTeamUserRepository.saveAndFlush(user);
        return storedUser;
    }

}
