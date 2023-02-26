package org.waldreg.repository.attendance.waiver.mapper;

import org.springframework.stereotype.Component;
import org.waldreg.attendance.waiver.dto.WaiverDto;
import org.waldreg.domain.user.User;
import org.waldreg.domain.waiver.Waiver;

@Component
public class WaiverMapper{

    public Waiver waiverDtoToWaiver(WaiverDto waiverDto, User user){
        return Waiver.builder()
                .waiverUser(user)
                .waiverDate(waiverDto.getWaiverDate())
                .waiverReason(waiverDto.getWaiverReason())
                .build();
    }

    public WaiverDto waiverToWaiverDto(Waiver waiver){
        return WaiverDto.builder()
                .id(waiver.getWaiverUser().getId())
                .userId(waiver.getWaiverUser().getUserId())
                .userName(waiver.getWaiverUser().getName())
                .waiverId(waiver.getWaiverId())
                .waiverDate(waiver.getWaiverDate())
                .waiverReason(waiver.getWaiverReason())
                .build();
    }

}
