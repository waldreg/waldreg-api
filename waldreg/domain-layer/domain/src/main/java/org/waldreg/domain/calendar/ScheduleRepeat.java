package org.waldreg.domain.calendar;

import java.time.LocalDateTime;

public final class ScheduleRepeat{

    private final int cycle;
    private final LocalDateTime repeatFinishAt;

    private ScheduleRepeat(){
        throw new UnsupportedOperationException("Can not invoke constructor \"ScheduleRepeat()\"");
    }

    private ScheduleRepeat(Builder builder){
        this.cycle = builder.cycle;
        this.repeatFinishAt = builder.repeatFinishAt;
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
