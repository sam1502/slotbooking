package com.postman.slotbooking.resources;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TestResourceTest {

    @Spy
    TestResource testResource;

    MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(testResource).build();
    }

    @Nested
    @DisplayName("Given test resource")
    public class GivenTestResource {

        @Nested
        @DisplayName("When hit endpoint")
        public class WhenHitEndpoint {

            @Test
            @DisplayName("Then return the current version")
            public void thenReturnTheCurrentVersion() throws Exception{
                mockMvc.perform(get("/test")).
                        andExpect(status().isOk()).
                        andReturn();
            }
        }

    }
}