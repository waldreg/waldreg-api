package org.waldreg.domain.calendar;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public final class ScheduleRepeat{

    @Column(name = "SCHEDULE_REPEAT_CYCLE")
    private Integer cycle;

    @Column(name = "SCHEDULE_REPEAT_REPEAT_FINISH_AT", columnDefinition = "TIMESTAMP")
    private LocalDateTime repeatFinishAt;

    private ScheduleRepeat(){}

    private ScheduleRepeat(Builder builder){
        this.cycle = builder.cycle;
        this.repeatFinishAt = builder.repeatFinishAt;
    }

    public Integer getCycle(){
        return cycle;
    }

    public LocalDateTime getRepeatFinishAt(){
        return repeatFinishAt;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static final class Builder{

        private Integer cycle;
        private LocalDateTime repeatFinishAt;

        private Builder(){}

        public Builder cycle(Integer cycle){
            this.cycle = cycle;
            return this;
        }

        public Builder repeatFinishAt(LocalDateTime repeatFinishAt){
            this.repeatFinishAt = repeatFinishAt;
            return this;
        }

        public ScheduleRepeat build(){
            return new ScheduleRepeat(this);
        }

    }

}
