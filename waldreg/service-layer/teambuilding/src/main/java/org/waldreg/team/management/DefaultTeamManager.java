package org.waldreg.team.management;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.exception.*;
import org.waldreg.team.dto.TeamDto;
import org.waldreg.team.dto.TeamRequestDto;
import org.waldreg.team.dto.UserDto;
import org.waldreg.team.spi.TeamInTeamBuildingRepository;
import org.waldreg.team.spi.TeamRepository;
import org.waldreg.team.spi.TeamUserRepository;

@Service
public class DefaultTeamManager implements TeamManager{

    private final TeamRepository teamRepository;

    private final TeamUserRepository teamUserRepository;

    private final TeamInTeamBuildingRepository teamInTeamBuildingRepository;

    @Autowired
    public DefaultTeamManager(TeamRepository teamRepository, TeamUserRepository teamUserRepository, TeamInTeamBuildingRepository teamInTeamBuildingRepository){
        this.teamRepository = teamRepository;
        this.teamUserRepository = teamUserRepository;
        this.teamInTeamBuildingRepository = teamInTeamBuildingRepository;
    }

    @Override
    public void createTeam(int teamBuildingId, TeamRequestDto teamRequestDto){
        throwIfUnknownTeamBuildingId(teamBuildingId);
        throwIfDuplicatedTeamName(teamBuildingId, teamRequestDto.getTeamName());
        throwIfUnknownUserIdDetected(teamRequestDto.getMemberList());
        throwIfAlreadyInTeamUser(teamBuildingId, teamRequestDto.getMemberList());
        TeamDto teamDto = buildTeamDto(teamBuildingId, teamRequestDto);
        teamRepository.createTeam(teamDto);
    }

    private void throwIfUnknownTeamBuildingId(int teamBuildingId){
        if (!teamInTeamBuildingRepository.isExistTeamBuilding(teamBuildingId)){
            throw new UnknownTeamBuildingIdException(teamBuildingId);
        }
    }

    private TeamDto buildTeamDto(int teamBuildingId, TeamRequestDto teamRequestDto){
        return TeamDto.builder()
                .teamBuildingId(teamBuildingId)
                .teamName(teamRequestDto.getTeamName())
                .userDtoList(buildUserDtoList(teamRequestDto.getMemberList()))
                .build();
    }

    private List<UserDto> buildUserDtoList(List<String> memberList){
        List<UserDto> userDtoList = new ArrayList<>();
        for (String member : memberList){
            userDtoList.add(teamUserRepository.getUserInfoByUserId(member));
        }
        return userDtoList;
    }

    @Override
    public void updateTeamById(int teamId, TeamRequestDto teamRequestDto){
        throwIfUnknownTeamId(teamId);
        TeamDto teamDto = teamRepository.readTeamById(teamId);
        throwIfTeamNameIsOverflow(teamRequestDto.getTeamName());
        throwIfDuplicatedTeamName(teamDto.getTeamBuildingId(), teamRequestDto.getTeamName());
        throwIfUnknownUserIdDetected(teamRequestDto.getMemberList());
        throwIfAlreadyInTeamUser(teamDto.getTeamBuildingId(), teamRequestDto.getMemberList());
        teamDto = updateTeamDto(teamDto, teamRequestDto);
        teamRepository.updateTeamById(teamId, teamDto);
    }

    private void throwIfUnknownUserIdDetected(List<String> userIdList){
        for (String userId : userIdList){
            if (!isExistUserId(userId)){
                throw new UnknownUserIdException(userId);
            }
        }
    }

    private boolean isExistUserId(String userId){
        return teamUserRepository.isExistUserByUserId(userId);
    }

    private void throwIfAlreadyInTeamUser(int teamBuildingId, List<String> userIdList){
        List<UserDto> userDtoList = teamRepository.readAllUserByTeamBuildingId(teamBuildingId);
        for (UserDto userDto : userDtoList){
            for (String userId : userIdList){
                if (userDto.getUserId().equals(userId)){
                    throw new DuplicateUserSelectException(userId);
                }
            }
        }
    }

    private TeamDto updateTeamDto(TeamDto teamDto, TeamRequestDto teamRequestDto){
        teamDto.setTeamName(teamRequestDto.getTeamName());
        teamDto.setUserDtoList(buildUserDtoList(teamRequestDto.getMemberList()));
        return teamDto;
    }

    @Override
    public TeamDto readTeamById(int teamId){
        return teamRepository.readTeamById(teamId);
    }

    @Override
    public void updateTeamNameById(int teamId, String teamName){
        throwIfUnknownTeamId(teamId);
        throwIfTeamNameIsOverflow(teamName);
        TeamDto teamDto = teamRepository.readTeamById(teamId);
        throwIfDuplicatedTeamName(teamDto.getTeamBuildingId(), teamName);
        teamDto.setTeamName(teamName);
        teamRepository.updateTeamById(teamId, teamDto);
    }

    private void throwIfTeamNameIsOverflow(String teamName){
        int length = teamName.length();
        if(length > 1000){
            throw new ContentOverflowException("TEAMBUILDING-411", "Team name cannot be more than 1000 current length \"" + length + "\"");
        }
    }

    private void throwIfDuplicatedTeamName(int teamBuildingId, String teamName){
        List<TeamDto> teamDtoList = teamRepository.readAllTeamByTeamBuildingId(teamBuildingId);
        for (TeamDto teamDto : teamDtoList){
            if (teamDto.getTeamName().equals(teamName)){
                throw new DuplicatedTeamNameException(teamName);
            }
        }
    }

    @Override
    public void deleteTeamById(int teamId){
        throwIfUnknownTeamId(teamId);
        teamRepository.deleteTeamById(teamId);
    }

    private void throwIfUnknownTeamId(int teamId){
        if(!isExistTeam(teamId)){
            throw new UnknownTeamIdException(teamId);
        }
    }

    private boolean isExistTeam(int teamId){return teamRepository.isExistTeam(teamId);}

}
