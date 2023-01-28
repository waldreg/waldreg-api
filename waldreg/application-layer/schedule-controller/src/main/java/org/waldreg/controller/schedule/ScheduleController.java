package org.waldreg.controller.schedule;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.controller.schedule.mapper.ControllerScheduleMapper;
import org.waldreg.controller.schedule.request.ScheduleRequest;
import org.waldreg.schedule.dto.ScheduleDto;
import org.waldreg.schedule.management.ScheduleManager;

@RestController
public class ScheduleController{

    private final ScheduleManager scheduleManager;

    private final ControllerScheduleMapper controllerScheduleMapper;

    public ScheduleController(ScheduleManager scheduleManager, ControllerScheduleMapper controllerScheduleMapper){
        this.scheduleManager = scheduleManager;
        this.controllerScheduleMapper = controllerScheduleMapper;
    }

    @PostMapping("/schedule")
    public void createSchedule(@RequestBody @Validated ScheduleRequest scheduleRequest){
        ScheduleDto scheduleDto = controllerScheduleMapper.scheduleRequestToScheduleDto(scheduleRequest);
        scheduleManager.createSchedule(scheduleDto);
    }

}
