package org.waldreg.domain.point;

import java.time.LocalDateTime;

public interface Point{

    abstract class PointBuilder<R extends Point>{

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
