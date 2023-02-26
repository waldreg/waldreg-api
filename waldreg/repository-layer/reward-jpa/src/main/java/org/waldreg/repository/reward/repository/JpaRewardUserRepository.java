package org.waldreg.repository.reward.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.rewardtag.RewardTagWrapper;

@Repository
public interface JpaRewardUserRepository extends JpaRepository<RewardTagWrapper, Integer>{

}
