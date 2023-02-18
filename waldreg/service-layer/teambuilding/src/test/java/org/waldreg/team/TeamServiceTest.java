package org.waldreg.team;

import java.time.LocalDateTime;
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
import org.waldreg.team.dto.TeamDto;
import org.waldreg.team.dto.TeamRequestDto;
import org.waldreg.team.management.DefaultTeamManager;
import org.waldreg.team.management.TeamManager;
import org.waldreg.team.spi.TeamInTeamBuildingRepository;
import org.waldreg.team.spi.TeamRepository;
import org.waldreg.team.spi.TeamUserRepository;

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

        //when&then
        Assertions.assertDoesNotThrow(() -> teamManager.createTeam(teamBuildingId, teamRequestDto));

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


}
