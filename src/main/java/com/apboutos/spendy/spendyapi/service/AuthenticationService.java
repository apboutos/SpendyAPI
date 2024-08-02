package com.apboutos.spendy.spendyapi.service;

import com.apboutos.spendy.spendyapi.model.User;
import com.apboutos.spendy.spendyapi.repository.UserRepository;
import com.apboutos.spendy.spendyapi.request.UserAuthenticationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;
    //private final JWTService jwtService;
    //private final MessageSender messageSender;

    public String authenticateUser(UserAuthenticationRequest request) throws UsernameNotFoundException {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password())
        );

        final User user = repository.findByUsername((request.username()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        //messageSender.sendMessage("User" + user.getUsername() + " logged in");
        return user.getUsername();//jwtService.generateToken(user);

    }

}
