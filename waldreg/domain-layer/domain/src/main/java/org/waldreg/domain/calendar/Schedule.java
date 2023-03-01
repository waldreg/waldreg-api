package org.waldreg.domain.calendar;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SCHEDULE")
public final class Schedule{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHEDULE_ID")
    private Integer id;

    @Column(name = "SCHEDULE_TITLE", nullable = false, length = 1000)
    private String scheduleTitle;

    @Column(name = "SCHEDULE_CONTENT", length = 1000)
    private String scheduleContent;

    @Column(name = "SCHEDULE_STARTED_AT", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "SCHEDULE_FINISH_AT", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime finishAt;

    @Embedded
    private ScheduleRepeat scheduleRepeat;

    private Schedule(){}

    private Schedule(Builder builder){
        this.id = builder.id;
        this.scheduleTitle = builder.scheduleTitle;
        this.scheduleContent = builder.scheduleContent;
        this.startedAt = builder.startedAt;
        this.finishAt = builder.finishAt;
        this.scheduleRepeat = builder.scheduleRepeat;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }

    public String getScheduleTitle(){
        return scheduleTitle;
    }

    public LocalDateTime getStartedAt(){
        return startedAt;
    }

    public LocalDateTime getFinishAt(){
        return finishAt;
    }

    public String getScheduleContent(){
        return scheduleContent;
    }

    public ScheduleRepeat getScheduleRepeat(){
        return scheduleRepeat;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setScheduleTitle(String scheduleTitle){
        this.scheduleTitle = scheduleTitle;
    }

    public void setScheduleContent(String scheduleContent){
        this.scheduleContent = scheduleContent;
    }

    public void setStartedAt(LocalDateTime startedAt){
        this.startedAt = startedAt;
    }

    public void setFinishAt(LocalDateTime finishAt){
        this.finishAt = finishAt;
    }

    public void setScheduleRepeat(ScheduleRepeat scheduleRepeat){
        this.scheduleRepeat = scheduleRepeat;
    }

    public final static class Builder{

        private int id;
        private String scheduleTitle;
        private String scheduleContent;
        private LocalDateTime startedAt;
        private LocalDateTime finishAt;
        private ScheduleRepeat scheduleRepeat;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder scheduleTitle(String scheduleTitle){
            this.scheduleTitle = scheduleTitle;
            return this;
        }

        public Builder scheduleContent(String scheduleContent){
            this.scheduleContent = scheduleContent;
            return this;
        }

        public Builder startedAt(LocalDateTime startedAt){
            this.startedAt = startedAt;
            return this;
        }

        public Builder finishAt(LocalDateTime finishAt){
            this.finishAt = finishAt;
            return this;
        }

        public Builder scheduleRepeat(ScheduleRepeat scheduleRepeat){
            this.scheduleRepeat = scheduleRepeat;
            return this;
        }

        public Schedule build(){
            return new Schedule(this);
        }

    }

}
