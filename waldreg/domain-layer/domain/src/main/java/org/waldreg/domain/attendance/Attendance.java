package org.waldreg.domain.attendance;

import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ATTENDANCE")
public final class Attendance{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATTENDANCE_ATTENDANCE_ID")
    private Integer attendanceId;

    @OneToOne(orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTENDANCE_USER_ATTENDANCE_USER_ID", nullable = false)
    private AttendanceUser attendanceUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTENDANCE_TYPE_REWARD_ID", nullable = false)
    private AttendanceTypeReward attendanceType;

    @Column(name = "ATTENDANCE_ATTENDANCE_DATE", nullable = false)
    private LocalDate attendanceDate;

    private Attendance(){}

    private Attendance(Builder builder){
        this.attendanceUser = builder.attendanceUser;
        this.attendanceType = builder.attendanceType;
        this.attendanceDate = builder.attendanceDate;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getAttendanceId(){
        return attendanceId;
    }

    public AttendanceUser getAttendanceUser(){
        return attendanceUser;
    }

    public AttendanceTypeReward getAttendanceType(){
        return attendanceType;
    }

    public void setAttendanceType(AttendanceTypeReward attendanceType){
        this.attendanceType = attendanceType;
    }

    public LocalDate getAttendanceDate(){
        return attendanceDate;
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof Attendance)){
            return false;
        }
        Attendance attendance = (Attendance)object;
        return attendance.getAttendanceDate().equals(this.attendanceDate)
                && Objects.equals(attendance.getAttendanceUser().getUser().getId(), this.attendanceUser.getUser().getId());
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.attendanceDate, this.attendanceUser.getUser().getId());
    }

    public static final class Builder{

        private AttendanceUser attendanceUser;
        private AttendanceTypeReward attendanceType;
        private LocalDate attendanceDate;

        private Builder(){}

        public Builder user(AttendanceUser attendanceUser){
            this.attendanceUser = attendanceUser;
            return this;
        }

        public Builder attendanceType(AttendanceTypeReward attendanceType){
            this.attendanceType = attendanceType;
            return this;
        }

        public Builder attendanceDate(LocalDate attendanceDate){
            this.attendanceDate = attendanceDate;
            return this;
        }

        public Attendance build(){
            return new Attendance(this);
        }

    }


}
