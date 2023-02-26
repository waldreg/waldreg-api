package org.waldreg.repository.reward.exist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.repository.reward.repository.JpaRewardUserRepository;
import org.waldreg.reward.users.spi.repository.UserExistChecker;

@Repository
public class UserExistCheckerServiceProvider implements UserExistChecker{

    private final JpaRewardUserRepository jpaRewardUserRepository;

    @Autowired
    UserExistCheckerServiceProvider(JpaRewardUserRepository jpaRewardUserRepository){
        this.jpaRewardUserRepository = jpaRewardUserRepository;
    }

    @Override
    public boolean isUserExist(int id){
        return jpaRewardUserRepository.existsById(id);
    }

}
