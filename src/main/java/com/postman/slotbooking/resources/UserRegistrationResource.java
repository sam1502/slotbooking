package com.postman.slotbooking.resources;

import com.postman.slotbooking.models.Users;
import com.postman.slotbooking.services.UserRegistrationImpl;
import com.postman.slotbooking.util.JWTUtil;
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

@RestController
@RequestMapping("/user")
public class UserRegistrationResource {

    @Autowired
    private UserRegistrationImpl userRegistration;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtil jwtUtil;


    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Users users) {
        ResponseEntity<String> response;
        Integer userId = userRegistration.registerUser(users);

        if(userId != -1) {
            response = new ResponseEntity<>(userId.toString(), HttpStatus.OK);
        } else
            response = new ResponseEntity<>("Username already taken please select a new useranme", HttpStatus.BAD_REQUEST);

        return response;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signInAndReturnAuthenticationToken(@RequestBody Users users) throws Exception{
        authenticate(users.getUserName(), users.getPassword());

        Users existingUser  = userRegistration.findUserByUserName(users.getUserName());
        final String jwtToken = jwtUtil.generateToken(existingUser);

        return new ResponseEntity<>(jwtToken, HttpStatus.OK);
    }



    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}
