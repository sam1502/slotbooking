package com.postman.slotbooking.resources;

import com.google.gson.Gson;
import com.postman.slotbooking.models.AvailableTimings;
import com.postman.slotbooking.services.SchedulingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SchedulingResourceTest {

    @Spy
    @InjectMocks
    SchedulingResource schedulingResource;

    @Mock
    SchedulingServiceImpl schedulingService;


    MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(schedulingResource).build();
    }

    @Nested
    @DisplayName("Given posting available timings()")
    public class GivenPostingAvailableTimings {

        @Nested
        @DisplayName("When hit endpoint with right json")
        public class WhenHitEndpointWithRightJson {

            @Test
            @DisplayName("Then persist slots to db")
            public void thenPersistSlotsToDB() throws Exception{

                when(schedulingService.persistMyTimings(any(AvailableTimings.class))).thenReturn(" ");

                mockMvc.perform( MockMvcRequestBuilders
                        .post("/slots/available")
                        .content(new Gson().toJson(new AvailableTimings()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());


                verify(schedulingService, times(1)).persistMyTimings(any(AvailableTimings.class));
            }
        }
    }

    @Nested
    @DisplayName("Given book schedule api()")
    public class GivenBookScheduleApi {

        @Nested
        @DisplayName("When post to api")
        public class WhenPostToApi {

            @Test
            @DisplayName("Then return response with ok status")
            public void thenReturnOkStatus() throws Exception{

                doReturn("sameer").when(schedulingResource).getCurrentUserName();
                when(schedulingService.bookSchedule(any(AvailableTimings.class), anyString())).thenReturn(new HashMap<>());
                mockMvc.perform( MockMvcRequestBuilders
                        .post("/slots/bookschedule")
                        .content(new Gson().toJson(new AvailableTimings()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("Then return response with bad request status")
            public void thenReturnBadRequestStatus() throws Exception{

                doReturn("sameer").when(schedulingResource).getCurrentUserName();
                doThrow(UsernameNotFoundException.class).when(schedulingService).bookSchedule(any(AvailableTimings.class), anyString());
                mockMvc.perform( MockMvcRequestBuilders
                        .post("/slots/bookschedule")
                        .content(new Gson().toJson(new AvailableTimings()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
            }
        }
    }

    @Nested
    @DisplayName("Given getCurrent useranme method()")
    public class GivenGetCurrentUserNameMethod {

        @Nested
        @DisplayName("When method call")
        public class WhenMethodCall {

            @Test
            @DisplayName("Then return username")
            public void thenReturnUserName() {

                doReturn("sameer").when(schedulingResource).getCurrentUserName();
                assertNotNull(schedulingResource.getCurrentUserName());
            }
        }
    }
}