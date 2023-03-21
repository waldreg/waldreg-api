package org.waldreg.repository.user;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;

@Repository
class UserRepositoryCommander{

    private final EntityManager entityManager;

    @Autowired
    UserRepositoryCommander(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    User readUserById(int id){
        try{
            return entityManager.createQuery("select u from User as u where u.id = :id", User.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException nre){
            throw new IllegalStateException("Cannot find user with id \"" + id + "\"");
        }
    }

    User readUserByUserId(String userId){
        try{
            return entityManager.createQuery("select u from User as u join u.character where u.userInfo.userId = :userId", User.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException nre){
            throw new IllegalStateException("Cannot find user with user_id \"" + userId + "\"");
        }
    }

    List<User> readUserList(int startIdx, int endIdx){
        return entityManager.createQuery("select u from User as u order by u.id", User.class)
                .setFirstResult(startIdx - 1)
                .setMaxResults(endIdx - startIdx + 1)
                .getResultList();
    }

}
