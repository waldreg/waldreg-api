package org.waldreg.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.rewardtag.RewardTag;

@Repository
public class MemoryRewardTagStorage{

    private final Map<Integer, RewardTag> storage;
    private final AtomicInteger atomicInteger;

    {
        storage = new ConcurrentHashMap<>();
        atomicInteger = new AtomicInteger(1);
    }

    public void createRewardTag(RewardTag rewardTag){

    }

    public void updateRewardTag(int rewardTagId, RewardTag rewardTag){

    }

    public List<RewardTag> readRewardTagList(){
        List<RewardTag> rewardTagList = new ArrayList<>();
        for(Map.Entry<Integer, RewardTag> entry : storage.entrySet()){
            rewardTagList.add(entry.getValue());
        }
        return rewardTagList;
    }

    public RewardTag readRewardTag(int rewardTagId){
        return storage.get(rewardTagId);
    }

    public void deleteRewardTag(int rewardTagId){
        storage.remove(rewardTagId);
    }

    public void deleteAll(){
        storage.clear();
    }

}
