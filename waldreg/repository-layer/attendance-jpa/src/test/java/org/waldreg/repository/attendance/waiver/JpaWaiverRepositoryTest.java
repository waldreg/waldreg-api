package org.waldreg.repository.attendance.waiver;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.domain.waiver.Waiver;
import org.waldreg.repository.attendance.helper.TestJpaCharacterRepository;
import org.waldreg.repository.attendance.waiver.repository.JpaUserRepository;
import org.waldreg.repository.attendance.waiver.repository.JpaWaiverRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
class JpaWaiverRepositoryTest{

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private JpaWaiverRepository jpaWaiverRepository;

    @Autowired
    private TestJpaCharacterRepository jpaCharacterRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("출석 면제 신청 및 조회 테스트")
    void WAIVE_TEST(){
        // given
        User user = jpaUserRepository.save(getUser("hello"));
        Waiver waiver = Waiver.builder()
                .waiverUser(user)
                .waiverDate(LocalDate.now())
                .waiverReason("sick")
                .build();

        // when
        Waiver saved = jpaWaiverRepository.save(waiver);

        entityManager.flush();
        entityManager.clear();

        Waiver result = jpaWaiverRepository.findById(saved.getWaiverId()).get();

        // then
        assertWaiver(saved, result);
    }

    @Test
    @DisplayName("모든 출석 면제 신청 조회 테스트")
    void READ_ALL_WAIVER_TEST(){
        // given
        User user = jpaUserRepository.save(getUser("hello"));
        Waiver waiver1 = Waiver.builder()
                .waiverUser(user)
                .waiverDate(LocalDate.now())
                .waiverReason("sick")
                .build();

        Waiver waiver2 = Waiver.builder()
                .waiverUser(user)
                .waiverDate(LocalDate.now().plusYears(1))
                .waiverReason("sick")
                .build();

        Waiver waiver3 = Waiver.builder()
                .waiverUser(user)
                .waiverDate(LocalDate.now().plusYears(2))
                .waiverReason("sick")
                .build();

        // when
        List<Waiver> waiverList = jpaWaiverRepository.saveAllAndFlush(List.of(waiver1, waiver2, waiver3));

        entityManager.clear();

        List<Waiver> resultList = jpaWaiverRepository.findAll();

        // then
        assertEquals(resultList.size(), waiverList.size());
        resultList.forEach(r -> waiverList.stream()
                .filter(e -> Objects.equals(e.getWaiverId(), r.getWaiverId()))
                .findFirst()
                .ifPresentOrElse(
                e -> assertWaiver(e, r),
                () -> {throw new IllegalStateException();}
        ));
    }

    @Test
    @DisplayName("출석 면제 요청 삭제 테스트")
    void DELETE_WAIVER_TEST(){
        // given
        User user = jpaUserRepository.save(getUser("hello"));
        Waiver waiver = Waiver.builder()
                .waiverUser(user)
                .waiverDate(LocalDate.now())
                .waiverReason("sick")
                .build();

        // when
        Waiver saved = jpaWaiverRepository.save(waiver);

        entityManager.flush();
        entityManager.clear();

        jpaWaiverRepository.deleteById(saved.getWaiverId());

        entityManager.flush();
        entityManager.clear();

        Optional<Waiver> result = jpaWaiverRepository.findById(waiver.getWaiverId());

        // then
        assertTrue(result.isEmpty());
    }

    private void assertWaiver(Waiver expected, Waiver result){
        assertAll(
                () -> assertEquals(expected.getWaiverId(), result.getWaiverId()),
                () -> assertEquals(expected.getWaiverDate(), result.getWaiverDate()),
                () -> assertEquals(expected.getWaiverReason(), result.getWaiverReason()),
                () -> assertEquals(expected.getWaiverUser().getId(), result.getWaiverUser().getId()),
                () -> assertEquals(expected.getWaiverUser().getUserId(), result.getWaiverUser().getUserId()),
                () -> assertEquals(expected.getWaiverUser().getUserPassword(), result.getWaiverUser().getUserPassword()),
                () -> assertEquals(expected.getWaiverUser().getName(), result.getWaiverUser().getName()),
                () -> assertEquals(expected.getWaiverUser().getCreatedAt(), result.getWaiverUser().getCreatedAt())
        );
    }

    private User getUser(String userId){
        return User.builder()
                .userId(userId)
                .userPassword(userId)
                .name(userId)
                .character(getCharacter(userId))
                .phoneNumber("010-0000-0000")
                .build();
    }

    private Character getCharacter(String name){
        return jpaCharacterRepository.saveAndFlush(Character.builder()
                .characterName(name)
                .permissionList(List.of())
                .build());
    }

}
