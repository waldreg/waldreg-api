package org.waldreg.domain.point;

import java.time.LocalDateTime;

public abstract class Point{

    private final String pointInfo;
    private final LocalDateTime pointPresentedAt;
    private final int point;

    Point(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Point()\"");
    }

    public Point(PointBuilder<? extends Point> builder){
        this.pointInfo = builder.info;
        this.pointPresentedAt = builder.presentedAt;
        this.point = builder.point;
    }

    public String getPointInfo(){
        return this.pointInfo;
    }

    public LocalDateTime getPointPresentedAt(){
        return this.pointPresentedAt;
    }

    public int getPoint(){
        return this.point;
    }

    public abstract static class PointBuilder<R extends Point>{

        String info;
        LocalDateTime presentedAt;
        int point;

        PointBuilder(){}

        public PointBuilder<R> info(String info){
            this.info = info;
            return this;
        }

        public PointBuilder<R> presentedAt(LocalDateTime presentedAt){
            this.presentedAt = presentedAt;
            return this;
        }

        public PointBuilder<R> point(int point){
            this.point = point;
            return this;
        }

        public abstract Point build();

    }

}
