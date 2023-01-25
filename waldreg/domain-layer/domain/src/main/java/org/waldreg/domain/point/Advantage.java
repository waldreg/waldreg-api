package org.waldreg.domain.point;

import java.time.LocalDateTime;

public final class Advantage implements Point{

    private final String advantageInfo;
    private final LocalDateTime advantagePresentedAt;
    private final int advantagePoint;

    private Advantage(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Advantage()\"");
    }

    public Advantage(PointBuilder<Advantage> builder){
        this.advantageInfo = builder.info;
        this.advantagePresentedAt = builder.presentedAt;
        this.advantagePoint = builder.point;
    }

    public static PointBuilder<Advantage> builder(){
        return new PointBuilder<>(){
            @Override
            public Point build(){
                return new Advantage(this);
            }
        };
    }

    public String getAdvantageInfo(){
        return advantageInfo;
    }

    public LocalDateTime getAdvantagePresentedAt(){
        return advantagePresentedAt;
    }

    public int getAdvantagePoint(){
        return advantagePoint;
    }

}
