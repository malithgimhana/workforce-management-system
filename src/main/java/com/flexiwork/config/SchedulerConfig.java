package com.flexiwork.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    // Scheduling is enabled via @EnableScheduling on FlexiWorkApplication
    // This class exists as a named config for scheduler-related beans if needed
}
