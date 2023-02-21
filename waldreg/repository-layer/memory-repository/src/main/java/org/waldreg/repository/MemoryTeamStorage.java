package org.waldreg.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.waldreg.domain.teambuilding.Team;

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
        team.setTeamId(atomicInteger.getAndIncrement());
        storage.put(team.getTeamId(),team);
        return team;
    }

    public Team readTeamById(int teamId){
        return storage.get(teamId);
    }

}
