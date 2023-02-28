package org.waldreg.repository.teambuilding.team;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.teambuilding.TeamUser;
import org.waldreg.domain.user.User;
import org.waldreg.repository.teambuilding.repository.JpaTeamUserRepository;
import org.waldreg.repository.teambuilding.team.mapper.TeamRepositoryMapper;
import org.waldreg.repository.teambuilding.team.repository.JpaTeamRepository;
import org.waldreg.repository.teambuilding.teambuilding.repository.JpaTeamBuildingRepository;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.team.spi.TeamRepository;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingsTeamRepository;

@Repository
public class TeamRepositoryServiceProvider implements TeamRepository, TeamBuildingsTeamRepository{

    private final TeamRepositoryMapper teamRepositoryMapper;

    private final JpaTeamRepository jpaTeamRepository;

    private final JpaTeamBuildingRepository jpaTeamBuildingRepository;

    private final JpaTeamUserRepository jpaTeamUserRepository;

    public TeamRepositoryServiceProvider(TeamRepositoryMapper teamRepositoryMapper, JpaTeamRepository jpaTeamRepository, JpaTeamBuildingRepository jpaTeamBuildingRepository, JpaTeamUserRepository jpaTeamUserRepository){
        this.teamRepositoryMapper = teamRepositoryMapper;
        this.jpaTeamRepository = jpaTeamRepository;
        this.jpaTeamBuildingRepository = jpaTeamBuildingRepository;
        this.jpaTeamUserRepository = jpaTeamUserRepository;
    }

    @Override
    @Transactional
    public TeamDto createTeam(TeamDto teamDto){
        Team team = teamRepositoryMapper.teamDtoToTeam(teamDto);
        team.setTeamBuilding(jpaTeamBuildingRepository.findById(teamDto.getTeamBuildingId()).get());
        Team savedTeam = jpaTeamRepository.save(team);
        teamDto.getUserList().stream().forEach(userDto -> savedTeam.addTeamUser(jpaTeamUserRepository.findById(userDto.getId()).get()));
        return teamRepositoryMapper.teamToTeamDto(savedTeam);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamDto readTeamById(int teamId){
        Team team = jpaTeamRepository.findById(teamId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find team with id \"" + teamId + "\"");}
        );
        return teamRepositoryMapper.teamToTeamDto(team);
    }

    @Override
    @Transactional
    public void updateTeamById(int teamId, TeamDto teamDto){
        Team team = jpaTeamRepository.findById(teamId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find team with id \"" + teamId + "\"");}
        );
        team.setTeamName(teamDto.getTeamName());
        team.setLastModifiedAt(LocalDateTime.now());
        setTeamUserList(teamDto.getUserList(), team);
        jpaTeamRepository.save(team);
    }

    private void setTeamUserList(List<UserDto> userDtoList, Team team){
        List<TeamUser> deleteTeamUserList = team.getTeamUserList().stream()
                .filter(tu -> userDtoList.stream().noneMatch(Predicate.isEqual(tu)))
                .collect(Collectors.toList());
        List<UserDto> addUserDtoList = userDtoList.stream()
                .filter(u -> team.getTeamUserList().stream().noneMatch(Predicate.isEqual(u)))
                .collect(Collectors.toList());
        deleteTeamUserList.stream().forEach(tu -> team.deleteTeamUser(tu.getUser().getId()));
        addUserDtoList.stream().forEach(au -> team.addTeamUser(jpaTeamUserRepository.findById(au.getId()).get()));
    }

    @Override
    @Transactional
    public void deleteTeamById(int teamId){
        Team team = jpaTeamRepository.findById(teamId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find team with id \"" + teamId + "\"");}
        );
        team.getTeamBuilding().deleteTeam(teamId);
        jpaTeamRepository.deleteById(teamId);
    }

    @Override
    public List<TeamDto> readAllTeamByTeamBuildingId(int teamBuildingId){
        List<Team> teamList = jpaTeamRepository.findAllByTeamBuildingId(teamBuildingId);
        return teamRepositoryMapper.teamListToTeamDtoList(teamList);
    }

    @Override
    public List<UserDto> readAllUserByTeamBuildingId(int teamBuildingId){
        List<User> userList = new ArrayList<>();
        jpaTeamRepository.findAllUserIdByTeamBuildingId(teamBuildingId).stream().forEach(id -> userList.add(jpaTeamUserRepository.findById(id).get()));
        return teamRepositoryMapper.userListToUserDtoList(userList);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistTeam(int teamId){
        return jpaTeamRepository.existsById(teamId);
    }

}
