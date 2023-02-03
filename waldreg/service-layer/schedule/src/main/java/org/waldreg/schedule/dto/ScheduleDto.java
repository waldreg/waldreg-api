package org.waldreg.schedule.dto;

public class ScheduleDto{

    private final int id;
    private final String scheduleTitle;
    private final String scheduleContent;
    private final String startedAt;
    private final String finishAt;
    private final RepeatDto repeatDto;

    private ScheduleDto(){throw new UnsupportedOperationException("Can not invoke constructor \"ScheduleDto()\"");}

    private ScheduleDto(Builder builder){
        this.id = builder.id;
        this.scheduleTitle = builder.scheduleTitle;
        this.scheduleContent = builder.scheduleContent;
        this.startedAt = builder.startedAt;
        this.finishAt = builder.finishAt;
        this.repeatDto = builder.repeatDto;
    }

    public static Builder builder(){return new Builder();}

    public int getId(){
        return id;
    }

    public String getScheduleTitle(){
        return scheduleTitle;
    }

    public String getScheduleContent(){
        return scheduleContent;
    }

    public String getStartedAt(){
        return startedAt;
    }

    public String getFinishAt(){
        return finishAt;
    }

    public RepeatDto getRepeatDto(){
        return repeatDto;
    }


    public final static class Builder{

        private int id;
        private String scheduleTitle;
        private String scheduleContent;
        private String startedAt;
        private String finishAt;
        private RepeatDto repeatDto;

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

        public Builder startedAt(String startedAt){
            this.startedAt = startedAt;
            return this;
        }

        public Builder finishAt(String finishAt){
            this.finishAt = finishAt;
            return this;
        }

        public Builder repeatDto(RepeatDto repeatDto){
            this.repeatDto = repeatDto;
            return this;
        }

        public ScheduleDto build(){return new ScheduleDto(this);}

    }

}
