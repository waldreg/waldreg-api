package org.waldreg.domain.attendance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.waldreg.domain.user.User;

@Entity
@Table(name = "ATTENDANCE_USER")
public final class AttendanceUser{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATTENDANCE_USER_ATTENDANCE_USER_ID")
    private Integer attendanceUserId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false, unique = true)
    private User user;

    private AttendanceUser(){}

    private AttendanceUser(Builder builder){
        this.user = builder.user;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getAttendanceUserId(){
        return attendanceUserId;
    }

    public User getUser(){
        return user;
    }

    public static final class Builder{

        private User user;

        private Builder(){}

        public Builder user(User user){
            this.user = user;
            return this;
        }

        public AttendanceUser build(){
            return new AttendanceUser(this);
        }

    }

}
