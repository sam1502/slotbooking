package com.postman.slotbooking.resources;

import com.google.gson.Gson;
import com.postman.slotbooking.models.PUsers;
import com.postman.slotbooking.services.UserRegistrationImpl;
import com.postman.slotbooking.util.JWTUtil;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRegistrationResourceTest {


    @Spy
    @InjectMocks
    UserRegistrationResource userRegistrationResource;

    @Mock
    UserRegistrationImpl userRegistration;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JWTUtil jwtUtil;

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
                        .andExpect(status().isConflict());

                verify(userRegistration,times(1)).registerUser(any(PUsers.class));
            }
        }
    }

    @Nested
    @DisplayName("Given sign in api")
    public class GivenSignInApi {

        @Nested
        @DisplayName("When post to api")
        public class WhenPostToApi {

            @Test
            @DisplayName("Then return ok status")
            public void thenReturnOkStatus() throws Exception{

                PUsers users = new PUsers();
                users.setUserName("sameer");
                users.setPassword("1234");

                when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("hello","1234"))).thenReturn(anyObject());
                when(userRegistration.findUserByUserName("sameer")).thenReturn(users);
                when(jwtUtil.generateToken(users)).thenReturn("mockJwtToken");


                mockMvc.perform( MockMvcRequestBuilders
                        .post("/user/sign-in")
                        .content(new Gson().toJson(users))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

                verify(userRegistration,times(1)).findUserByUserName(anyString());
                verify(jwtUtil,times(1)).generateToken(any(PUsers.class));

            }
        }
    }


    @Nested
    @DisplayName("Given authenticate method")
    public class GivenAuthenticateMethod {

        @Nested
        @DisplayName("When called")
        public class WhenCalled {

            @Test
            @DisplayName("Then verify Method Call")
            public void thenVerifyMethodCall() throws Exception{

                userRegistrationResource.authenticate(anyString(),anyString());
                verify(authenticationManager, times(1)).authenticate(new UsernamePasswordAuthenticationToken("",""));
            }

            @Test
            @DisplayName("Then verify Exception thrown")
            public void thenVerifyExceptionThrown() {

                doThrow(DisabledException.class).when(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("",""));
                try {
                    userRegistrationResource.authenticate(anyString(), anyString());
                } catch (Exception e) {
                    String expected = "USER_DISABLED";
                    Assert.assertEquals(expected, e.getMessage());
                }
            }

            @Test
            @DisplayName("Then verify Exception thrown 1")
            public void thenVerifyExceptionThrown1() {

                doThrow(BadCredentialsException.class).when(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("",""));
                try {
                    userRegistrationResource.authenticate(anyString(), anyString());
                } catch (Exception e) {
                    String expected = "INVALID_CREDENTIALS";
                    Assert.assertEquals(expected, e.getMessage());
                }
            }
        }
    }
}