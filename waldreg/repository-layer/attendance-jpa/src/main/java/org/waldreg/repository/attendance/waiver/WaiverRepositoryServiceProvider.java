package org.waldreg.repository.attendance.waiver;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.attendance.waiver.dto.WaiverDto;
import org.waldreg.attendance.waiver.spi.WaiverRepository;
import org.waldreg.domain.attendance.Attendance;
import org.waldreg.domain.user.User;
import org.waldreg.domain.waiver.Waiver;
import org.waldreg.repository.attendance.repository.JpaAttendanceRepository;
import org.waldreg.repository.attendance.repository.JpaAttendanceTypeRewardRepository;
import org.waldreg.repository.attendance.repository.JpaAttendanceUserRepository;
import org.waldreg.repository.attendance.repository.JpaUserRepository;
import org.waldreg.repository.attendance.waiver.mapper.WaiverMapper;
import org.waldreg.repository.attendance.waiver.repository.JpaWaiverRepository;

@Repository
public class WaiverRepositoryServiceProvider implements WaiverRepository{

    private final JpaWaiverRepository jpaWaiverRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaAttendanceUserRepository jpaAttendanceUserRepository;
    private final JpaAttendanceRepository jpaAttendanceRepository;
    private final JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository;
    private final WaiverMapper waiverMapper;

    @Autowired
    WaiverRepositoryServiceProvider(JpaWaiverRepository jpaWaiverRepository,
            @Qualifier("attendanceJpaUserRepository") JpaUserRepository jpaUserRepository,
            JpaAttendanceUserRepository jpaAttendanceUserRepository,
            JpaAttendanceRepository jpaAttendanceRepository,
            JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository,
            WaiverMapper waiverMapper){
        this.jpaWaiverRepository = jpaWaiverRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.jpaAttendanceUserRepository = jpaAttendanceUserRepository;
        this.jpaAttendanceRepository = jpaAttendanceRepository;
        this.jpaAttendanceTypeRewardRepository = jpaAttendanceTypeRewardRepository;
        this.waiverMapper = waiverMapper;
    }

    @Override
    @Transactional
    public void waive(WaiverDto waiverRequest){
        User user = jpaUserRepository.findById(waiverRequest.getId()).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user id \"" + waiverRequest.getId() + "\"");}
        );
        Waiver waiver = waiverMapper.waiverDtoToWaiver(waiverRequest, user);
        jpaWaiverRepository.save(waiver);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAttendanceTarget(int id){
        return jpaAttendanceUserRepository.existsByUserId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WaiverDto> readWaiverList(){
        List<Waiver> waiverList = jpaWaiverRepository.findAll();
        return waiverList.stream()
                .map(waiverMapper::waiverToWaiverDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WaiverDto> readWaiverByWaiverId(int waiverId){
        Optional<Waiver> waiver = jpaWaiverRepository.findById(waiverId);
        if(waiver.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(waiverMapper.waiverToWaiverDto(waiver.get()));
    }

    @Override
    @Transactional
    public void acceptWaiver(int waiverId, AttendanceType attendanceType){
        Waiver waiver = jpaWaiverRepository.findById(waiverId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find waiver id \"" + waiverId + "\"");}
        );
        createAttendanceUserIfDoesNotStaged(waiver);
        Attendance attendance = jpaAttendanceRepository.findSpecificBetweenAttendanceDate(waiver.getWaiverUser().getId(), waiver.getWaiverDate(), waiver.getWaiverDate()).get(0);
        attendance.setAttendanceType(jpaAttendanceTypeRewardRepository.findByName(attendanceType.toString())
                .orElseThrow(
                        () -> {throw new IllegalStateException("Cannot find attendanceTypeReward named \"" + attendanceType + "\"");}
                ));
    }

    private void createAttendanceUserIfDoesNotStaged(Waiver waiver){
        List<Attendance> attendanceList = jpaAttendanceRepository.findSpecificBetweenAttendanceDate(waiver.getWaiverUser().getId(), waiver.getWaiverDate(), waiver.getWaiverDate());
        if(attendanceList.isEmpty()){
            jpaAttendanceRepository.save(
                    Attendance.builder()
                            .user(jpaAttendanceUserRepository
                                    .findByUserId(waiver.getWaiverUser().getId())
                                    .orElseThrow(() -> {throw new IllegalStateException("Cannot find user id \"" + waiver.getWaiverUser().getId() + "\"");}))
                            .attendanceDate(waiver.getWaiverDate())
                            .attendanceType(jpaAttendanceTypeRewardRepository.findByName(AttendanceType.ABSENCE.toString())
                                    .orElseThrow(() -> {throw new IllegalStateException("Cannot find attendanceTypeReward named \"" + AttendanceType.ABSENCE + "\"");}))
                            .build()
            );
        }
    }

    @Override
    @Transactional
    public void deleteWaiver(int waiverId){
        jpaWaiverRepository.deleteById(waiverId);
    }

}
