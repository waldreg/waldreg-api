package org.waldreg.repository.attendance.management;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.attendance.management.dto.AttendanceDayDto;
import org.waldreg.attendance.management.dto.AttendanceStatusChangeDto;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.management.dto.AttendanceUserDto;
import org.waldreg.attendance.management.spi.AttendanceRepository;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.attendance.Attendance;
import org.waldreg.domain.attendance.AttendanceTypeReward;
import org.waldreg.domain.attendance.AttendanceUser;
import org.waldreg.domain.user.User;
import org.waldreg.repository.attendance.management.mapper.AttendanceManagementMapper;
import org.waldreg.repository.attendance.repository.JpaAttendanceRepository;
import org.waldreg.repository.attendance.repository.JpaAttendanceTypeRewardRepository;
import org.waldreg.repository.attendance.repository.JpaAttendanceUserRepository;
import org.waldreg.repository.attendance.repository.JpaUserRepository;

@Repository
public class AttendanceRepositoryServiceProvider implements AttendanceRepository{

    private final JpaAttendanceRepository jpaAttendanceRepository;
    private final JpaAttendanceUserRepository jpaAttendanceUserRepository;
    private final JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository;
    private final JpaUserRepository jpaUserRepository;
    private final AttendanceManagementMapper attendanceManagementMapper;

    @Autowired
    AttendanceRepositoryServiceProvider(JpaAttendanceRepository jpaAttendanceRepository,
            JpaAttendanceUserRepository jpaAttendanceUserRepository,
            JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository,
            @Qualifier("attendanceJpaUserRepository") JpaUserRepository jpaUserRepository,
            AttendanceManagementMapper attendanceManagementMapper){
        this.jpaAttendanceRepository = jpaAttendanceRepository;
        this.jpaAttendanceUserRepository = jpaAttendanceUserRepository;
        this.jpaAttendanceTypeRewardRepository = jpaAttendanceTypeRewardRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.attendanceManagementMapper = attendanceManagementMapper;
    }

    @Override
    @Transactional
    public void registerAttendanceTarget(int id){
        User user = jpaUserRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user id \"" + id + "\"");}
        );
        AttendanceUser attendanceUser = AttendanceUser.builder()
                .user(user)
                .build();
        jpaAttendanceUserRepository.save(attendanceUser);
    }

    @Override
    @Transactional
    public void deleteRegisteredAttendanceTarget(int id){
        jpaAttendanceUserRepository.deleteByUserId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AttendanceTargetDto> readAttendanceTarget(int id){
        Attendance attendance = getAttendance(id);
        return Optional.of(attendanceManagementMapper.attendanceToAttendanceTargetDto(attendance));
    }

    private Attendance getAttendance(int id){
        return getAttendance(id, LocalDate.now());
    }

    @Override
    @Transactional
    public void changeAttendanceStatus(AttendanceStatusChangeDto attendanceStatusChangeDto){
        Attendance attendance = getAttendance(attendanceStatusChangeDto.getId(), attendanceStatusChangeDto.getAttendanceDate());
        attendance.setAttendanceType(getAttendanceTypeReward(attendanceStatusChangeDto.getAttendanceType()));
    }

    private Attendance getAttendance(int id, LocalDate localDate){
        List<Attendance> attendanceList = jpaAttendanceRepository.findSpecificBetweenAttendanceDate(id, localDate, localDate);
        throwIfDoesNotExistAttendance(id, attendanceList);
        return attendanceList.get(0);
    }

    private void throwIfDoesNotExistAttendance(int id, List<Attendance> attendanceList){
        if(attendanceList.size() != 1){
            throw new IllegalStateException("Cannot find Attendance user.id \"" + id + "\"");
        }
    }

    @Override
    @Transactional
    public List<AttendanceDayDto> readAttendanceStatusList(LocalDate from, LocalDate to){
        List<Attendance> attendanceList = jpaAttendanceRepository.findBetweenAttendanceDate(from, to);
        return attendanceManagementMapper.attendanceListToAttendanceDayDtoList(attendanceList);
    }

    @Override
    @Transactional
    public AttendanceUserDto readSpecificAttendanceStatusList(int id, LocalDate from, LocalDate to){
        List<Attendance> attendanceList = jpaAttendanceRepository.findSpecificBetweenAttendanceDate(id, from, to);
        return attendanceManagementMapper.attendanceListToAttendanceUserDto(attendanceList);
    }

    @Override
    @Transactional
    public void createNewAttendanceCalendarIfAbsent(LocalDate current){
        Map<Integer, Attendance> existAttendanceMap = convertListToMap(jpaAttendanceRepository.findBetweenAttendanceDate(current, current));
        List<AttendanceUser> attendanceUserList = jpaAttendanceUserRepository.findAll();
        List<Attendance> attendanceListToAdd = new ArrayList<>();
        attendanceUserList.forEach(au -> {
            if(existAttendanceMap.get(au.getAttendanceUserId()) == null){
                attendanceListToAdd.add(
                        Attendance.builder().user(au).attendanceDate(current).attendanceType(getAbsenceAttendanceTypeReward()).build()
                );
            }
        });
        jpaAttendanceRepository.saveAll(attendanceListToAdd);
    }

    private Map<Integer, Attendance> convertListToMap(List<Attendance> attendanceList){
        return attendanceList.stream().collect(Collectors.toMap(a -> a.getAttendanceUser().getAttendanceUserId(), a -> a));
    }

    private AttendanceTypeReward getAbsenceAttendanceTypeReward(){
        return getAttendanceTypeReward(AttendanceType.ABSENCE);
    }

    private AttendanceTypeReward getAttendanceTypeReward(AttendanceType attendanceType){
        return jpaAttendanceTypeRewardRepository.findByName(attendanceType.toString())
                .orElseThrow(() -> {throw new IllegalStateException("Cannot find AttendanceTypeReward named \"" + attendanceType + "\"");});
    }

}
