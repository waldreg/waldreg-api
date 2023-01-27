package org.wadlreg.reward.tag.lib;

import org.springframework.stereotype.Service;

@Service
public class TagExceedClipper{

    private final int maxTagTitleLength = 100;
    private final int minTagPoint = -1000;
    private final int maxTagPoint = 1000;

    public String clipTagTitle(String title){
        if(title.length() <= maxTagTitleLength) return title;
        return title.substring(0, maxTagTitleLength);
    }

    public int clipTagPoint(int point){
        if(point < minTagPoint) return minTagPoint;
        return Math.min(point, maxTagPoint);
    }

}
