package org.waldreg.repository.attendance.waiver.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.waldreg.attendance.waiver.dto.WaiverDto;
import org.waldreg.domain.user.User;
import org.waldreg.domain.waiver.Waiver;

@Service
public final class MemoryWaiverMapper{

    public Waiver waiverDtoToWaiver(WaiverDto waiverDto, User user){
        return Waiver.builder()
                .waiverUser(user)
                .waiverReason(waiverDto.getWaiverReason())
                .waiverDate(waiverDto.getWaiverDate())
                .build();
    }

    public List<WaiverDto> waiverListToWaiverDtoList(List<Waiver> waiverList){
        return waiverList.stream()
                .map(w -> WaiverDto.builder()
                        .waiverId(w.getWaiverId())
                        .id(w.getWaiverUser().getId())
                        .userId(w.getWaiverUser().getUserId())
                        .userName(w.getWaiverUser().getName())
                        .waiverDate(w.getWaiverDate())
                        .waiverReason(w.getWaiverReason())
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
