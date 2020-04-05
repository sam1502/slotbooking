package com.postman.slotbooking.resources;

import com.postman.slotbooking.models.AvailableTimings;
import com.postman.slotbooking.services.SchedulingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/slots")
public class SchedulingResource {

    @Autowired
    SchedulingServiceImpl schedulingService;


    @PostMapping(value = "/available", consumes = "application/json")
    public String myAvailableSlotsForADay(@RequestBody AvailableTimings timings) throws Exception{
        return schedulingService.persistMyTimings(timings);
    }

    @PostMapping(value = "/bookschedule")
    public ResponseEntity<Map<String,String>> bookMeetingSlots(@RequestBody AvailableTimings timings) throws Exception{
        Map<String, String> response = new HashMap<>();
        String currentUserName = getCurrentUserName();
        try {
             response = schedulingService.bookSchedule(timings, currentUserName);
        } catch (UsernameNotFoundException e) {
            String res = String.format("User %s not found",timings.getUserName());
            response.put("Error", res);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    public String getCurrentUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username="";
        if(principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        }
        return username;
    }
}
