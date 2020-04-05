package com.postman.slotbooking.resources;

import com.postman.slotbooking.models.PUsers;
import com.postman.slotbooking.services.UserRegistrationImpl;
import com.postman.slotbooking.util.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserRegistrationResource {

    @Autowired
    private UserRegistrationImpl userRegistration;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;

    private final Logger logger = LoggerFactory.getLogger(UserRegistrationResource.class);


    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody PUsers PUsers) {
        logger.info("Registration request for user name :{} ", PUsers.getUserName());
        ResponseEntity<Map<String, String>> response;
        Integer userId = userRegistration.registerUser(PUsers);

        Map<String, String> mapResponse = new HashMap<>();

        if(userId != -1) {
            mapResponse.put("uerId", userId.toString());
            response = new ResponseEntity<>(mapResponse, HttpStatus.OK);
        } else {
            mapResponse.put("error", "Username already taken please select a new useranme");
            response = new ResponseEntity<>(mapResponse, HttpStatus.CONFLICT);
        }

        return response;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<Map<String, String>> signInAndReturnAuthenticationToken(@RequestBody PUsers PUsers) throws Exception{
        logger.info("Sign-in request for user name :{} ", PUsers.getUserName());
        authenticate(PUsers.getUserName(), PUsers.getPassword());

        PUsers existingUser  = userRegistration.findUserByUserName(PUsers.getUserName());
        final String jwtToken = jwtUtil.generateToken(existingUser);
        Map<String, String> response = new HashMap<>();
        response.put("JWTToken", jwtToken);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    public void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}
