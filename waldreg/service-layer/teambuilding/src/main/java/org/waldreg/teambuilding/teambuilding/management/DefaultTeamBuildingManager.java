package org.waldreg.teambuilding.teambuilding.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.exception.ContentOverflowException;
import org.waldreg.teambuilding.exception.InvalidRangeException;
import org.waldreg.teambuilding.exception.InvalidTeamCountException;
import org.waldreg.teambuilding.exception.InvalidUserWeightException;
import org.waldreg.teambuilding.exception.UnknownTeamBuildingIdException;
import org.waldreg.teambuilding.exception.UnknownUserIdException;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingRequestDto;
import org.waldreg.teambuilding.teambuilding.dto.UserRequestDto;
import org.waldreg.teambuilding.teambuilding.management.teamcreator.TeamCreator;
import org.waldreg.teambuilding.teambuilding.management.teamcreator.TeamCreator.Team;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingRepository;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingUserRepository;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingsTeamRepository;

@Service
public class DefaultTeamBuildingManager implements TeamBuildingManager{

    private final TeamBuildingRepository teamBuildingRepository;
    private final TeamBuildingUserRepository teamBuildingUserRepository;
    private final TeamBuildingsTeamRepository teamBuildingsTeamRepository;
    private final TeamCreator teamCreator;

    @Autowired
    public DefaultTeamBuildingManager(TeamBuildingRepository teamBuildingRepository, TeamBuildingUserRepository teamBuildingUserRepository, TeamBuildingsTeamRepository teamBuildingsTeamRepository, TeamCreator teamCreator){
        this.teamBuildingRepository = teamBuildingRepository;
        this.teamBuildingUserRepository = teamBuildingUserRepository;
        this.teamBuildingsTeamRepository = teamBuildingsTeamRepository;
        this.teamCreator = teamCreator;
    }

    @Override
    public void createTeamBuilding(TeamBuildingRequestDto teamBuildingRequestDto){
        String teamBuildingTitle = teamBuildingRequestDto.getTeamBuildingTitle();
        int teamCount = teamBuildingRequestDto.getTeamCount();
        List<UserRequestDto> userRequestDtoList = teamBuildingRequestDto.getUserList();
        throwIfTeamBuildingTitleIsOverflow(teamBuildingTitle);
        throwIfTeamCountIsLessThanOrEqualToZero(teamCount);
        throwIfTeamCountExceedMemberCount(teamCount, userRequestDtoList.size());
        throwIfUserWeightIsOutOfRange(userRequestDtoList);
        throwIfUnknownUserIdDetected(userRequestDtoList);
        List<TeamDto> teamDtoList = createTeamDtoList(userRequestDtoList, teamCount);
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.createTeamBuilding(teamBuildingTitle);
        teamDtoList = createTeamInTeamDtoList(setTeamBuildingIdOfTeamDtoList(teamBuildingDto.getTeamBuildingId(), teamDtoList));
        teamBuildingRepository.updateTeamListInTeamBuilding(setTeamDtoListOfTeamBuilding(teamBuildingDto, teamDtoList));
    }

    private List<TeamDto> setTeamBuildingIdOfTeamDtoList(int teamBuildingId, List<TeamDto> teamDtoList){
        for (TeamDto teamDto : teamDtoList){
            teamDto.setTeamBuildingId(teamBuildingId);
        }
        return teamDtoList;
    }

    private List<TeamDto> createTeamInTeamDtoList(List<TeamDto> teamDtoList){
        List<TeamDto> storedTeamDtoList = new ArrayList<>();
        for (TeamDto teamDto : teamDtoList){
            storedTeamDtoList.add(teamBuildingsTeamRepository.createTeam(teamDto));
        }
        return storedTeamDtoList;
    }

    private TeamBuildingDto setTeamDtoListOfTeamBuilding(TeamBuildingDto teamBuildingDto, List<TeamDto> teamDtoList){
        teamBuildingDto.setTeamDtoList(teamDtoList);
        return teamBuildingDto;
    }

    private void throwIfTeamCountIsLessThanOrEqualToZero(int teamCount){
        if (teamCount <= 0){
            throw new InvalidTeamCountException("TEAMBUILDING-402", "The number of team cannot be less than or equal to zero, current team count \"" + teamCount + "\"");
        }
    }

    private void throwIfTeamCountExceedMemberCount(int teamCount, int memberCount){
        if (teamCount > memberCount){
            throw new InvalidTeamCountException("TEAMBUILDING-410", "Team count \"" + teamCount + "\" cannot exceed member count \"" + memberCount + "\"");
        }
    }

    private void throwIfUserWeightIsOutOfRange(List<UserRequestDto> userRequestDtoList){
        for (UserRequestDto userRequestDto : userRequestDtoList){
            if (!isUserWeightInRange(userRequestDto.getWeight())){
                throw new InvalidUserWeightException(userRequestDto.getWeight());
            }
        }
    }

    private boolean isUserWeightInRange(int weight){
        return weight >= 1 && weight <= 10;
    }

    private void throwIfUnknownUserIdDetected(List<UserRequestDto> userRequestDtoList){
        for (UserRequestDto userRequestDto : userRequestDtoList){
            if (!isExistUserId(userRequestDto.getUserId())){
                throw new UnknownUserIdException(userRequestDto.getUserId());
            }
        }
    }

    private boolean isExistUserId(String userId){
        return teamBuildingUserRepository.isExistUserByUserId(userId);
    }

    private List<TeamDto> createTeamDtoList(List<UserRequestDto> userRequestDtoList, int teamCount){
        List<TeamDto> teamDtoList = new ArrayList<>();
        Collections.sort(userRequestDtoList);
        List<Team> teamMemberList = teamCreator.createRandomTeamList(userRequestDtoList, teamCount);
        int teamNumber = 1;
        for (Team team : teamMemberList){
            teamDtoList.add(buildTeamDto(team.getMemberList(), teamNumber));
            teamNumber++;
        }
        return teamDtoList;
    }

    private TeamDto buildTeamDto(List<String> memberList, int teamNumber){
        return TeamDto.builder()
                .teamName("Team " + teamNumber)
                .userDtoList(buildUserDtoList(memberList))
                .build();
    }

    private List<UserDto> buildUserDtoList(List<String> memberList){
        List<UserDto> userDtoList = new ArrayList<>();
        for (String member : memberList){
            userDtoList.add(teamBuildingUserRepository.getUserInfoByUserId(member));
        }
        return userDtoList;
    }

    @Override
    public TeamBuildingDto readTeamBuildingById(int teamBuildingId){
        throwIfUnknownTeamBuildingId(teamBuildingId);
        return teamBuildingRepository.readTeamBuildingById(teamBuildingId);
    }

    @Override
    public List<TeamBuildingDto> readAllTeamBuilding(int startIdx, int endIdx){
        int maxIdx = teamBuildingRepository.readMaxIdx();
        throwIfInvalidRangeDetected(startIdx, endIdx);
        endIdx = adjustEndIdx(startIdx, endIdx, maxIdx);
        return teamBuildingRepository.readAllTeamBuilding(startIdx, endIdx);
    }

    private void throwIfInvalidRangeDetected(int startIdx, int endIdx){
        if (startIdx > endIdx || 1 > endIdx){
            throw new InvalidRangeException(startIdx, endIdx);
        }
    }

    private int adjustEndIdx(int startIdx, int endIdx, int maxIdx){
        endIdx = adjustEndIdxToMaxIdx(endIdx, maxIdx);
        endIdx = adjustEndIdxToPerPage(startIdx, endIdx);
        return endIdx;
    }

    private int adjustEndIdxToMaxIdx(int endIdx, int maxIdx){
        if (endIdx > maxIdx){
            return maxIdx;
        }
        return endIdx;
    }

    private int adjustEndIdxToPerPage(int startIdx, int endIdx){
        if (endIdx - startIdx + 1 > PerPage.PER_PAGE){
            return startIdx + PerPage.PER_PAGE - 1;
        }
        return endIdx;
    }

    @Override
    public void updateTeamBuildingTitleById(int teamBuildingId, String teamBuildingTitle){
        throwIfUnknownTeamBuildingId(teamBuildingId);
        throwIfTeamBuildingTitleIsOverflow(teamBuildingTitle);
        TeamBuildingDto teamBuildingDto = teamBuildingRepository.readTeamBuildingById(teamBuildingId);
        teamBuildingDto.setTeamBuildingTitle(teamBuildingTitle);
        teamBuildingRepository.updateTeamBuildingTitleById(teamBuildingId, teamBuildingTitle);
    }

    private void throwIfTeamBuildingTitleIsOverflow(String teamBuildingTitle){
        int length = teamBuildingTitle.length();
        if (length > 1000){
            throw new ContentOverflowException("TEAMBUILDING-401", "Teambuilding title cannot be more than 1000 current length \"" + length + "\"");
        }
    }

    @Override
    public void deleteTeamBuildingById(int teamBuildingId){
        throwIfUnknownTeamBuildingId(teamBuildingId);
        teamBuildingRepository.deleteTeamBuildingById(teamBuildingId);
    }

    @Override
    public int readMaxIdx(){
        return teamBuildingRepository.readMaxIdx();
    }

    private void throwIfUnknownTeamBuildingId(int teamBuildingId){
        if (!teamBuildingRepository.isExistTeamBuilding(teamBuildingId)){
            throw new UnknownTeamBuildingIdException(teamBuildingId);
        }
    }

}
