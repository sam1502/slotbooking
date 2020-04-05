package com.postman.slotbooking.services;

import com.postman.slotbooking.exception.UserNotFoundException;
import com.postman.slotbooking.models.AvailableTimings;
import com.postman.slotbooking.models.PSchedleAvailable;
import com.postman.slotbooking.models.PUsers;
import com.postman.slotbooking.repository.SchedulingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SchedulingServiceImpl {

    @Autowired
    UserRegistrationImpl userService;

    @Autowired
    SchedulingRepository schedulingRepository;

    private final Logger logger = LoggerFactory.getLogger(SchedulingServiceImpl.class);

    public String persistMyTimings(AvailableTimings timings) throws Exception {
        checkDateOfSchedule(timings.getStartTime(), timings.getEndTime());
        PUsers user = getUserByUserName(timings.getUserName());
        int schedulesPersisted = 0;
        PSchedleAvailable schedleAvailable = new PSchedleAvailable();
        LocalDateTime start = timings.getStartTime();
        LocalDateTime end = start;


        while (end.isBefore(timings.getEndTime())) {
            end = start.plusMinutes(30);

            schedleAvailable.setId(UUID.randomUUID());
            schedleAvailable.setUserId(user.getId());
            schedleAvailable.setStartTime(start);
            schedleAvailable.setEndTime(end);
            schedleAvailable.setAvailable(true);

            schedulingRepository.saveAndFlush(schedleAvailable);
            start = end;
            schedulesPersisted++;
        }

        return String.format("Persisted %s schedules in interval of 30 minutes.", schedulesPersisted);
    }

    public Map<String, String> bookSchedule(AvailableTimings timings, String bookingFor)  throws Exception{
        LocalDateTime endTime = timings.getStartTime().plusHours(1);
        checkDateOfSchedule(timings.getStartTime(),endTime);

        Map<String, String> response = new HashMap<>();
        if (scheduleAvailableIfNotPersistWithFalseStatus(timings, bookingFor)) {
            PUsers user = getUserByUserName(timings.getUserName());
            if(user != null) {
                List<PSchedleAvailable> schedleAvailables = schedulingRepository.getAvailableSlotsWithTrueStatus(timings.getStartTime(), endTime, user.getId());
                String responseString = String.format("%s your schedule is booked with %s for %s on time %s", bookingFor, user.getUserName(), Timestamp.valueOf(timings.getStartTime()),"");
                if (schedleAvailables.size() == 2) {
                    for (PSchedleAvailable schedules : schedleAvailables) {
                        schedules.setAvailable(false);
                        schedules.setRemarks(timings.getRemarks());
                        schedules.setBookedByUsername(bookingFor);
                        schedulingRepository.saveAndFlush(schedules);
                        response.put("Schedules booked", responseString);
                    }
                }
            } else {
                logger.error("user with username : {} not found", timings.getUserName());
                throw new UserNotFoundException("User not found");
            }
        } else {
            response.put("Error", String.format("The time slot requested for user %s is not available", bookingFor));
        }
        return new HashMap<>();
    }


    /**
     * Helper methods
     */

    public void checkDateOfSchedule(LocalDateTime startTime, LocalDateTime endTime) throws Exception {
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new Exception("Start time is incorrect please check.");
        }
        if(endTime.isBefore(startTime)) {
            throw new Exception("End time cannot be before start time");
        }
    }

    public PUsers getUserByUserName(String username) {
        return userService.findUserByUserName(username);
    }

    public boolean scheduleAvailableIfNotPersistWithFalseStatus(AvailableTimings timings, String username) {
        boolean available;
        PUsers user = getUserByUserName(username);
        List<PSchedleAvailable> schedleAvailables = schedulingRepository.getAvailableSlot(timings.getStartTime(), timings.getEndTime(), user.getId());

        if (!schedleAvailables.isEmpty()) {
            PSchedleAvailable schedule = schedleAvailables.get(0);
            available = schedule.isAvailable();
        } else {
            available = true;
            LocalDateTime start = timings.getStartTime();
            LocalDateTime end = start;
            while (end.isBefore(timings.getEndTime())) {
                PSchedleAvailable schedleAvailable = new PSchedleAvailable();
                end = start.plusMinutes(30);
                schedleAvailable.setId(UUID.randomUUID());
                schedleAvailable.setUserId(user.getId());
                schedleAvailable.setStartTime(start);
                schedleAvailable.setEndTime(end);
                schedleAvailable.setAvailable(false);
                schedleAvailable.setRemarks("Persisted while user booked schedule with user");
                schedulingRepository.saveAndFlush(schedleAvailable);
                start = end;
            }
        }
        return available;
    }
}
