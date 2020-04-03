package com.postman.slotbooking.resources;

import com.google.gson.Gson;
import com.postman.slotbooking.models.PUsers;
import com.postman.slotbooking.services.UserRegistrationImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.PushbackInputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRegistrationResourceTest {


    @Spy
    @InjectMocks
    UserRegistrationResource userRegistrationResource;

    @Mock
    UserRegistrationImpl userRegistration;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userRegistrationResource).build();
    }


    @Nested
    @DisplayName("Given user register api()")
    public class GivenUserRegisterApi {

        @Nested
        @DisplayName("When post with proper data")
        public class WhenPostWithProperData {

            @Test
            @DisplayName("Then get userId")
            public void thenGetUSerId() throws Exception{

                when(userRegistration.registerUser(any(PUsers.class))).thenReturn(1);

                PUsers users = new PUsers();
                users.setUserName("sameer");
                users.setPassword("1234");

                mockMvc.perform( MockMvcRequestBuilders
                        .post("/user/register")
                        .content(new Gson().toJson(users))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("Then get error")
            public void thenGetError() throws Exception{
                when(userRegistration.registerUser(any(PUsers.class))).thenReturn(-1);

                PUsers users = new PUsers();
                users.setUserName("sameer");
                users.setPassword("1234");

                mockMvc.perform( MockMvcRequestBuilders
                        .post("/user/register")
                        .content(new Gson().toJson(users))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());

                verify(userRegistration,times(1)).registerUser(any(PUsers.class));
            }
        }
    }
}