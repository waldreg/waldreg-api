package org.waldreg.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.rewardtag.RewardTag;

@Repository
public class RewardTagStorage{

    private final Map<Integer, RewardTag> storage;
    private final AtomicInteger atomicInteger;

    {
        storage = new ConcurrentHashMap<>();
        atomicInteger = new AtomicInteger(1);
    }

    public void createRewardTag(RewardTag rewardTag){
        int id = atomicInteger.getAndIncrement();
        rewardTag.setRewardTagId(id);
        storage.put(id, rewardTag);
    }

}
