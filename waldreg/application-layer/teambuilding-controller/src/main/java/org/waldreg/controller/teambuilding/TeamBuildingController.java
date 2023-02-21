package org.waldreg.controller.teambuilding;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.controller.teambuilding.mapper.ControllerTeamBuildingMapper;
import org.waldreg.controller.teambuilding.request.TeamBuildingRequest;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingRequestDto;
import org.waldreg.teambuilding.teambuilding.management.TeamBuildingManager;
import org.waldreg.token.aop.annotation.Authenticating;

@RestController
public class TeamBuildingController{

    private final TeamBuildingManager teamBuildingManager;

    private final ControllerTeamBuildingMapper controllerTeamBuildingMapper;


    public TeamBuildingController(TeamBuildingManager teamBuildingManager, ControllerTeamBuildingMapper controllerTeamBuildingMapper){
        this.teamBuildingManager = teamBuildingManager;
        this.controllerTeamBuildingMapper = controllerTeamBuildingMapper;
    }

    @Authenticating
    @PermissionVerifying(value = "Teambuilding create manager")
    @PostMapping("/teambuilding")
    public void createTeamBuilding(@RequestBody @Validated TeamBuildingRequest teamBuildingRequest){
        TeamBuildingRequestDto teamBuildingRequestDto = controllerTeamBuildingMapper.teamBuildingRequestToTeamBuildingDto(teamBuildingRequest);
        teamBuildingManager.createTeamBuilding(teamBuildingRequestDto);
    }


}