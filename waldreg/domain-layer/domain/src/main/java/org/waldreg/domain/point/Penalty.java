package org.waldreg.domain.point;

import java.time.LocalDateTime;

public final class Penalty implements Point{

    private final String penaltyInfo;
    private final LocalDateTime penaltyPresentedAt;
    private final int penaltyPoint;

    private Penalty(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Penalty()\"");
    }

    public Penalty(PointBuilder<Penalty> builder){
        this.penaltyInfo = builder.info;
        this.penaltyPresentedAt = builder.presentedAt;
        this.penaltyPoint = builder.point;
    }

    public static PointBuilder<Penalty> builder(){
        return new PointBuilder<>(){
            @Override
            public Point build(){
                return new Penalty(this);
            }
        };
    }

    public String getPenaltyInfo(){
        return penaltyInfo;
    }

    public LocalDateTime getPenaltyPresentedAt(){
        return penaltyPresentedAt;
    }

    public int getPenaltyPoint(){
        return penaltyPoint;
    }

}
