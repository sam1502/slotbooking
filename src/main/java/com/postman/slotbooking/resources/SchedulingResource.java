package com.postman.slotbooking.resources;

import com.postman.slotbooking.models.AvailableTimings;
import com.postman.slotbooking.models.PSchedleAvailable;
import com.postman.slotbooking.services.SchedulingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/slots")
public class SchedulingResource {

    @Autowired
    SchedulingServiceImpl schedulingService;

    @PostMapping(value = "/available", consumes = "application/json")
    public boolean myAvailableSlotsForADay(@RequestBody AvailableTimings timings) throws Exception{
        return schedulingService.persistMyTimings(timings);
    }
}
