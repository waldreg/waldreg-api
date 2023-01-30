package org.waldreg.controller.schedule.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ScheduleListResponse{

    @JsonProperty("schedules")
    private List<ScheduleResponse> scheduleList;

    public ScheduleListResponse(){}

    private ScheduleListResponse(Builder builder){
        this.scheduleList = builder.scheduleList;
    }

    public static Builder builder(){return new Builder();}

    public List<ScheduleResponse> getScheduleList(){return scheduleList;}

    public final static class Builder{

        private List<ScheduleResponse> scheduleList;

        private Builder(){}

        public Builder ScheduleList(List<ScheduleResponse> scheduleList){
            this.scheduleList = scheduleList;
            return this;
        }

        public ScheduleListResponse build(){return new ScheduleListResponse(this);}

    }

}

