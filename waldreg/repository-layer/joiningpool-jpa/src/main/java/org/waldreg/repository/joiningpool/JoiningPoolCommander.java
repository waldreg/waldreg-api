package org.waldreg.repository.joiningpool;

import java.util.List;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.UserJoiningPool;

@Repository
class JoiningPoolCommander{

    private final EntityManager entityManager;

    @Autowired
    JoiningPoolCommander(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    public List<UserJoiningPool> readUserJoiningPool(int startIdx, int endIdx){
        Logger logger = Logger.getLogger(this.getClass().getSimpleName());
        logger.info(() -> "startIdx \"" + startIdx + "\"");
        logger.info(() -> "endIdx \"" + endIdx + "\"");
        return entityManager.createQuery("select ju from UserJoiningPool as ju", UserJoiningPool.class)
                .setFirstResult(startIdx - 1)
                .setMaxResults(endIdx - startIdx + 1)
                .getResultList();
    }

}
