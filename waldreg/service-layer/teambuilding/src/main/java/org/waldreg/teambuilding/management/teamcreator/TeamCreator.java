package org.waldreg.teambuilding.management.teamcreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import org.waldreg.teambuilding.dto.UserRequestDto;

@Service
public class TeamCreator{

    public List<Team> createRandomTeamList(List<UserRequestDto> userRequestDtoList, int teamCount){
        List<Team> teamList = initTeamList(teamCount);
        int index = 0;
        for (UserRequestDto userRequestDto : userRequestDtoList){
            int teamIndex = index % teamCount;
            if (teamIndex == 0){
                Collections.sort(teamList);
            }
            teamList.get(teamIndex).weight += userRequestDto.getWeight();
            teamList.get(teamIndex).memberList.add(userRequestDto.getUserId());
            index++;
        }
        return teamList;
    }

    private List<Team> initTeamList(int teamCount){
        List<Team> teamList = new ArrayList<>();
        for (int i = 0; i < teamCount; i++){
            teamList.add(new Team(0, new ArrayList<>()));
        }
        return teamList;
    }

    public class Team implements Comparable<Team>{

        private int weight;
        private List<String> memberList;

        public Team(int weight, List<String> memberList){
            this.weight = weight;
            this.memberList = memberList;
        }

        public List<String> getMemberList(){
            return memberList;
        }

        @Override
        public int compareTo(Team team){
            if (team.weight < weight){
                return 1;
            } else if (team.weight > weight){
                return -1;
            }
            return 0;
        }

    }

}
