package com.postman.slotbooking.services;

import com.postman.slotbooking.models.AvailableTimings;
import com.postman.slotbooking.models.PSchedleAvailable;
import com.postman.slotbooking.models.PUsers;
import com.postman.slotbooking.repository.SchedulingRepository;
import com.postman.slotbooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SchedulingServiceImplTest {

    @Spy
    @InjectMocks
    SchedulingServiceImpl schedulingService;

    @Mock
    SchedulingRepository repository;

    @Mock
    UserRepository userRepository;

    @Mock
    UserRegistrationImpl userRegistration;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Nested
    @DisplayName("Given persiste my timings method()")
    public class GivenPersistMyTimingsMethod {

        @Nested
        @DisplayName("When method call")
        public class WhenMethodCall {

            @Test
            @DisplayName("Then return boolean status")
            public void thenReturnBooleanStatus() throws Exception{

                AvailableTimings timings = new AvailableTimings();
                timings.setStartTime(LocalDateTime.now().plusHours(1));
                timings.setEndTime(LocalDateTime.now().plusHours(2));
                timings.setUserName("sameer");

                PUsers user = new PUsers();
                user.setId(1);
                user.setUserName("sameer");

                doNothing().when(schedulingService).checkDateOfSchedule(LocalDateTime.now(),LocalDateTime.now());
                when(schedulingService.getUserByUserName("sameer")).thenReturn(user);
                when(repository.saveAndFlush(any(PSchedleAvailable.class))).thenReturn(new PSchedleAvailable());
                assertNotNull(schedulingService.persistMyTimings(timings));
            }

            @Test
            @DisplayName("Then throw wrong start date exception")
            public void thenThrowException(){
                assertThrows(Exception.class, () -> schedulingService.checkDateOfSchedule(LocalDateTime.now().minusSeconds(1),LocalDateTime.now()));
            }

            @Test
            @DisplayName("Then throw wrong end date exception")
            public void thenThrowExceptionForWrongEndDate(){
                assertThrows(Exception.class, () -> schedulingService.checkDateOfSchedule(LocalDateTime.now(),LocalDateTime.now().minusSeconds(2)));
            }
        }
    }

    @Nested
    @DisplayName("Given schedule available method()")
    public class GivenScheduleAvailableMethod {

        @Nested
        @DisplayName("When method called")
        public class WhenMethodCalled {

            @Test
            @DisplayName("Then return boolean response")
            public void thenReturnBooleanResponse() {
                PUsers user = new PUsers();
                user.setId(1);
                user.setUserName("sameer");

                AvailableTimings timings = new AvailableTimings();
                timings.setStartTime(LocalDateTime.now());
                timings.setEndTime(LocalDateTime.now());

                when(schedulingService.getUserByUserName("sameer")).thenReturn(user);
                when(repository.getAvailableSlot(LocalDateTime.now(), LocalDateTime.now(), 1)).thenReturn(new ArrayList<>());
                when(repository.saveAndFlush(any(PSchedleAvailable.class))).thenReturn(new PSchedleAvailable());
                assertTrue(schedulingService.scheduleAvailableIfNotPersistWithFalseStatus(timings,"sameer"));

            }

            @Test
            @DisplayName("Then return boolean response for schedules found")
            public void thenReturnBooleanResponseForSchedulesFound() {
                PUsers user = new PUsers();
                user.setId(1);
                user.setUserName("sameer");

                AvailableTimings timings = new AvailableTimings();
                timings.setStartTime(LocalDateTime.now());
                timings.setEndTime(LocalDateTime.now().minusSeconds(1));

                PSchedleAvailable schedleAvailable = new PSchedleAvailable();
                schedleAvailable.setAvailable(false);

                when(schedulingService.getUserByUserName("sameer")).thenReturn(user);
                when(repository.getAvailableSlot(timings.getStartTime(), timings.getEndTime(), 1)).thenReturn(Collections.singletonList(schedleAvailable));
                when(repository.saveAndFlush(any(PSchedleAvailable.class))).thenReturn(new PSchedleAvailable());
                assertFalse(schedulingService.scheduleAvailableIfNotPersistWithFalseStatus(timings,"sameer"));

            }
        }
    }

}