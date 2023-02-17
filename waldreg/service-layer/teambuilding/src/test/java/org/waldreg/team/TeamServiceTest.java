package org.waldreg.team;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
    TeamInTeamBuildingRepository teamBuildingRepository;

    @Test
    @DisplayName("새로운 팀 추가 성공 테스트")
    public void CREATE_NEW_TEAM_BUILDING_SUCCESS_TEST(){
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

}
