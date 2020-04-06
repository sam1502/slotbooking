package com.postman.slotbooking.services;

import com.postman.slotbooking.exception.SlotNotAvailableException;
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

    public Map<String,String> persistMyTimings(AvailableTimings timings) throws Exception {
        checkDateOfSchedule(timings.getStartTime(), timings.getEndTime());
        Map<String, String> response = new HashMap<>();
        if(!isOverridingSchedules(timings)) {
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
            response.put("Message", String.format("Persisted %s schedules in interval of 30 minutes.", schedulesPersisted));
        } else {
            response.put("Message", "Timings are already persisted cannot override");
        }
        return response;
    }

    public Map<String, String> bookSchedule(AvailableTimings timings, String bookingUserName)  throws Exception{
        LocalDateTime endTime = timings.getStartTime().plusHours(1);
        timings.setEndTime(endTime);
        checkDateOfSchedule(timings.getStartTime(),endTime);

        Map<String, String> response = new HashMap<>();
        if (scheduleAvailableIfNotPersistWithFalseStatus(timings, bookingUserName)) {
            PUsers user = getUserByUserName(timings.getUserName());
            if(user != null) {
                List<PSchedleAvailable> schedleAvailables = schedulingRepository.getAvailableSlotsWithTrueStatus(timings.getStartTime(), endTime, user.getId());
                if (schedleAvailables.size() == 2) {
                    String responseString = String.format("%s your schedule is booked with %s for %s on time %s", bookingUserName, user.getUserName(), Timestamp.valueOf(timings.getStartTime()),"");
                    for (PSchedleAvailable schedules : schedleAvailables) {
                        schedules.setAvailable(false);
                        schedules.setRemarks(timings.getRemarks());
                        schedules.setBookedByUsername(bookingUserName);
                        schedulingRepository.saveAndFlush(schedules);
                        response.put("Schedules booked", responseString);
                    }
                } else {
                    revertBookedSlotsForOtherUser(timings,bookingUserName);
                    throw new SlotNotAvailableException(String.format("The time slot requested for user %s is not available", timings.getUserName()));
                }
            } else {
                logger.error("user with username : {} not found", timings.getUserName());
                throw new UserNotFoundException("User not found");
            }
        } else {
            throw new SlotNotAvailableException(String.format("The time slot requested for user %s is not available", bookingUserName));
        }
        return response;
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

    public boolean scheduleAvailableIfNotPersistWithFalseStatus(AvailableTimings timings, String bookingUserName) {
        boolean available = false;
        PUsers user = getUserByUserName(bookingUserName);
        List<PSchedleAvailable> schedleAvailables = schedulingRepository.getAvailableSlot(timings.getStartTime(), timings.getEndTime(), user.getId());

            if (!schedleAvailables.isEmpty()) {
                if(schedleAvailables.get(0).isAvailable()) {
                    for (PSchedleAvailable schedules : schedleAvailables) {
                        schedules.setAvailable(false);
                        schedules.setRemarks(timings.getRemarks());
                        schedules.setBookedByUsername(timings.getUserName());
                        schedulingRepository.saveAndFlush(schedules);
                        available = true;
                    }
                }
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
                    schedleAvailable.setBookedByUsername(timings.getUserName());
                    schedleAvailable.setAvailable(false);
                    schedleAvailable.setRemarks(String.format("Persisted while user booked schedule with user %s ",timings.getUserName()));
                    schedulingRepository.saveAndFlush(schedleAvailable);
                    start = end;
                }
            }
        return available;
    }

    public void revertBookedSlotsForOtherUser(AvailableTimings timings, String bookingUserName) {
        PUsers user = getUserByUserName(bookingUserName);
        List<PSchedleAvailable> schedleAvailables = schedulingRepository.getAvailableSlot(timings.getStartTime(), timings.getEndTime(), user.getId());

        for(PSchedleAvailable schedules : schedleAvailables) {
            schedules.setAvailable(true);
            schedulingRepository.saveAndFlush(schedules);
        }
    }

    public List<PSchedleAvailable> getAllSchedules(String username) {
        PUsers user = userService.findUserByUserName(username);
        List<PSchedleAvailable> availables =  schedulingRepository.availableSchedules(user.getId());
        return availables;
    }

    public boolean isOverridingSchedules(AvailableTimings timings) {
        boolean overriding;
        PUsers user = getUserByUserName(timings.getUserName());
        List<PSchedleAvailable> schedleAvailables = schedulingRepository.getAvailableSlot(timings.getStartTime(), timings.getEndTime(), user.getId());

        if(schedleAvailables.isEmpty()) {
            overriding = false;
        } else
            overriding = true;

        return overriding;
    }
}
