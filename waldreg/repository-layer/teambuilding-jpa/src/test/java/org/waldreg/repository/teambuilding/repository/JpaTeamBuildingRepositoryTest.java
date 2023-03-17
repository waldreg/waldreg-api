package org.waldreg.repository.teambuilding.repository;

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
import org.waldreg.repository.teambuilding.teamuser.repository.JpaTeamUserRepository;

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
public class JpaTeamBuildingRepositoryTest{

    @Autowired
    private JpaTeamBuildingRepository jpaTeamBuildingRepository;
    @Autowired
    private JpaTeamRepository jpaTeamRepository;
    @Autowired
    private TestJpaTeamUserWrapperRepository testJpaTeamUserWrapperRepository;
    @Autowired
    private TestJpaCharacterRepository testJpaCharacterRepository;
    @Autowired
    private JpaTeamUserRepository jpaTeamUserRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("새로운 팀빌딩 그룹 생성 성공 테스트")
    public void CREATE_NEW_TEAM_BUILDING_SUCCESS_TEST(){
        //given
        String title = "hihihi";
        TeamBuilding teamBuilding = createTeamBuilding(title);

        //when
        TeamBuilding result = jpaTeamBuildingRepository.saveAndFlush(teamBuilding);

        //then
        Assertions.assertEquals(title, result.getTeamBuildingTitle());
    }

    @Test
    @DisplayName("새로 생성된 팀빌딩 그룹에 팀 목록 저장 및 id로 조회 성공 테스트")
    public void ADD_TEAM_LIST_IN_NEW_TEAM_BUILDING_AND_READ_BY_ID_SUCCESS_TEST(){
        //given
        String title = "hihihi";
        TeamBuilding teamBuilding = createTeamBuilding(title);
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        List<User> userList = createUserList(character);

        //when
        TeamBuilding storedTeamBuilding = jpaTeamBuildingRepository.saveAndFlush(teamBuilding);
        createTeamList(userList, storedTeamBuilding);
        entityManager.flush();
        entityManager.clear();
        TeamBuilding result = jpaTeamBuildingRepository.findById(storedTeamBuilding.getTeamBuildingId()).get();

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamBuildingId(), result.getTeamBuildingId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamBuildingTitle(), result.getTeamBuildingTitle()),
                () -> Assertions.assertEquals(storedTeamBuilding.getLastModifiedAt().withNano(0), result.getLastModifiedAt().withNano(0)),
                () -> Assertions.assertEquals(storedTeamBuilding.getCreatedAt().withNano(0), result.getCreatedAt().withNano(0)),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(0).getTeamId(), result.getTeamList().get(0).getTeamId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(0).getTeamBuilding().getTeamBuildingId(), result.getTeamList().get(0).getTeamBuilding().getTeamBuildingId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(0).getLastModifiedAt().withNano(0), result.getTeamList().get(0).getLastModifiedAt().withNano(0)),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(0).getTeamName(), result.getTeamList().get(0).getTeamName()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(0).getTeamUserList().get(0).getUser().getUserId(), result.getTeamList().get(0).getTeamUserList().get(0).getUser().getUserId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(0).getTeamUserList().size(), result.getTeamList().get(0).getTeamUserList().size()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(1).getTeamId(), result.getTeamList().get(1).getTeamId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(1).getTeamBuilding().getTeamBuildingId(), result.getTeamList().get(1).getTeamBuilding().getTeamBuildingId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(1).getLastModifiedAt().withNano(0), result.getTeamList().get(1).getLastModifiedAt().withNano(0)),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(1).getTeamName(), result.getTeamList().get(1).getTeamName()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(1).getTeamUserList().get(0).getUser().getUserId(), result.getTeamList().get(1).getTeamUserList().get(0).getUser().getUserId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(1).getTeamUserList().size(), result.getTeamList().get(1).getTeamUserList().size()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(2).getTeamId(), result.getTeamList().get(2).getTeamId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(2).getTeamBuilding().getTeamBuildingId(), result.getTeamList().get(2).getTeamBuilding().getTeamBuildingId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(2).getLastModifiedAt().withNano(0), result.getTeamList().get(2).getLastModifiedAt().withNano(0)),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(2).getTeamName(), result.getTeamList().get(2).getTeamName()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(2).getTeamUserList().get(0).getUser().getUserId(), result.getTeamList().get(2).getTeamUserList().get(0).getUser().getUserId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(2).getTeamUserList().size(), result.getTeamList().get(2).getTeamUserList().size())
        );

    }

    @Test
    @DisplayName("teambuilding_title 수정 성공 테스트")
    public void UPDATE_TEAM_BUILDING_TITLE_SUCCESS_TEST(){
        //given
        String title = "hihihi";
        String modifiedTitle = "dldldldlldld";
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        List<User> userList = createUserList(character);

        //when
        TeamBuilding teamBuilding = createTeamBuilding(title);
        TeamBuilding storedTeamBuilding = jpaTeamBuildingRepository.saveAndFlush(teamBuilding);
        createTeamList(userList, storedTeamBuilding);
        entityManager.flush();
        entityManager.clear();
        storedTeamBuilding.setTeamBuildingTitle(modifiedTitle);
        jpaTeamBuildingRepository.save(storedTeamBuilding);
        entityManager.flush();
        entityManager.clear();
        TeamBuilding result = jpaTeamBuildingRepository.findById(storedTeamBuilding.getTeamBuildingId()).get();

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamBuildingId(), result.getTeamBuildingId()),
                () -> Assertions.assertEquals(modifiedTitle, result.getTeamBuildingTitle()),
                () -> Assertions.assertEquals(storedTeamBuilding.getLastModifiedAt().withNano(0), result.getLastModifiedAt().withNano(0)),
                () -> Assertions.assertEquals(storedTeamBuilding.getCreatedAt().withNano(0), result.getCreatedAt().withNano(0)),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(0).getTeamId(), result.getTeamList().get(0).getTeamId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(0).getTeamBuilding().getTeamBuildingId(), result.getTeamList().get(0).getTeamBuilding().getTeamBuildingId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(0).getLastModifiedAt().withNano(0), result.getTeamList().get(0).getLastModifiedAt().withNano(0)),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(0).getTeamName(), result.getTeamList().get(0).getTeamName()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(0).getTeamUserList().get(0).getUser().getUserId(), result.getTeamList().get(0).getTeamUserList().get(0).getUser().getUserId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(0).getTeamUserList().size(), result.getTeamList().get(0).getTeamUserList().size()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(1).getTeamId(), result.getTeamList().get(1).getTeamId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(1).getTeamBuilding().getTeamBuildingId(), result.getTeamList().get(1).getTeamBuilding().getTeamBuildingId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(1).getLastModifiedAt().withNano(0), result.getTeamList().get(1).getLastModifiedAt().withNano(0)),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(1).getTeamName(), result.getTeamList().get(1).getTeamName()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(1).getTeamUserList().get(0).getUser().getUserId(), result.getTeamList().get(1).getTeamUserList().get(0).getUser().getUserId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(1).getTeamUserList().size(), result.getTeamList().get(1).getTeamUserList().size()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(2).getTeamId(), result.getTeamList().get(2).getTeamId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(2).getTeamBuilding().getTeamBuildingId(), result.getTeamList().get(2).getTeamBuilding().getTeamBuildingId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(2).getLastModifiedAt().withNano(0), result.getTeamList().get(2).getLastModifiedAt().withNano(0)),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(2).getTeamName(), result.getTeamList().get(2).getTeamName()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(2).getTeamUserList().get(0).getUser().getUserId(), result.getTeamList().get(2).getTeamUserList().get(0).getUser().getUserId()),
                () -> Assertions.assertEquals(storedTeamBuilding.getTeamList().get(2).getTeamUserList().size(), result.getTeamList().get(2).getTeamUserList().size())
        );

    }

    @Test
    @DisplayName("팀빌딩 삭제 성공 테스트")
    public void DELETE_TEAM_BUILDING_SUCCESS_TEST(){
        //given
        String title = "hihihi";
        TeamBuilding teamBuilding = createTeamBuilding(title);
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        List<User> userList = createUserList(character);

        //when
        TeamBuilding storedTeamBuilding = jpaTeamBuildingRepository.saveAndFlush(teamBuilding);
        List<Team> teamList = createTeamList(userList, storedTeamBuilding);
        storedTeamBuilding.setTeamList(teamList);
        entityManager.flush();
        entityManager.clear();
        jpaTeamBuildingRepository.deleteById(storedTeamBuilding.getTeamBuildingId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertFalse(() -> jpaTeamBuildingRepository.existsById(storedTeamBuilding.getTeamBuildingId())),
                () -> Assertions.assertEquals(0,teamList.size()),
                () -> Assertions.assertTrue(() -> jpaTeamUserRepository.existsById(userList.get(0).getId()))
        );

    }

    private TeamBuilding createTeamBuilding(String title){
        return TeamBuilding.builder()
                .teamBuildingTitle(title)
                .build();
    }

    private List<Team> createTeamList(List<User> userList, TeamBuilding teamBuilding){
        List<Team> teamList = teamBuilding.getTeamList();
        teamList.add(createTeam(teamBuilding, "team 1", List.of(userList.get(0), userList.get(2))));
        teamList.add(createTeam(teamBuilding, "team 2", List.of(userList.get(3), userList.get(4))));
        teamList.add(createTeam(teamBuilding, "team 3", List.of(userList.get(1), userList.get(5))));
        return teamList;
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

    private Team createTeam(TeamBuilding teamBuilding, String teamName, List<User> userList){
        Team team = Team.builder()
                .teamBuilding(teamBuilding)
                .teamName(teamName)
                .build();
        Team storedTeam = jpaTeamRepository.saveAndFlush(team);
        userList.forEach(t -> storedTeam.addTeamUser(t));
        entityManager.flush();
        entityManager.clear();
        return team;
    }

}
