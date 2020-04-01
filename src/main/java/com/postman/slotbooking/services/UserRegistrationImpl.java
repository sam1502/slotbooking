package com.postman.slotbooking.services;

import com.postman.slotbooking.models.Users;
import com.postman.slotbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserRegistrationImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;


    public Integer registerUser(Users users) {
        Users user = userRepository.findByuserName(users.getUserName());

        if(user == null) {
            return userRepository.saveAndFlush(users).getId();
        } else
            return -1;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = userRepository.findByuserName(username);
        return new User(users.getUserName(), users.getPassword(),new ArrayList<>());
    }

    public Users findUserByUserName(String userName) {
        return userRepository.findByuserName(userName);
    }
}
