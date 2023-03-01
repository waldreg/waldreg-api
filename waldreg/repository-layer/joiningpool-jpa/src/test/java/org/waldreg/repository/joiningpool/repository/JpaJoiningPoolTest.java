package org.waldreg.repository.joiningpool.repository;

import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.user.UserJoiningPool;

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
public class JpaJoiningPoolTest{

    @Autowired
    private JpaJoiningPoolRepository jpaJoiningPoolRepository;
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("가입 신청 성공 테스트")
    public void CREATE_USER_SUCCESS_TEST(){
        //given
        UserJoiningPool userJoiningPool = UserJoiningPool.builder()
                .userId("userId")
                .name("name")
                .userPassword("userPassword")
                .phoneNumber("010-1234-5678")
                .build();
        //when&then
        Assertions.assertDoesNotThrow(() -> jpaJoiningPoolRepository.save(userJoiningPool));
    }

    @Test
    @DisplayName("대기열에서 userId로 유저 삭제 성공 테스트")
    public void DELETE_USER_BY_USER_ID_SUCCESS_TEST(){
        //given
        UserJoiningPool userJoiningPool = UserJoiningPool.builder()
                .userId("userId")
                .name("name")
                .userPassword("userPassword")
                .phoneNumber("010-1234-5678")
                .build();
        jpaJoiningPoolRepository.save(userJoiningPool);
        entityManager.flush();
        entityManager.clear();
        //when
        jpaJoiningPoolRepository.deleteByUserId(userJoiningPool.getUserId());

        //then
        Assertions.assertFalse(jpaJoiningPoolRepository.existsByUserId(userJoiningPool.getUserId()));

    }

    @Test
    @DisplayName("유저 아이디로 조회 성공")
    public void INQUIRY_USER_BY_USER_ID_TEST(){
        //given
        UserJoiningPool userJoiningPool = UserJoiningPool.builder()
                .userId("userId")
                .name("name")
                .userPassword("userPassword")
                .phoneNumber("010-1234-5678")
                .build();
        jpaJoiningPoolRepository.save(userJoiningPool);
        entityManager.flush();
        entityManager.clear();
        //when
        UserJoiningPool result = jpaJoiningPoolRepository.findByUserId(userJoiningPool.getUserId()).get();
        //then
        Assertions.assertAll(
                ()->Assertions.assertEquals(userJoiningPool.getUserId(),result.getUserId()),
                ()->Assertions.assertEquals(userJoiningPool.getUserPassword(),result.getUserPassword()),
                ()->Assertions.assertEquals(userJoiningPool.getName(),result.getName()),
                ()->Assertions.assertEquals(userJoiningPool.getPhoneNumber(),result.getPhoneNumber())
        );
    }

    @Test
    @DisplayName("유저 아이디로 존재 여부 조회 성공")
    public void IS_EXIST_USER_BY_USER_ID_TEST(){
        //given
        UserJoiningPool userJoiningPool = UserJoiningPool.builder()
                .userId("userId")
                .name("name")
                .userPassword("userPassword")
                .phoneNumber("010-1234-5678")
                .build();
        jpaJoiningPoolRepository.save(userJoiningPool);
        entityManager.flush();
        entityManager.clear();
        //when
        boolean result = jpaJoiningPoolRepository.existsByUserId(userJoiningPool.getUserId());
        //then
        Assertions.assertTrue(result);
    }

}
