package org.waldreg.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.attendance.Attendance;
import org.waldreg.domain.attendance.AttendanceTypeReward;
import org.waldreg.domain.attendance.AttendanceUser;

@Repository
public class MemoryAttendanceStorage{

    private final List<Attendance> attendanceList;
    private final List<AttendanceUser> attendanceTargetList;
    private final Map<String, AttendanceTypeReward> attendanceTypeMap;
    private final AtomicInteger atomicInteger;

    public MemoryAttendanceStorage(){
        this.attendanceList = new ArrayList<>();
        this.attendanceTargetList = new ArrayList<>();
        this.attendanceTypeMap = new ConcurrentHashMap<>();
        this.atomicInteger = new AtomicInteger(1);
    }

    public void addAttendanceTarget(AttendanceUser attendanceUser){
        attendanceUser.setAttendanceUserId(atomicInteger.getAndIncrement());
        boolean isPresent = attendanceTargetList.stream().anyMatch(a -> a.getUser().getId() == attendanceUser.getUser().getId());
        if(isPresent){
            return;
        }
        attendanceTargetList.add(attendanceUser);
        stageAttendanceUser();
    }

    public void stageAttendanceUser(){
        final LocalDate now = LocalDate.now();
        attendanceTargetList
                .forEach(a -> stageAttendanceUser(
                        Attendance.builder()
                                .attendanceId(atomicInteger.getAndIncrement())
                                .user(a)
                                .attendanceType(attendanceTypeMap.get(AttendanceType.ABSENCE.toString()))
                                .attendanceDate(now)
                                .build()
                ));
    }

    private void stageAttendanceUser(Attendance attendance){
        boolean isPresent = attendanceList.stream()
                .anyMatch(s -> s.equals(attendance));
        if(isPresent){
            return;
        }
        attendanceList.add(attendance);
    }

    public Attendance readAttendance(int id){
        return attendanceList.stream()
                .filter(a -> a.getAttendanceUser().getUser().getId() == id)
                .findFirst()
                .orElseThrow(() -> {throw new IllegalStateException("Cannot read attendance users id \"" + id + "\"");});
    }

    public void deleteAttendanceTarget(int id){
        attendanceTargetList.stream()
                .filter(at -> at.getUser().getId() == id)
                .findAny()
                .ifPresent(attendanceTargetList::remove);
        deleteStagedAttendanceTarget(id);
    }

    private void deleteStagedAttendanceTarget(int id){
        attendanceList.stream()
                .filter(a -> a.getAttendanceUser().getUser().getId() == id)
                .findAny()
                .ifPresent(attendanceList::remove);
    }

    public void changeAttendance(int targetUserId, LocalDate targetDate, AttendanceType changedType){
        attendanceList.stream()
                .filter(a -> isMatched(a, targetUserId, targetDate))
                .findFirst()
                .ifPresent(a -> a.setAttendanceType(attendanceTypeMap.get(changedType.toString())));
    }

    private boolean isMatched(Attendance attendance, int id, LocalDate targetDate){
        return attendance.getAttendanceDate().equals(targetDate) && attendance.getAttendanceUser().getUser().getId() == id;
    }

    public List<Attendance> readAllAttendance(LocalDate from, LocalDate to){
        return attendanceList.stream()
                .filter(a -> isMatchedDate(from, a.getAttendanceDate(), to))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Attendance> readSpecificUsersAttendance(int id, LocalDate from, LocalDate to){
        return attendanceList.stream()
                .filter(a -> isMatchedDate(from, a.getAttendanceDate(), to) && a.getAttendanceUser().getUser().getId() == id)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean isMatchedDate(LocalDate from, LocalDate matchedDate, LocalDate to){
        return from.isBefore(matchedDate) || from.isEqual(matchedDate)
                && to.isAfter(matchedDate) || to.isEqual(matchedDate);
    }

    public boolean isAttendanceTarget(int id){
        return attendanceTargetList.stream().anyMatch(a -> a.getUser().getId() == id);
    }

    @PostConstruct
    private void initAttendanceRewardTag(){
        initAbsenceAttendance();
        initLateAttendance();
        initAttendanced();
        initAcknowledgeAttendance();
    }

    private void initAbsenceAttendance(){
        attendanceTypeMap.put(AttendanceType.ABSENCE.toString(), AttendanceTypeReward.builder()
                .rewardTag(null)
                .name(AttendanceType.ABSENCE.toString())
                .build());
    }

    private void initLateAttendance(){
        attendanceTypeMap.put(AttendanceType.LATE_ATTENDANCE.toString(), AttendanceTypeReward.builder()
                .rewardTag(null)
                .name(AttendanceType.LATE_ATTENDANCE.toString())
                .build());
    }

    private void initAttendanced(){
        attendanceTypeMap.put(AttendanceType.ATTENDANCED.toString(), AttendanceTypeReward.builder()
                .rewardTag(null)
                .name(AttendanceType.ATTENDANCED.toString())
                .build());
    }

    private void initAcknowledgeAttendance(){
        attendanceTypeMap.put(AttendanceType.ACKNOWLEDGE_ABSENCE.toString(), AttendanceTypeReward.builder()
                .rewardTag(null)
                .name(AttendanceType.ACKNOWLEDGE_ABSENCE.toString())
                .build());
    }

    public void deleteAll(){
        attendanceList.clear();
        attendanceTargetList.clear();
    }

}
