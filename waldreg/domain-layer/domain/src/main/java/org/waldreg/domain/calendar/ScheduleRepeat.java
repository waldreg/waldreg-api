package org.waldreg.domain.calendar;

import java.time.LocalDateTime;

public final class ScheduleRepeat{

    private int cycle;
    private LocalDateTime repeatFinishAt;

    private ScheduleRepeat(){
        throw new UnsupportedOperationException("Can not invoke constructor \"ScheduleRepeat()\"");
    }

    private ScheduleRepeat(Builder builder){
        this.cycle = builder.cycle;
        this.repeatFinishAt = builder.repeatFinishAt;
    }

    public int getCycle(){
        return cycle;
    }

    public void setCycle(int cycle){
        this.cycle = cycle;
    }

    public LocalDateTime getRepeatFinishAt(){
        return repeatFinishAt;
    }

    public void setRepeatFinishAt(LocalDateTime repeatFinishAt){
        this.repeatFinishAt = repeatFinishAt;
    }

    public static Builder builder(){
        return new Builder();
    }

    public final static class Builder{

        private int cycle;
        private LocalDateTime repeatFinishAt;

        private Builder(){}

        public Builder cycle(int cycle){
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
