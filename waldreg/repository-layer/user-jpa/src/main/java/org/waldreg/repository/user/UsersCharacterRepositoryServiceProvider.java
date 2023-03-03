package org.waldreg.repository.user;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.repository.user.repository.JpaCharacterRepository;
import org.waldreg.user.spi.UsersCharacterRepository;

@Repository
public class UsersCharacterRepositoryServiceProvider implements UsersCharacterRepository{

    private final JpaCharacterRepository jpaCharacterRepository;

    public UsersCharacterRepositoryServiceProvider(JpaCharacterRepository jpaCharacterRepository){this.jpaCharacterRepository = jpaCharacterRepository;}

    @Override
    @Transactional(readOnly = true)
    public boolean isExistCharacterName(String characterName){
        return jpaCharacterRepository.existsByCharacterName(characterName);
    }

}
