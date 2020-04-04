package com.postman.slotbooking.services;

import com.postman.slotbooking.models.PUsers;
import com.postman.slotbooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserRegistrationImplTest {

    @Spy
    @InjectMocks
    UserRegistrationImpl userRegistration;

    @Mock
    UserRepository repository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Nested
    @DisplayName("Given register user()")
    public class GivenRegisterUser {

        @Nested
        @DisplayName("When called method")
        public class WhenCalledMethod {

            @Test
            @DisplayName("Then return userID")
            public void thenReturnUserId() {

                PUsers user = new PUsers();
                user.setId(1);

                when(repository.findByuserName(anyString())).thenReturn(null);
                when(repository.saveAndFlush(any(PUsers.class))).thenReturn(user);
                int actualUserId= userRegistration.registerUser(new PUsers());
                assertEquals(1,actualUserId);
                verify(repository, times(1)).saveAndFlush(any(PUsers.class));
            }

            @Test
            @DisplayName("Then return negative userID")
            public void thenReturnNegativeUserId() {

                PUsers user = new PUsers();
                user.setId(1);
                user.setUserName("sameer");

                when(repository.findByuserName(user.getUserName())).thenReturn(user);
                when(repository.saveAndFlush(any(PUsers.class))).thenReturn(user);
                int actualUserId= userRegistration.registerUser(user);
                assertEquals(-1,actualUserId);
                verify(repository, times(1)).findByuserName(anyString());
            }
        }
    }

    @Nested
    @DisplayName("Given find by userName method()")
    public class GivenFindByUserName {

        @Nested
        @DisplayName("When called method")
        public class WhenCalledMethod {

            @Test
            @DisplayName("Then return user")
            public void thenReturnUser() {

                PUsers user = new PUsers();
                user.setUserName("sameer");
                user.setId(1);

                when(repository.findByuserName(anyString())).thenReturn(user);
                assertNotNull(userRegistration.findUserByUserName(anyString()));
            }
        }
    }

    @Nested
    @DisplayName("Given find by userId method()")
    public class GivenFindByUserId {

        @Nested
        @DisplayName("When method called")
        public class WhenMethodCalled {

            @Test
            @DisplayName("Then return user")
            public void thenReturnUser() {

                PUsers user = new PUsers();
                user.setUserName("sameer");
                user.setId(1);

                when(repository.getOne(anyInt())).thenReturn(user);
                assertNotNull(userRegistration.findUserByUserId(anyInt()));
            }
        }
    }

    @Nested
    @DisplayName("Given loadBy username method()")
    public class GivenLoadByUserNameMethod {

        @Nested
        @DisplayName("When called method")
        public class WhenCalledMethod {

            @Test
            @DisplayName("Then return userdetails")
            public void thenReturnUserDetails() {

                PUsers user = new PUsers();
                user.setUserName("sameer");
                user.setPassword("1234");
                user.setId(1);

                when(repository.findByuserName(anyString())).thenReturn(user);
                assertNotNull(userRegistration.loadUserByUsername(anyString()));
            }
        }
    }

}