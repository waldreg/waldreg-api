package org.waldreg.repository;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.waiver.Waiver;

@Repository
public class MemoryWaiverStorage{

    private final List<Waiver> waiverList;
    private final AtomicInteger atomicInteger;

    @Autowired
    public MemoryWaiverStorage(List<Waiver> waiverList){
        this.waiverList = waiverList;
        this.atomicInteger = new AtomicInteger(1);
    }

    public void addWaiver(Waiver waiver){
    }

    public List<Waiver> readWaiverList(){
        return this.waiverList;
    }

    public Waiver readWaiverByWaiverId(int waiverId){
        return waiverList.stream()
                .filter(w -> w.getWaiverId() == waiverId)
                .findFirst()
                .orElse(null);
    }

    public void deleteWaiverByWaiverId(int waiverId){
        waiverList.stream()
                .filter(w -> w.getWaiverId() == waiverId)
                .findFirst().ifPresent(waiverList::remove);
    }

    public void deleteAll(){
        waiverList.clear();
    }

}
