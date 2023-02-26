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
import org.waldreg.domain.rewardtag.RewardTag;

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
    }

    public void stageAttendanceUser(){
        final LocalDate now = LocalDate.now();
        stageAttendanceUser(now);
    }

    public void stageAttendanceUser(LocalDate localDate){
    }

    public void changeAttendance(int targetUsersId, LocalDate targetDate, AttendanceType changedType){
        stageAttendanceSpecificUser(targetUsersId, targetDate);
        attendanceList.stream()
                .filter(a -> isMatched(a, targetUsersId, targetDate))
                .findFirst()
                .ifPresent(a -> a.setAttendanceType(attendanceTypeMap.get(changedType.toString())));
    }

    private void stageAttendanceSpecificUser(int targetUserId, LocalDate targetDate){

    }

    private void stageIfDoesNotDuplicated(Attendance attendance){
        boolean isPresent = attendanceList.stream()
                .anyMatch(s -> s.equals(attendance));
        if(isPresent){
            return;
        }
        attendanceList.add(attendance);
    }

    private boolean isMatched(Attendance attendance, int id, LocalDate targetDate){
        return attendance.getAttendanceDate().equals(targetDate) && attendance.getAttendanceUser().getUser().getId() == id;
    }

    public Attendance readAttendance(int id){
        return attendanceList.stream()
                .filter(a -> a.getAttendanceUser().getUser().getId() == id)
                .findFirst()
                .orElse(null);
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
        return (from.isBefore(matchedDate) || from.isEqual(matchedDate))
                && (to.isAfter(matchedDate) || to.isEqual(matchedDate));
    }

    public boolean isAttendanceTarget(int id){
        return attendanceTargetList.stream().anyMatch(a -> a.getUser().getId() == id);
    }

    public void setRewardTagToAttendanceType(AttendanceType attendanceType, RewardTag rewardTag){
        attendanceTypeMap.get(attendanceType.toString()).setRewardTag(rewardTag);
    }

    public AttendanceTypeReward readAttendanceTypeReward(AttendanceType attendanceType){
        return attendanceTypeMap.get(attendanceType.toString());
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