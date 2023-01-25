package org.waldreg.domain.calendar;

import java.time.LocalDateTime;

public final class Schedule{

    private final int id;
    private final String scheduleTitle;
    private final String scheduleContent;
    private final LocalDateTime startedAt;
    private final LocalDateTime finishAt;
    private final ScheduleRepeat scheduleRepeat;

    private Schedule(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Schedule()\"");
    }

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