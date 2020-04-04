package com.postman.slotbooking.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SchedulingResourceTest {

    @InjectMocks
    SchedulingResource schedulingResource;

    @Mock
    SchedulingServiceImpl schedulingService;

    @Mock
    private ObjectMapper objectMapper;

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

                when(schedulingService.persistMyTimings(any(AvailableTimings.class))).thenReturn(true);

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
}