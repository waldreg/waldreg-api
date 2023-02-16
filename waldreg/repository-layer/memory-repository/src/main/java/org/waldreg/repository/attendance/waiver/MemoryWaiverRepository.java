package org.waldreg.repository.attendance.waiver;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.attendance.waiver.dto.WaiverDto;
import org.waldreg.attendance.waiver.spi.WaiverRepository;
import org.waldreg.domain.user.User;
import org.waldreg.domain.waiver.Waiver;
import org.waldreg.repository.MemoryAttendanceStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.MemoryWaiverStorage;
import org.waldreg.repository.attendance.waiver.mapper.MemoryWaiverMapper;

@Repository
public final class MemoryWaiverRepository implements WaiverRepository{

    private final MemoryAttendanceStorage memoryAttendanceStorage;
    private final MemoryWaiverStorage memoryWaiverStorage;
    private final MemoryUserStorage memoryUserStorage;
    private final MemoryWaiverMapper memoryWaiverMapper;

    @Autowired
    public MemoryWaiverRepository(MemoryAttendanceStorage memoryAttendanceStorage,
                                    MemoryWaiverStorage memoryWaiverStorage,
                                    MemoryUserStorage memoryUserStorage,
                                    MemoryWaiverMapper memoryWaiverMapper){
        this.memoryAttendanceStorage = memoryAttendanceStorage;
        this.memoryWaiverStorage = memoryWaiverStorage;
        this.memoryUserStorage = memoryUserStorage;
        this.memoryWaiverMapper = memoryWaiverMapper;
    }

    @Override
    public void waive(WaiverDto waiverRequest){
        User user = memoryUserStorage.readUserById(waiverRequest.getId());
        memoryWaiverStorage.addWaiver(memoryWaiverMapper.waiverDtoToWaiver(waiverRequest, user));
    }

    @Override
    public boolean isAttendanceTarget(int id){
        return memoryAttendanceStorage.isAttendanceTarget(id);
    }

    @Override
    public List<WaiverDto> readWaiverList(){
        List<Waiver> waiverList = memoryWaiverStorage.readWaiverList();
        return memoryWaiverMapper.waiverListToWaiverDtoList(waiverList);
    }

    @Override
    public Optional<WaiverDto> readWaiverByWaiverId(int waiverId){
        return Optional.empty();
    }

    @Override
    public void acceptWaiver(int waiverId, AttendanceType attendanceType){
        Waiver waiver = memoryWaiverStorage.readWaiverByWaiverId(waiverId);
        memoryAttendanceStorage.changeAttendance(waiver.getWaiverUser().getId(), waiver.getWaiverDate(), attendanceType);
        deleteWaiver(waiverId);
    }

    @Override
    public void deleteWaiver(int waiverId){

    }

}
