package org.waldreg.repository.schedule;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.domain.calendar.Schedule;
import org.waldreg.domain.calendar.ScheduleRepeat;
import org.waldreg.repository.schedule.mapper.ScheduleRepositoryMapper;
import org.waldreg.repository.schedule.repository.JpaScheduleRepository;
import org.waldreg.schedule.dto.RepeatDto;
import org.waldreg.schedule.dto.ScheduleDto;
import org.waldreg.schedule.spi.ScheduleRepository;

@Repository
public class ScheduleRepositoryServiceProvider implements ScheduleRepository{

    private final JpaScheduleRepository jpaScheduleRepository;
    private final ScheduleRepositoryMapper scheduleRepositoryMapper;

    public ScheduleRepositoryServiceProvider(JpaScheduleRepository jpaScheduleRepository, ScheduleRepositoryMapper scheduleRepositoryMapper){
        this.jpaScheduleRepository = jpaScheduleRepository;
        this.scheduleRepositoryMapper = scheduleRepositoryMapper;
    }

    @Override
    @Transactional
    public void createSchedule(ScheduleDto scheduleDto){
        Schedule schedule = scheduleRepositoryMapper.scheduleDtoToSchedule(scheduleDto);
        jpaScheduleRepository.save(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleDto readScheduleById(int id){
        Schedule schedule = jpaScheduleRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find schedule with id \"" + id + "\"");}
        );
        return scheduleRepositoryMapper.scheduleToScheduleDto(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleDto> readScheduleByTerm(int year, int month){
        List<Schedule> scheduleList = jpaScheduleRepository.findScheduleByTerm(year, month);
        return scheduleRepositoryMapper.scheduleListToScheduleDtoList(scheduleList);
    }

    @Override
    @Transactional
    public void updateScheduleById(int id, ScheduleDto scheduleDto){
        Schedule schedule = jpaScheduleRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find schedule with id \"" + id + "\"");}
        );
        schedule.setScheduleTitle(scheduleDto.getScheduleTitle());
        schedule.setScheduleContent(scheduleDto.getScheduleContent());
        schedule.setStartedAt(LocalDateTime.parse(scheduleDto.getStartedAt()));
        schedule.setFinishAt(LocalDateTime.parse(scheduleDto.getFinishAt()));
        schedule.setScheduleRepeat(null);
        if (isExistRepeat(scheduleDto.getRepeatDto())){
            schedule.setScheduleRepeat(buildScheduleRepeat(scheduleDto.getRepeatDto()));
        }
    }

    private boolean isExistRepeat(RepeatDto repeatDto){
        return repeatDto != null;
    }

    private ScheduleRepeat buildScheduleRepeat(RepeatDto repeatDto){
        return ScheduleRepeat.builder()
                .cycle(repeatDto.getCycle())
                .repeatFinishAt(LocalDateTime.parse(repeatDto.getRepeatFinishAt()))
                .build();
    }

    @Override
    @Transactional
    public void deleteScheduleById(int id){
        jpaScheduleRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find schedule with id \"" + id + "\"");}
        );
        jpaScheduleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistScheduleId(int id){
        return jpaScheduleRepository.existsById(id);
    }

}
