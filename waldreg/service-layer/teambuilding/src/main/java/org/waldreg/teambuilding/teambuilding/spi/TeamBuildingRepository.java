package org.waldreg.teambuilding.teambuilding.spi;

import java.util.List;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingDto;

public interface TeamBuildingRepository{

    TeamBuildingDto createTeamBuilding(String title);

    void updateTeamListInTeamBuilding(TeamBuildingDto teamBuildingDto);

    TeamBuildingDto readTeamBuildingById(int teamBuildingId);

    List<TeamBuildingDto> readAllTeamBuilding(int startIdx, int endIdx);

    void updateTeamBuildingTitleById(int teamBuildingId, String teamBuildingTitle);

    void deleteTeamBuildingById(int teamBuildingId);

    boolean isExistTeamBuilding(int teamBuildingId);

    int readMaxIdx();

}
