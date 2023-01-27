package org.wadlreg.reward.tag.lib;

import org.springframework.stereotype.Service;

@Service
public class TagExceedClipper{

    private final int maxTagTitleLength;
    private final int minTagPoint;
    private final int maxTagPoint;

    public TagExceedClipper(){
        this.maxTagTitleLength = 100;
        this.minTagPoint = -1000;
        this.maxTagPoint = 1000;
    }

    public String clipTagTitle(String title){
        if(title.length() <= maxTagTitleLength) return title;
        return title.substring(0, maxTagTitleLength);
    }

    public int clipTagPoint(int point){
        if(point < minTagPoint) return minTagPoint;
        return Math.min(point, maxTagPoint);
    }

}
