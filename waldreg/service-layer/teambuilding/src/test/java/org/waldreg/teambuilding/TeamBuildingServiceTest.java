package org.waldreg.teambuilding;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.teambuilding.dto.TeamBuildingRequestDto;
import org.waldreg.teambuilding.dto.UserRequestDto;
import org.waldreg.teambuilding.management.DefaultTeamBuildingManager;
import org.waldreg.teambuilding.management.TeamBuildingManager;
import org.waldreg.teambuilding.management.teamcreator.TeamCreator;
import org.waldreg.teambuilding.spi.TeamBuildingRepository;
import org.waldreg.teambuilding.spi.TeamBuildingUserRepository;
import org.waldreg.teambuilding.spi.TeamRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultTeamBuildingManager.class, TeamCreator.class})
public class TeamBuildingServiceTest{

    @Autowired
    TeamBuildingManager teamBuildingManager;

    @MockBean
    TeamBuildingRepository teamBuildingRepository;

    @MockBean
    TeamRepository teamRepository;

    @MockBean
    TeamBuildingUserRepository teamBuildingUserRepository;

    @Test
    @DisplayName("새로운 팀빌딩 그룹 생성 성공 테스트")
    public void CREATE_NEW_TEAM_BUILDING_SUCCESS_TEST(){
        //given
        String title = "2nd Algorithm Contest Team";
        int teamCount = 3;
        List<UserRequestDto> userRequestDtoList = createUserRequestDtoList();
        TeamBuildingRequestDto teamBuildingRequestDto = TeamBuildingRequestDto.builder()
                .teamBuildingTitle(title)
                .teamCount(teamCount)
                .userRequestDtoList(userRequestDtoList)
                .build();

        //then
        Assertions.assertDoesNotThrow(() -> teamBuildingManager.createTeamBuilding(teamBuildingRequestDto));

    }

    private List<UserRequestDto> createUserRequestDtoList(){
        List<UserRequestDto> userDtoList = new ArrayList<>();
        userDtoList.add(createUserRequestDto("alcuk_id", 2));
        userDtoList.add(createUserRequestDto("alcuk_id2", 1));
        userDtoList.add(createUserRequestDto("alcuk_id3", 3));
        userDtoList.add(createUserRequestDto("alcuk_id4", 4));
        userDtoList.add(createUserRequestDto("alcuk_id5", 2));
        userDtoList.add(createUserRequestDto("alcuk_id6",10));
        return userDtoList;
    }

    private UserRequestDto createUserRequestDto(String userId, int weight){
        return UserRequestDto.builder()
                .userId(userId)
                .weight(weight)
                .build();
    }


}
