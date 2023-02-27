package org.waldreg.repository.reward.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.rewardtag.RewardTagWrapper;

@Repository
public interface JpaRewardTagWrapperRepository extends JpaRepository<RewardTagWrapper, Integer>{

    @Modifying
    @Query(value = "DELETE FROM REWARD_TAG_WRAPPER", nativeQuery = true)
    void deleteAllWrapper();

    @Modifying
    @Query(value = "DELETE FROM REWARD_TAG_WRAPPER AS RTW WHERE RTW.REWARD_TAG_REWARD_TAG_ID = :rewardTagId", nativeQuery = true)
    void deleteByRewardTagId(@Param("rewardTagId") int rewardTagId);

}
