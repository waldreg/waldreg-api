package org.waldreg.controller.teambuilding;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.controller.teambuilding.mapper.ControllerTeamBuildingMapper;
import org.waldreg.controller.teambuilding.request.TeamBuildingRequest;
import org.waldreg.controller.teambuilding.request.TeamRequest;
import org.waldreg.teambuilding.team.dto.TeamRequestDto;
import org.waldreg.teambuilding.team.management.TeamManager;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingRequestDto;
import org.waldreg.teambuilding.teambuilding.management.TeamBuildingManager;
import org.waldreg.token.aop.annotation.Authenticating;

@RestController
public class TeamBuildingController{

    private final TeamBuildingManager teamBuildingManager;

    private final TeamManager teamManager;

    private final ControllerTeamBuildingMapper controllerTeamBuildingMapper;


    public TeamBuildingController(TeamBuildingManager teamBuildingManager, TeamManager teamManager, ControllerTeamBuildingMapper controllerTeamBuildingMapper){
        this.teamBuildingManager = teamBuildingManager;
        this.teamManager = teamManager;
        this.controllerTeamBuildingMapper = controllerTeamBuildingMapper;
    }

    @Authenticating
    @PermissionVerifying(value = "Teambuilding create manager")
    @PostMapping("/teambuilding")
    public void createTeamBuilding(@RequestBody @Validated TeamBuildingRequest teamBuildingRequest){
        TeamBuildingRequestDto teamBuildingRequestDto = controllerTeamBuildingMapper.teamBuildingRequestToTeamBuildingDto(teamBuildingRequest);
        teamBuildingManager.createTeamBuilding(teamBuildingRequestDto);
    }

    @Authenticating
    @PermissionVerifying(value = "Teambuilding create manager")
    @PostMapping("/teambuilding/{teambuilding-id}")
    public void createTeam(@PathVariable("teambuilding-id") int teamBuildingId, @RequestBody @Validated TeamRequest teamRequest){
        TeamRequestDto teamRequestDto = controllerTeamBuildingMapper.teamRequestToTeamRequestDto(teamRequest);
        teamManager.createTeam(teamBuildingId, teamRequestDto);
    }


}