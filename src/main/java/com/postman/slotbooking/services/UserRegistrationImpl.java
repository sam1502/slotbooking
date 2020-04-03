package com.postman.slotbooking.services;

import com.postman.slotbooking.models.PUsers;
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


    public Integer registerUser(PUsers PUsers) {
        PUsers user = userRepository.findByuserName(PUsers.getUserName());

        if(user == null) {
            return userRepository.saveAndFlush(PUsers).getId();
        } else
            return -1;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        PUsers PUsers = userRepository.findByuserName(username);
        return new User(PUsers.getUserName(), PUsers.getPassword(),new ArrayList<>());
    }

    public PUsers findUserByUserName(String userName) {
        return userRepository.findByuserName(userName);
    }

    public PUsers findUserByUserId(Integer id) {
        return userRepository.getOne(id);
    }
}
