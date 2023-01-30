package org.waldreg.controller.schedule.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScheduleRepeatResponse{

    @JsonProperty("cycle")
    private int cycle;
    @JsonProperty("repeat_finish_at")
    private String repeatFinishAt;

    public ScheduleRepeatResponse(){}

    private ScheduleRepeatResponse(Builder builder){
        this.cycle = builder.cycle;
        this.repeatFinishAt = builder.repeatFinishAt;
    }

    public static Builder builder(){return new Builder();}

    public int getCycle(){
        return cycle;
    }

    public void setCycle(int cycle){
        this.cycle = cycle;
    }

    public String getRepeatFinishAt(){
        return repeatFinishAt;
    }

    public void setRepeatFinishAt(String repeatFinishAt){
        this.repeatFinishAt = repeatFinishAt;
    }

    public final static class Builder{

        private int cycle;
        private String repeatFinishAt;

        private Builder(){}

        public Builder cycle(int cycle){
            this.cycle = cycle;
            return this;
        }

        public Builder repeatFinishAt(String repeatFinishAt){
            this.repeatFinishAt = repeatFinishAt;
            return this;
        }

        public ScheduleRepeatResponse build(){return new ScheduleRepeatResponse(this);}

    }

}
