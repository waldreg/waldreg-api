package org.waldreg.repository.reward.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.rewardtag.RewardTagWrapper;

@Repository
public interface JpaRewardTagWrapperRepository extends JpaRepository<RewardTagWrapper, Integer>{

    @Modifying
    @Query(value = "DELETE FROM REWARD_TAG_WRAPPER", nativeQuery = true)
    void deleteAllWrapper();

}
