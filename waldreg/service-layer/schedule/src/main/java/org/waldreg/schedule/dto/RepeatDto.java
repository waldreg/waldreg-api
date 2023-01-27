package org.waldreg.schedule.dto;

public class RepeatDto{

    private final int cycle;
    private final String repeatFinishAt;

    private RepeatDto(){
        throw new UnsupportedOperationException("Can not invoke constructor \"RepeatDto()\"");
    }

    private RepeatDto(Builder builder){
        this.cycle = builder.cycle;
        this.repeatFinishAt = builder.repeatFinishAt;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getCycle(){
        return cycle;
    }

    public String getRepeatFinishAt(){
        return repeatFinishAt;
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

        public RepeatDto build(){
            return new RepeatDto(this);
        }

    }

}
