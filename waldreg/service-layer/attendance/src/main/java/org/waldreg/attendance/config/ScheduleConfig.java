package org.waldreg.attendance.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = "org.waldreg.attendance.schedule")
public class ScheduleConfig{}
