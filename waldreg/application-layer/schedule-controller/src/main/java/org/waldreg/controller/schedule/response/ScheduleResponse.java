package org.waldreg.controller.schedule.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScheduleResponse{

    @JsonProperty("id")
    private int id;
    @JsonProperty("schedule_title")
    private String scheduleTitle;
    @JsonProperty("scheduleContent")
    private String scheduleContent;
    @JsonProperty("startedAt")
    private String startedAt;
    @JsonProperty("finishAt")
    private String finishAt;
    @JsonProperty("repeat")
    private ScheduleRepeatResponse repeat = null;

    private ScheduleResponse(){}

    private ScheduleResponse(Builder builder){
        this.id = builder.id;
        this.scheduleTitle = builder.scheduleTitle;
        this.scheduleContent = builder.scheduleContent;
        this.startedAt = builder.startedAt;
        this.finishAt = builder.finishAt;
        this.repeat = builder.repeat;
    }

    public static Builder builder(){return new Builder();}

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

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

    public ScheduleRepeatResponse getRepeat(){
        return repeat;
    }

    public void setRepeat(ScheduleRepeatResponse repeat){
        this.repeat = repeat;
    }

    public final static class Builder{

        private int id;
        private String scheduleTitle;
        private String scheduleContent;
        private String startedAt;
        private String finishAt;
        private ScheduleRepeatResponse repeat;

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

        public Builder repeat(ScheduleRepeatResponse repeat){
            this.repeat = repeat;
            return this;
        }

        public ScheduleResponse build(){return new ScheduleResponse(this);}

    }


}
