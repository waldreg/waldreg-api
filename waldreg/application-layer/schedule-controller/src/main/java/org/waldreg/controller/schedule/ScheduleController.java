package org.waldreg.controller.schedule;

import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.controller.schedule.mapper.ControllerScheduleMapper;
import org.waldreg.controller.schedule.request.ScheduleRequest;
import org.waldreg.controller.schedule.response.ScheduleResponse;
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

    @RequestMapping(value = "/calendar")
    public List<ScheduleResponse> readScheduleByTerm(@RequestParam("year") int year, @RequestParam("month") int month){
        List<ScheduleDto> scheduleDtoList = scheduleManager.readScheduleByTerm(year, month);
        return controllerScheduleMapper.scheduleDtoListToScheduleResponseList(scheduleDtoList);
    }

    @GetMapping("/schedule/{schedule-id}")
    public ScheduleResponse readScheduleById(@PathVariable("schedule-id") int id){
        ScheduleDto scheduleDto = scheduleManager.readScheduleById(id);
        return controllerScheduleMapper.scheduleDtoToScheduleResponse(scheduleDto);
    }

    @PutMapping("/schedule/{schedule-id}")
    public void updateSchedule(@PathVariable("schedule-id") int id, @RequestBody ScheduleRequest scheduleRequest){
        ScheduleDto scheduleDto = controllerScheduleMapper.scheduleRequestToScheduleDto(scheduleRequest);
        scheduleManager.updateScheduleById(id, scheduleDto);
    }



}
