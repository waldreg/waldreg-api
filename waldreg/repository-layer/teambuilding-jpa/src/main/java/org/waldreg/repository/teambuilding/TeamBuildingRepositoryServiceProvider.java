package org.waldreg.repository.teambuilding;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.teambuilding.TeamBuilding;
import org.waldreg.repository.teambuilding.mapper.TeamBuildingRepositoryMapper;
import org.waldreg.repository.teambuilding.repository.JpaTeamBuildingRepository;
import org.waldreg.repository.teambuilding.repository.JpaTeamRepository;
import org.waldreg.repository.teambuilding.repository.JpaTeamUserRepository;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingRepository;

@Repository
public class TeamBuildingRepositoryServiceProvider implements TeamBuildingRepository{

    private final JpaTeamBuildingRepository jpaTeamBuildingRepository;
    private final JpaTeamRepository jpaTeamRepository;
    private final JpaTeamUserRepository jpaTeamUserRepository;
    private final TeamBuildingRepositoryMapper teamBuildingRepositoryMapper;

    public TeamBuildingRepositoryServiceProvider(JpaTeamBuildingRepository jpaTeamBuildingRepository, JpaTeamRepository jpaTeamRepository, JpaTeamUserRepository jpaTeamUserRepository, TeamBuildingRepositoryMapper teamBuildingRepositoryMapper){
        this.jpaTeamBuildingRepository = jpaTeamBuildingRepository;
        this.jpaTeamRepository = jpaTeamRepository;
        this.jpaTeamUserRepository = jpaTeamUserRepository;
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
    }

    @Override
    @Transactional
    public void deleteTeamBuildingById(int teamBuildingId){
        jpaTeamBuildingRepository.deleteById(teamBuildingId);
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
