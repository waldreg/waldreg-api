package org.waldreg.reward.tag.lib;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TagExceedClipper.class)
public class TagExceedClipperTest{

    @Autowired
    private TagExceedClipper clipper;

    @Test
    @DisplayName("제목이 100자를 초과한경우 100자로 맞추는 테스트")
    public void CLIP_EXCEED_TITLE_TEST(){
        // given
        String rewardTagTitle = "123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 ";

        // when
        String result = clipper.clipTagTitle(rewardTagTitle);

        // then
        Assertions.assertEquals(100, result.length());
    }

    @Test
    @DisplayName("점수가 -1000과 1000사이가 아닌 경우 사이로 맞추는 테스트")
    public void CLIP_TAG_POINT_TEST(){
        // given
        int point1 = 1001;
        int point2 = -1001;

        // when
        int result1 = clipper.clipTagPoint(point1);
        int result2 = clipper.clipTagPoint(point2);

        // then
        Assertions.assertAll(
                ()->Assertions.assertEquals(1000, result1),
                ()-> Assertions.assertEquals(-1000, result2)
        );
    }

}
