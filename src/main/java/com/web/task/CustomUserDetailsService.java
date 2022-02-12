package com.web.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomUserDetailsService implements UserDetailsService {  //implements getting user information from database

    @Autowired
    private UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repo.findByEmail(email);  //searching for user in database

        if(user == null){
            throw new UsernameNotFoundException("User not found!");
        }

        repo.updateLoginDate(email, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));  //update user last login date

        return new CustomUserDetails(user);  //found user
    }
}
