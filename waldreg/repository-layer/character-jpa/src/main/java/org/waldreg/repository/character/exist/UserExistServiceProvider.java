package org.waldreg.repository.character.exist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.character.spi.UserExistChecker;
import org.waldreg.repository.character.repository.JpaUserRepository;

@Repository
public class UserExistServiceProvider implements UserExistChecker{

    private final JpaUserRepository jpaUserRepository;

    @Autowired
    UserExistServiceProvider(JpaUserRepository jpaUserRepository){
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public boolean isExistUser(int id){
        return jpaUserRepository.existsById(id);
    }

}
