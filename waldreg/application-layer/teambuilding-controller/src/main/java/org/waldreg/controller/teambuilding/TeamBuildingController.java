package org.waldreg.controller.teambuilding;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.controller.teambuilding.mapper.ControllerTeamBuildingMapper;
import org.waldreg.teambuilding.teambuilding.management.TeamBuildingManager;

@RestController
public class TeamBuildingController{

    private final TeamBuildingManager teamBuildingManager;

    private final ControllerTeamBuildingMapper controllerTeamBuildingMapper;


    public TeamBuildingController(TeamBuildingManager teamBuildingManager, ControllerTeamBuildingMapper controllerTeamBuildingMapper){
        this.teamBuildingManager = teamBuildingManager;
        this.controllerTeamBuildingMapper = controllerTeamBuildingMapper;
    }



}