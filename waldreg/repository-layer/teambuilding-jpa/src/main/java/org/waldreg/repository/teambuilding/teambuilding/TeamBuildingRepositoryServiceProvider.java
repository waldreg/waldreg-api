package org.waldreg.repository.teambuilding.teambuilding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.teambuilding.TeamBuilding;
import org.waldreg.repository.teambuilding.teambuilding.mapper.TeamBuildingRepositoryMapper;
import org.waldreg.repository.teambuilding.teambuilding.repository.JpaTeamBuildingRepository;
import org.waldreg.repository.teambuilding.team.repository.JpaTeamRepository;
import org.waldreg.repository.teambuilding.repository.JpaTeamUserWrapperRepository;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.team.spi.TeamInTeamBuildingRepository;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingRepository;

@Repository
public class TeamBuildingRepositoryServiceProvider implements TeamBuildingRepository, TeamInTeamBuildingRepository{

    private final JpaTeamBuildingRepository jpaTeamBuildingRepository;
    private final JpaTeamRepository jpaTeamRepository;
    private final JpaTeamUserWrapperRepository jpaTeamUserWrapperRepository;
    private final TeamBuildingRepositoryMapper teamBuildingRepositoryMapper;

    public TeamBuildingRepositoryServiceProvider(JpaTeamBuildingRepository jpaTeamBuildingRepository, JpaTeamRepository jpaTeamRepository, JpaTeamUserWrapperRepository jpaTeamUserWrapperRepository, TeamBuildingRepositoryMapper teamBuildingRepositoryMapper){
        this.jpaTeamBuildingRepository = jpaTeamBuildingRepository;
        this.jpaTeamRepository = jpaTeamRepository;
        this.jpaTeamUserWrapperRepository = jpaTeamUserWrapperRepository;
        this.teamBuildingRepositoryMapper = teamBuildingRepositoryMapper;
    }

    @Override
    @Transactional
    public TeamBuildingDto createTeamBuilding(String title){
        TeamBuilding teamBuilding = teamBuildingRepositoryMapper.teamBuildingTitleToTeamBuildingDomain(title);
        TeamBuilding savedTeamBuilding = jpaTeamBuildingRepository.save(teamBuilding);
        return teamBuildingRepositoryMapper.teamBuildingToTeamBuildingDto(savedTeamBuilding);
    }

    @Override
    @Transactional
    public void updateTeamListInTeamBuilding(TeamBuildingDto teamBuildingDto){
        TeamBuilding teamBuilding = jpaTeamBuildingRepository.findById(teamBuildingDto.getTeamBuildingId()).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find team building with id \"" + teamBuildingDto.getTeamBuildingId() + "\"");}
        );
        teamBuilding.setTeamList(findTeamList(teamBuildingDto.getTeamList()));
    }

    private List<Team> findTeamList(List<TeamDto> teamDtoList){
        List<Team> teamList = new ArrayList<>();
        teamDtoList.stream().forEach(t -> teamList.add(jpaTeamRepository.findById(t.getTeamId()).get()));
        return teamList;
    }

    @Override
    @Transactional(readOnly = true)
    public TeamBuildingDto readTeamBuildingById(int teamBuildingId){
        TeamBuilding teamBuilding = jpaTeamBuildingRepository.findById(teamBuildingId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find team building with id \"" + teamBuildingId + "\"");}
        );
        return teamBuildingRepositoryMapper.teamBuildingToTeamBuildingDto(teamBuilding);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamBuildingDto> readAllTeamBuilding(int startIdx, int endIdx){
        int start = startIdx - 1;
        int count = endIdx - startIdx + 1;
        return teamBuildingRepositoryMapper.teamBuildingListToTeamBuildingDtoList(jpaTeamBuildingRepository.findAll(start, count));
    }

    @Override
    @Transactional
    public void updateTeamBuildingTitleById(int teamBuildingId, String teamBuildingTitle){
        TeamBuilding teamBuilding = jpaTeamBuildingRepository.findById(teamBuildingId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find team building with id \"" + teamBuildingId + "\"");}
        );
        teamBuilding.setTeamBuildingTitle(teamBuildingTitle);
        teamBuilding.setLastModifiedAt(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deleteTeamBuildingById(int teamBuildingId){
        jpaTeamBuildingRepository.deleteById(teamBuildingId);
    }

    @Override
    @Transactional
    public void addTeamInTeamBuildingTeamList(TeamDto teamDto){
        TeamBuilding teamBuilding = jpaTeamBuildingRepository.findById(teamDto.getTeamBuildingId()).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find team building with id \"" + teamDto.getTeamBuildingId() + "\"");}
        );
        Team team = jpaTeamRepository.findById(teamDto.getTeamId()).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find team with id \"" + teamDto.getTeamId() + "\"");}
        );
        List<Team> teamList = teamBuilding.getTeamList();
        teamList.add(team);
        teamBuilding.setTeamList(teamList);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistTeamBuilding(int teamBuildingId){
        return jpaTeamBuildingRepository.existsById(teamBuildingId);
    }

    @Override
    @Transactional(readOnly = true)
    public int readMaxIdx(){
        return (int) jpaTeamBuildingRepository.count();
    }

}
