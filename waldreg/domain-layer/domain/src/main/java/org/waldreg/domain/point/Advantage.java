package org.waldreg.domain.point;

import java.time.LocalDateTime;

public final class Advantage extends Point{

    private Advantage(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Advantage()\"");
    }

    public Advantage(PointBuilder<Advantage> builder){
        super(builder);
    }

    public static PointBuilder<Advantage> builder(){
        return new PointBuilder<>(){
            @Override
            public Point build(){
                return new Advantage(this);
            }
        };
    }

}
