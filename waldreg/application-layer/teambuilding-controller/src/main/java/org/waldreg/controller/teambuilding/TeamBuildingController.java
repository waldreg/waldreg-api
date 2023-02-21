package org.waldreg.controller.teambuilding;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.character.aop.behavior.VerifyingFailBehavior;
import org.waldreg.character.aop.parameter.PermissionVerifyState;
import org.waldreg.character.exception.NoPermissionException;
import org.waldreg.controller.teambuilding.mapper.ControllerTeamBuildingMapper;
import org.waldreg.controller.teambuilding.request.TeamBuildingRequest;
import org.waldreg.controller.teambuilding.request.TeamBuildingUpdateRequest;
import org.waldreg.controller.teambuilding.request.TeamRequest;
import org.waldreg.controller.teambuilding.request.TeamUpdateRequest;
import org.waldreg.controller.teambuilding.response.TeamBuildingListResponse;
import org.waldreg.controller.teambuilding.response.TeamBuildingResponse;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.team.dto.TeamRequestDto;
import org.waldreg.teambuilding.team.management.TeamManager;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingRequestDto;
import org.waldreg.teambuilding.teambuilding.management.PerPage;
import org.waldreg.teambuilding.teambuilding.management.TeamBuildingManager;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.util.token.DecryptedTokenContextGetter;

@RestController
public class TeamBuildingController{

    private final TeamBuildingManager teamBuildingManager;

    private final TeamManager teamManager;

    private final ControllerTeamBuildingMapper controllerTeamBuildingMapper;

    private final DecryptedTokenContextGetter decryptedTokenContextGetter;

    @Autowired
    public TeamBuildingController(TeamBuildingManager teamBuildingManager, TeamManager teamManager, ControllerTeamBuildingMapper controllerTeamBuildingMapper, DecryptedTokenContextGetter decryptedTokenContextGetter){
        this.teamBuildingManager = teamBuildingManager;
        this.teamManager = teamManager;
        this.controllerTeamBuildingMapper = controllerTeamBuildingMapper;
        this.decryptedTokenContextGetter = decryptedTokenContextGetter;
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

    @Authenticating
    @GetMapping("/teambuilding/{teambuilding-id}")
    public TeamBuildingResponse readTeamBuildingByTeamBuildingId(@PathVariable("teambuilding-id") int teamBuildingId){
        TeamBuildingDto teamBuildingDto = teamBuildingManager.readTeamBuildingById(teamBuildingId);
        return controllerTeamBuildingMapper.teamBuildingDtoToTeamBuildingResponse(teamBuildingDto);
    }

    @Authenticating
    @RequestMapping("/teambuilding")
    public TeamBuildingListResponse readAllTeamBuilding(@RequestParam(value = "from", required = false) int startIdx, @RequestParam(value = "to", required = false) int endIdx){
        if (isInvalidRange(startIdx, endIdx)){
            startIdx = 1;
            endIdx = PerPage.PER_PAGE;
        }
        int maxIdx = teamBuildingManager.readMaxIdx();
        List<TeamBuildingDto> teamBuildingDtoList = teamBuildingManager.readAllTeamBuilding(startIdx, endIdx);
        List<TeamBuildingResponse> teamBuildingResponseList = controllerTeamBuildingMapper.teamBuildingDtoListToTeamBuildingResponseList(teamBuildingDtoList);
        return controllerTeamBuildingMapper.createTeamBuildingListResponse(maxIdx, teamBuildingResponseList);
    }

    private boolean isInvalidRange(Integer from, Integer to){
        return from == null || to == null;
    }

    @Authenticating
    @PermissionVerifying(value = "Teambuilding update manager")
    @PutMapping("/teambuilding/{team-id}")
    public void updateTeamByTeamId(@PathVariable("team-id") int teamId, @RequestBody TeamRequest teamRequest){
        TeamRequestDto teamRequestDto = controllerTeamBuildingMapper.teamRequestToTeamRequestDto(teamRequest);
        teamManager.updateTeamById(teamId, teamRequestDto);
    }

    @Authenticating
    @PermissionVerifying(value = "Teambuilding update manager")
    @PatchMapping("/teambuilding/{teambuilding-id}")
    public void updateTeamBuildingByTeamBuildingId(@PathVariable("teambuilding-id") int teamBuildingId, @RequestBody TeamBuildingUpdateRequest teamBuildingUpdateRequest){
        teamBuildingManager.updateTeamBuildingTitleById(teamBuildingId, teamBuildingUpdateRequest.getTeamBuildingTitle());
    }

    @Authenticating
    @PermissionVerifying(value = "Teambuilding update manager", fail = VerifyingFailBehavior.PASS)
    @PatchMapping("/teambuilding/{team-id}")
    public void updateTeamNameByTeamId(@PathVariable("team-id") int teamId, @RequestBody TeamUpdateRequest teamUpdateRequest, @Nullable PermissionVerifyState permissionVerifyState){
        int id = decryptedTokenContextGetter.get();
        List<UserDto> userDtoList = teamManager.readTeamById(teamId).getUserList();
        if (permissionVerifyState.isVerified() || userDtoList.stream().anyMatch(i -> i.getId() == id)){
            teamManager.updateTeamNameById(teamId, teamUpdateRequest.getTeamName());
        }
        throw new NoPermissionException();
    }

    @Authenticating
    @PermissionVerifying(value = "Teambuilding delete manager")
    @DeleteMapping("/teambuilding/{teambuilding-id}")
    public void deleteTeamBuildingByTeamBuildingId(@PathVariable("teambuilding-id") int teamBuildingId){
        teamBuildingManager.deleteTeamBuildingById(teamBuildingId);
    }

    @Authenticating
    @PermissionVerifying(value = "Teambuilding delete manager")
    @DeleteMapping("/teambuilding/{team-id}")
    public void deleteTeamByTeamId(@PathVariable("team-id") int teamId){
        teamManager.deleteTeamById(teamId);
    }

}