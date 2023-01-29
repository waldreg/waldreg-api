package org.waldreg.controller.board.response.memberTier;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MemberTierListResponse{

    @JsonProperty("member_tier")
    private MemberTier memberTiers[];

    public MemberTierListResponse(MemberTier[] memberTiers){
        this.memberTiers = memberTiers;
    }

}
