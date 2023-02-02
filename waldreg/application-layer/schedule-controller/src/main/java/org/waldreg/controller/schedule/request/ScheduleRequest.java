package org.waldreg.controller.schedule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;

public class ScheduleRequest{

    @NotBlank(message = "SCHEDULE-407 Schedule title cannot be blank")
    @JsonProperty("schedule_title")
    private String scheduleTitle;
    @JsonProperty("scheduleContent")
    private String scheduleContent;
    @JsonProperty("startedAt")
    private String startedAt;
    @JsonProperty("finishAt")
    private String finishAt;
    @JsonProperty("repeat")
    private ScheduleRepeatRequest repeat = null;

    private ScheduleRequest(){}

    private ScheduleRequest(Builder builder){
        this.scheduleTitle = builder.scheduleTitle;
        this.scheduleContent = builder.scheduleContent;
        this.startedAt = builder.startedAt;
        this.finishAt = builder.finishAt;
        this.repeat = builder.repeat;
    }

    public static Builder builder(){return new Builder();}

    public String getScheduleTitle(){
        return scheduleTitle;
    }

    public void setScheduleTitle(String scheduleTitle){
        this.scheduleTitle = scheduleTitle;
    }

    public String getScheduleContent(){
        return scheduleContent;
    }

    public void setScheduleContent(String scheduleContent){
        this.scheduleContent = scheduleContent;
    }

    public String getStartedAt(){
        return startedAt;
    }

    public void setStartedAt(String startedAt){
        this.startedAt = startedAt;
    }

    public String getFinishAt(){
        return finishAt;
    }

    public void setFinishAt(String finishAt){
        this.finishAt = finishAt;
    }

    public ScheduleRepeatRequest getRepeat(){
        return repeat;
    }

    public void setRepeat(ScheduleRepeatRequest repeat){
        this.repeat = repeat;
    }

    public final static class Builder{

        private String scheduleTitle;
        private String scheduleContent;
        private String startedAt;
        private String finishAt;
        private ScheduleRepeatRequest repeat;

        private Builder(){}

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

        public Builder repeat(ScheduleRepeatRequest repeat){
            this.repeat = repeat;
            return this;
        }

        public ScheduleRequest build(){return new ScheduleRequest(this);}

    }

}


