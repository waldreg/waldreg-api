package org.waldreg.controller.board.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MemberTierList{

    @JsonProperty("member_tier")
    private MemberTier memberTiers[];

    public MemberTierList(MemberTier[] memberTiers){
        this.memberTiers = memberTiers;
    }

}
