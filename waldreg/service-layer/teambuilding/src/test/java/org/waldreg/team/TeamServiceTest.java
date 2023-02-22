package org.waldreg.team;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.exception.ContentOverflowException;
import org.waldreg.teambuilding.exception.DuplicateUserSelectException;
import org.waldreg.teambuilding.exception.DuplicatedTeamNameException;
import org.waldreg.teambuilding.exception.UnknownTeamBuildingIdException;
import org.waldreg.teambuilding.exception.UnknownTeamIdException;
import org.waldreg.teambuilding.exception.UnknownUserIdException;
import org.waldreg.teambuilding.team.dto.TeamRequestDto;
import org.waldreg.teambuilding.team.management.DefaultTeamManager;
import org.waldreg.teambuilding.team.management.TeamManager;
import org.waldreg.teambuilding.team.spi.TeamInTeamBuildingRepository;
import org.waldreg.teambuilding.team.spi.TeamRepository;
import org.waldreg.teambuilding.team.spi.TeamUserRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultTeamManager.class})
public class TeamServiceTest{

    @Autowired
    TeamManager teamManager;

    @MockBean
    TeamRepository teamRepository;

    @MockBean
    TeamUserRepository teamUserRepository;

    @MockBean
    TeamInTeamBuildingRepository teamInTeamBuildingRepository;

    @Test
    @DisplayName("팀빌딩 그룹 내 새로운 팀 추가 성공 테스트")
    public void CREATE_NEW_TEAM_IN_TEAM_BUILDING_SUCCESS_TEST(){
        //given
        int teamBuildingId = 1;
        String name = "new team";
        TeamRequestDto teamRequestDto = TeamRequestDto.builder()
                .teamName(name)
                .memberList(List.of())
                .build();

        //when
        Mockito.when(teamInTeamBuildingRepository.isExistTeamBuilding(Mockito.anyInt())).thenReturn(true);

        //then
        Assertions.assertDoesNotThrow(() -> teamManager.createTeam(teamBuildingId, teamRequestDto));

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 새로운 팀 추가 실패 테스트 - 없는 teambuilding_id")
    public void CREATE_NEW_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_UNKNOWN_TEAM_BUILDING_ID_TEST(){
        //given
        int teamBuildingId = 1;
        String name = "new team";
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingId);
        TeamRequestDto teamRequestDto = TeamRequestDto.builder()
                .teamName(name)
                .memberList(List.of())
                .build();

        //when
        Mockito.when(teamInTeamBuildingRepository.isExistTeamBuilding(Mockito.anyInt())).thenReturn(false);
        Mockito.when(teamRepository.readAllTeamByTeamBuildingId(Mockito.anyInt())).thenReturn(teamDtoList);

        //then
        Assertions.assertThrows(UnknownTeamBuildingIdException.class, () -> teamManager.createTeam(teamBuildingId, teamRequestDto));

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 새로운 팀 추가 실패 테스트 - teamName 중복")
    public void CREATE_NEW_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_DUPLICATED_TEAM_NAME_TEST(){
        //given
        int teamBuildingId = 1;
        String name = "team 1";
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingId);
        TeamRequestDto teamRequestDto = TeamRequestDto.builder()
                .teamName(name)
                .memberList(List.of())
                .build();

        //when
        Mockito.when(teamInTeamBuildingRepository.isExistTeamBuilding(Mockito.anyInt())).thenReturn(true);
        Mockito.when(teamRepository.readAllTeamByTeamBuildingId(Mockito.anyInt())).thenReturn(teamDtoList);

        //then
        Assertions.assertThrows(DuplicatedTeamNameException.class, () -> teamManager.createTeam(teamBuildingId, teamRequestDto));

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 새로운 팀 추가 실패 테스트 - 이미 같은 팀빌딩 내 다른 팀에 속한 user 포함")
    public void CREATE_NEW_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_USER_ALREADY_IN_TEAM_TEST(){
        //given
        int teamBuildingId = 1;
        String name = "new team";
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingId);
        List<UserDto> userDtoList = createUserDtoList();
        TeamRequestDto teamRequestDto = TeamRequestDto.builder()
                .teamName(name)
                .memberList(List.of("alcuk_id"))
                .build();

        //when
        Mockito.when(teamInTeamBuildingRepository.isExistTeamBuilding(Mockito.anyInt())).thenReturn(true);
        Mockito.when(teamRepository.readAllTeamByTeamBuildingId(Mockito.anyInt())).thenReturn(teamDtoList);
        Mockito.when(teamUserRepository.isExistUserByUserId(Mockito.anyString())).thenReturn(true);
        Mockito.when(teamRepository.readAllUserByTeamBuildingId(Mockito.anyInt())).thenReturn(userDtoList);

        //then
        Assertions.assertThrows(DuplicateUserSelectException.class, () -> teamManager.createTeam(teamBuildingId, teamRequestDto));

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 새로운 팀 추가 실패 테스트 - 없는 user_id")
    public void CREATE_NEW_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_UNKNOWN_USER_ID_TEST(){
        //given
        int teamBuildingId = 1;
        String name = "new team";
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingId);
        TeamRequestDto teamRequestDto = TeamRequestDto.builder()
                .teamName(name)
                .memberList(List.of("unknown_id"))
                .build();

        //when
        Mockito.when(teamInTeamBuildingRepository.isExistTeamBuilding(Mockito.anyInt())).thenReturn(true);
        Mockito.when(teamUserRepository.isExistUserByUserId(Mockito.anyString())).thenReturn(false);
        Mockito.when(teamRepository.readAllTeamByTeamBuildingId(Mockito.anyInt())).thenReturn(teamDtoList);

        //then
        Assertions.assertThrows(UnknownUserIdException.class, () -> teamManager.createTeam(teamBuildingId, teamRequestDto));

    }


    @Test
    @DisplayName("팀빌딩 그룹 내 팀 수정 성공 테스트")
    public void MODIFY_TEAM_IN_TEAM_BUILDING_SUCCESS_TEST(){
        //given
        int teamBuildingId = 1;
        int teamId = 1;
        String name = "new team";
        TeamDto teamDto = TeamDto.builder()
                .teamBuildingId(teamBuildingId)
                .teamId(teamId)
                .teamName(name)
                .userDtoList(List.of())
                .lastModifiedAt(LocalDateTime.now())
                .build();
        TeamRequestDto teamRequestDto = TeamRequestDto.builder()
                .teamName("modifiedName")
                .memberList(List.of())
                .build();

        //when
        Mockito.when(teamRepository.isExistTeam(Mockito.anyInt())).thenReturn(true);
        Mockito.when(teamRepository.readTeamById(Mockito.anyInt())).thenReturn(teamDto);
        teamManager.updateTeamById(teamId, teamRequestDto);
        teamDto.setTeamName("modifiedName");
        TeamDto result = teamManager.readTeamById(teamId);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(teamDto.getTeamBuildingId(), result.getTeamBuildingId()),
                () -> Assertions.assertEquals(teamDto.getTeamId(), result.getTeamId()),
                () -> Assertions.assertEquals(teamDto.getTeamName(), result.getTeamName()),
                () -> Assertions.assertEquals(teamDto.getLastModifiedAt(), result.getLastModifiedAt()),
                () -> Assertions.assertEquals(teamDto.getUserList(), result.getUserList())
        );

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 수정 실패 테스트 - 이미 다른 팀에 존재하는 user 추가 시도")
    public void MODIFY_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_USER_ALREADY_IN_OTHER_TEAM_TEST(){
        //given
        int teamBuildingId = 1;
        int teamId = 1;
        String name = "new team";
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingId);
        List<UserDto> userDtoList = createUserDtoList();
        TeamRequestDto teamRequestDto = TeamRequestDto.builder()
                .teamName(name)
                .memberList(List.of("alcuk_id"))
                .build();

        //when
        Mockito.when(teamRepository.isExistTeam(Mockito.anyInt())).thenReturn(true);
        Mockito.when(teamRepository.readTeamById(Mockito.anyInt())).thenReturn(teamDtoList.get(0));
        Mockito.when(teamRepository.readAllTeamByTeamBuildingId(Mockito.anyInt())).thenReturn(teamDtoList);
        Mockito.when(teamUserRepository.isExistUserByUserId(Mockito.anyString())).thenReturn(true);
        Mockito.when(teamRepository.readAllUserByTeamBuildingId(Mockito.anyInt())).thenReturn(userDtoList);

        //then
        Assertions.assertThrows(DuplicateUserSelectException.class, () -> teamManager.updateTeamById(teamId, teamRequestDto));

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 수정 실패 테스트 - 중복된 team 이름")
    public void MODIFY_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_DUPLICATED_TEAM_NAME_TEST(){
        //given
        int teamBuildingId = 1;
        int teamId = 1;
        String name = "team 1";
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingId);
        TeamRequestDto teamRequestDto = TeamRequestDto.builder()
                .teamName(name)
                .memberList(List.of())
                .build();

        //when
        Mockito.when(teamRepository.isExistTeam(Mockito.anyInt())).thenReturn(true);
        Mockito.when(teamRepository.readTeamById(Mockito.anyInt())).thenReturn(teamDtoList.get(0));
        Mockito.when(teamRepository.readAllTeamByTeamBuildingId(Mockito.anyInt())).thenReturn(teamDtoList);

        //then
        Assertions.assertThrows(DuplicatedTeamNameException.class, () -> teamManager.updateTeamById(teamId, teamRequestDto));

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 수정 실패 테스트 - 없는 team_id")
    public void MODIFY_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_UNKNOWN_TEAM_ID_TEST(){
        //given
        int teamBuildingId = 1;
        int teamId = 0;
        String name = "new team";
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingId);
        TeamRequestDto teamRequestDto = TeamRequestDto.builder()
                .teamName(name)
                .memberList(List.of())
                .build();

        //when
        Mockito.when(teamRepository.isExistTeam(Mockito.anyInt())).thenReturn(false);
        Mockito.when(teamRepository.readTeamById(Mockito.anyInt())).thenReturn(teamDtoList.get(0));
        Mockito.when(teamRepository.readAllTeamByTeamBuildingId(Mockito.anyInt())).thenReturn(teamDtoList);

        //then
        Assertions.assertThrows(UnknownTeamIdException.class, () -> teamManager.updateTeamById(teamId, teamRequestDto));

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 수정 실패 테스트 - team_name이 1000자 초과")
    public void MODIFY_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_TEAM_NAME_OVERFLOW_TEST(){
        //given
        int teamBuildingId = 1;
        int teamId = 0;
        String name = createOverflow();
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingId);
        TeamRequestDto teamRequestDto = TeamRequestDto.builder()
                .teamName(name)
                .memberList(List.of())
                .build();

        //when
        Mockito.when(teamRepository.isExistTeam(Mockito.anyInt())).thenReturn(true);
        Mockito.when(teamRepository.readTeamById(Mockito.anyInt())).thenReturn(teamDtoList.get(0));
        Mockito.when(teamRepository.readAllTeamByTeamBuildingId(Mockito.anyInt())).thenReturn(teamDtoList);

        //then
        Assertions.assertThrows(ContentOverflowException.class, () -> teamManager.updateTeamById(teamId, teamRequestDto));

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 수정 실패 테스트 - 없는 user_id")
    public void MODIFY_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_UNKNOWN_USER_ID_TEST(){
        //given
        int teamBuildingId = 1;
        int teamId = 0;
        String name = "new team";
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingId);
        TeamRequestDto teamRequestDto = TeamRequestDto.builder()
                .teamName(name)
                .memberList(List.of())
                .build();

        //when
        Mockito.when(teamRepository.isExistTeam(Mockito.anyInt())).thenReturn(false);
        Mockito.when(teamRepository.readTeamById(Mockito.anyInt())).thenReturn(teamDtoList.get(0));
        Mockito.when(teamRepository.readAllTeamByTeamBuildingId(Mockito.anyInt())).thenReturn(teamDtoList);

        //then
        Assertions.assertThrows(UnknownTeamIdException.class, () -> teamManager.updateTeamById(teamId, teamRequestDto));

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 이름 수정 성공 테스트")
    public void MODIFY_TEAM_NAME_IN_TEAM_BUILDING_SUCCESS_TEST(){
        //given
        int teamBuildingId = 1;
        int teamId = 1;
        String name = "new team";
        TeamDto teamDto = TeamDto.builder()
                .teamBuildingId(teamBuildingId)
                .teamId(teamId)
                .teamName(name)
                .userDtoList(List.of())
                .lastModifiedAt(LocalDateTime.now())
                .build();
        String updateName = "modifiedName";

        //when
        Mockito.when(teamRepository.isExistTeam(Mockito.anyInt())).thenReturn(true);
        Mockito.when(teamRepository.readTeamById(Mockito.anyInt())).thenReturn(teamDto);
        teamManager.updateTeamNameById(teamId, updateName);
        teamDto.setTeamName(updateName);
        TeamDto result = teamManager.readTeamById(teamId);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(teamDto.getTeamBuildingId(), result.getTeamBuildingId()),
                () -> Assertions.assertEquals(teamDto.getTeamId(), result.getTeamId()),
                () -> Assertions.assertEquals(teamDto.getTeamName(), result.getTeamName()),
                () -> Assertions.assertEquals(teamDto.getLastModifiedAt(), result.getLastModifiedAt()),
                () -> Assertions.assertEquals(teamDto.getUserList(), result.getUserList())
        );

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 이름 수정 실패 테스트 - 없는 team_id")
    public void MODIFY_TEAM_NAME_IN_TEAM_BUILDING_FAIL_CAUSE_UNKNOWN_TEAM_ID_TEST(){
        //given
        int teamBuildingId = 1;
        int teamId = 0;
        String name = "new team";
        TeamDto teamDto = TeamDto.builder()
                .teamBuildingId(teamBuildingId)
                .teamId(teamId)
                .teamName(name)
                .userDtoList(List.of())
                .lastModifiedAt(LocalDateTime.now())
                .build();
        String updateName = "modifiedName";

        //when
        Mockito.when(teamRepository.isExistTeam(Mockito.anyInt())).thenReturn(false);
        Mockito.when(teamRepository.readTeamById(Mockito.anyInt())).thenReturn(teamDto);

        //then
        Assertions.assertThrows(UnknownTeamIdException.class, () -> teamManager.updateTeamNameById(teamId, updateName));

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 이름 수정 실패 테스트 - 중복된 team_name")
    public void MODIFY_TEAM_NAME_IN_TEAM_BUILDING_FAIL_CAUSE_DUPLICATED_TEAM_NAME_TEST(){
        //given
        int teamBuildingId = 1;
        int teamId = 1;
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingId);
        String updateName = "team 1";

        //when
        Mockito.when(teamRepository.isExistTeam(Mockito.anyInt())).thenReturn(true);
        Mockito.when(teamRepository.readTeamById(Mockito.anyInt())).thenReturn(teamDtoList.get(0));
        Mockito.when(teamRepository.readAllTeamByTeamBuildingId(Mockito.anyInt())).thenReturn(teamDtoList);

        //then
        Assertions.assertThrows(DuplicatedTeamNameException.class, () -> teamManager.updateTeamNameById(teamId, updateName));

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 이름 수정 실패 테스트 - team_name 1000자 초과")
    public void MODIFY_TEAM_NAME_IN_TEAM_BUILDING_FAIL_CAUSE_TEAM_NAME_OVERFLOW_TEST(){
        //given
        int teamBuildingId = 1;
        int teamId = 1;
        List<TeamDto> teamDtoList = createTeamDtoList(teamBuildingId);
        String updateName = createOverflow();

        //when
        Mockito.when(teamRepository.isExistTeam(Mockito.anyInt())).thenReturn(true);
        Mockito.when(teamRepository.readTeamById(Mockito.anyInt())).thenReturn(teamDtoList.get(0));

        //then
        Assertions.assertThrows(ContentOverflowException.class, () -> teamManager.updateTeamNameById(teamId, updateName));

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 삭제 성공 테스트")
    public void DELETE_TEAM_IN_TEAM_BUILDING_SUCCESS_TEST(){
        //given
        int teamId = 1;

        //when
        Mockito.when(teamRepository.isExistTeam(Mockito.anyInt())).thenReturn(true);

        //then
        Assertions.assertDoesNotThrow(() -> teamManager.deleteTeamById(teamId));

    }

    @Test
    @DisplayName("팀빌딩 그룹 내 팀 삭제 실패 테스트 - 없는 team_id")
    public void DELETE_TEAM_IN_TEAM_BUILDING_FAIL_CAUSE_UNKNOWN_TEAM_ID_TEST(){
        //given
        int teamId = 1;

        //when
        Mockito.when(teamRepository.isExistTeam(Mockito.anyInt())).thenReturn(false);

        //then
        Assertions.assertThrows(UnknownTeamIdException.class, () -> teamManager.deleteTeamById(teamId));

    }


    private List<TeamDto> createTeamDtoList(int teamBuildingId){
        List<TeamDto> teamDtoList = new ArrayList<>();
        List<UserDto> userDtoList = createUserDtoList();
        teamDtoList.add(createTeamDto(teamBuildingId, "team 1", List.of(userDtoList.get(0), userDtoList.get(2))));
        teamDtoList.add(createTeamDto(teamBuildingId, "team 2", List.of(userDtoList.get(3), userDtoList.get(4))));
        teamDtoList.add(createTeamDto(teamBuildingId, "team 3", List.of(userDtoList.get(1), userDtoList.get(5))));
        return teamDtoList;
    }

    private List<UserDto> createUserDtoList(){
        List<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(createUserDto("alcuk_id"));
        userDtoList.add(createUserDto("alcuk_id2"));
        userDtoList.add(createUserDto("alcuk_id3"));
        userDtoList.add(createUserDto("alcuk_id4"));
        userDtoList.add(createUserDto("alcuk_id5"));
        userDtoList.add(createUserDto("alcuk_id6"));
        return userDtoList;
    }

    private TeamDto createTeamDto(int teamBuildingId, String teamName, List<UserDto> userDtoList){
        return TeamDto.builder()
                .teamName(teamName)
                .teamBuildingId(teamBuildingId)
                .lastModifiedAt(LocalDateTime.now())
                .userDtoList(userDtoList)
                .build();
    }

    private UserDto createUserDto(String userId){
        return UserDto.builder()
                .userId(userId)
                .name("name")
                .build();
    }

    private String createOverflow(){
        String title = "";
        for (int i = 0; i < 1005; i++){
            title = title + "A";
        }
        return title;
    }

}
