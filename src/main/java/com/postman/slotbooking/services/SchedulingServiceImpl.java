package com.postman.slotbooking.services;

import com.postman.slotbooking.models.AvailableTimings;
import com.postman.slotbooking.models.PSchedleAvailable;
import com.postman.slotbooking.models.PUsers;
import com.postman.slotbooking.repository.SchedulingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SchedulingServiceImpl {

    @Autowired
    UserRegistrationImpl userService;

    @Autowired
    SchedulingRepository schedulingRepository;

    public boolean persistMyTimings(AvailableTimings timings) throws Exception{
        checkDateOfSchedule(timings.getStartTime());

        boolean shcedulesPersisted = false;
        PUsers user = userService.findUserByUserName(timings.getUserName());

        List<PSchedleAvailable> schedleAvailables = new ArrayList<>();

        PSchedleAvailable schedleAvailable = new PSchedleAvailable();
        LocalDateTime start = timings.getStartTime();
        LocalDateTime end = start;


        while(end.isBefore(timings.getEndTime())) {
            end = start.plusMinutes(30);

            schedleAvailable.setId(UUID.randomUUID());
            schedleAvailable.setUserId(user.getId());
            schedleAvailable.setStartTime(start);
            schedleAvailable.setEndTime(end);
            schedleAvailable.setAvailable(true);

            schedulingRepository.saveAndFlush(schedleAvailable);
            schedleAvailables.add(schedleAvailable);
            start = end;
            shcedulesPersisted = true;
        }

        return shcedulesPersisted;
    }


    /**
     * Helper methods
     */

    private void checkDateOfSchedule(LocalDateTime startTime)  throws Exception{
        if(startTime.isBefore(LocalDateTime.now())) {
            throw new Exception("Start time is incorrect please check.");
        }
    }
}
