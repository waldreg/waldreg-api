package org.waldreg.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.user.User;

@Repository
public class MemoryTeamStorage{

    private final AtomicInteger atomicInteger;

    private final Map<Integer, Team> storage;

    {
        atomicInteger = new AtomicInteger(1);
        storage = new ConcurrentHashMap<>();
    }


    public void deleteAllTeam(){
        storage.clear();
    }

    public Team createTeam(Team team){
        return null;
    }

    public Team readTeamById(int teamId){
        return storage.get(teamId);
    }

    public void updateTeam(Team team){
        storage.replace(team.getTeamId(),team);
    }

    public void deleteTeamById(int teamId){
        storage.remove(teamId);
    }

    public List<Team> readAllTeamByTeamBuildingId(int teamBuildingId){
        return null;
    }

    public List<User> readAllUserByTeamBuildingId(int teamBuildingId){
        return null;
    }

}
