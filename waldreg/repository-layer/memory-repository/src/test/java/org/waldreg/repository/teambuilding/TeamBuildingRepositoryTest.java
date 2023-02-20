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
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.character.CharacterMapper;
import org.waldreg.repository.character.MemoryCharacterRepository;
import org.waldreg.repository.user.MemoryUserRepository;
import org.waldreg.repository.user.UserMapper;
import org.waldreg.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;
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
        teamBuildingDto.setTeamDtoList(teamDtoList);

        //then
        Assertions.assertDoesNotThrow(() -> teamBuildingRepository.updateTeamListInTeamBuilding(teamBuildingDto));

    }

    @Test
    @DisplayName("teamBuildingId로 팀 빌딩 조회")
    public void READ_TEAM_BUILDING_BY_TEAM_BUILDING_ID_SUCCESS_TEST(){
        //given
        String title = "hirhrirhr";

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingDto.getTeamBuildingId());
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

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingDto.getTeamBuildingId());
        teamBuildingDto.setTeamDtoList(teamDtoList);
        teamBuildingRepository.updateTeamListInTeamBuilding(teamBuildingDto);
        TeamBuildingDto teamBuildingDto2 = teamBuildingRepository.createTeamBuilding(title2);
        teamBuildingDto2.setTeamDtoList(teamDtoList);
        teamBuildingRepository.updateTeamListInTeamBuilding(teamBuildingDto2);
        TeamBuildingDto teamBuildingDto3 = teamBuildingRepository.createTeamBuilding(title3);
        teamBuildingDto3.setTeamDtoList(teamDtoList);
        teamBuildingRepository.updateTeamListInTeamBuilding(teamBuildingDto3);
        List<TeamBuildingDto> result = teamBuildingRepository.readAllTeamBuilding(1, 2);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.size()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamBuildingId(), result.get(0).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamBuildingTitle(), result.get(0).getTeamBuildingTitle()),
                () -> Assertions.assertEquals(teamBuildingDto.getLastModifiedAt(), result.get(0).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto.getCreatedAt(), result.get(0).getCreatedAt()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getTeamId(), result.get(0).getTeamList().get(0).getTeamId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getTeamBuildingId(), result.get(0).getTeamList().get(0).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getLastModifiedAt(), result.get(0).getTeamList().get(0).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getTeamName(), result.get(0).getTeamList().get(0).getTeamName()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getUserList().get(0).getUserId(), result.get(0).getTeamList().get(0).getUserList().get(0).getUserId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(0).getUserList().size(), result.get(0).getTeamList().get(0).getUserList().size()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getTeamId(), result.get(0).getTeamList().get(1).getTeamId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getTeamBuildingId(), result.get(0).getTeamList().get(1).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getLastModifiedAt(), result.get(0).getTeamList().get(1).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getTeamName(), result.get(0).getTeamList().get(1).getTeamName()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getUserList().get(0).getUserId(), result.get(0).getTeamList().get(1).getUserList().get(0).getUserId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(1).getUserList().size(), result.get(0).getTeamList().get(1).getUserList().size()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getTeamId(), result.get(0).getTeamList().get(2).getTeamId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getTeamBuildingId(), result.get(0).getTeamList().get(2).getTeamBuildingId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getLastModifiedAt(), result.get(0).getTeamList().get(2).getLastModifiedAt()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getTeamName(), result.get(0).getTeamList().get(2).getTeamName()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getUserList().get(0).getUserId(), result.get(0).getTeamList().get(2).getUserList().get(0).getUserId()),
                () -> Assertions.assertEquals(teamBuildingDto.getTeamList().get(2).getUserList().size(), result.get(0).getTeamList().get(2).getUserList().size()),
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

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingDto.getTeamBuildingId());
        teamBuildingDto.setTeamDtoList(teamDtoList);
        teamBuildingRepository.updateTeamListInTeamBuilding(teamBuildingDto);
        teamBuildingRepository.updateTeamBuildingTitleById(teamBuildingDto.getTeamBuildingId(),modifiedTitle);
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

        //when
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(title);
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingDto.getTeamBuildingId());
        teamBuildingDto.setTeamDtoList(teamDtoList);
        teamBuildingRepository.updateTeamListInTeamBuilding(teamBuildingDto);
        teamBuildingRepository.deleteTeamBuildingById(teamBuildingDto.getTeamBuildingId());

        //then
        Assertions.assertFalse(teamBuildingRepository.isExistTeamBuilding(teamBuildingDto.getTeamBuildingId()));

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
        teamDtoList.add(createTeamDto(teambuildingId, 1, "team 1", List.of(userDtoList.get(0), userDtoList.get(2))));
        teamDtoList.add(createTeamDto(teambuildingId, 2, "team 2", List.of(userDtoList.get(3), userDtoList.get(4))));
        teamDtoList.add(createTeamDto(teambuildingId, 3, "team 3", List.of(userDtoList.get(1), userDtoList.get(5))));
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
