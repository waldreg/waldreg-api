package org.waldreg.domain.point;

public final class Penalty extends Point{

    private Penalty(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Penalty()\"");
    }

    public Penalty(PointBuilder<Penalty> builder){
        super(builder);
    }

    public static PointBuilder<Penalty> builder(){
        return new PointBuilder<>(){
            @Override
            public Point build(){
                return new Penalty(this);
            }
        };
    }

}
