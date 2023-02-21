package org.waldreg.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.user.User;

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

    public void updateTeam(Team team){
        storage.replace(team.getTeamId(),team);
    }

    public void deleteTeamById(int teamId){
        storage.remove(teamId);
    }

    public List<Team> readAllTeamByTeamBuildingId(int teamBuildingId){
        List<Team> teamList = new ArrayList<>();
        for(Map.Entry<Integer, Team> teamEntry : storage.entrySet()){
            if(teamEntry.getValue().getTeamBuildingId() == teamBuildingId){
                teamList.add(teamEntry.getValue());
            }
        }
        return teamList;
    }

    public List<User> readAllUserByTeamBuildingId(int teamBuildingId){
        List<User> userList = new ArrayList<>();
        for(Map.Entry<Integer, Team> teamEntry : storage.entrySet()){
            if(teamEntry.getValue().getTeamBuildingId() == teamBuildingId){
                teamEntry.getValue().getUserList().stream().forEach(i->userList.add(i));
            }
        }
        return userList;
    }

}