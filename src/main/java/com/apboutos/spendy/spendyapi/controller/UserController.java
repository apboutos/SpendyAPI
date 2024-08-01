package com.apboutos.spendy.spendyapi.controller;

import com.apboutos.spendy.spendyapi.exception.*;
import com.apboutos.spendy.spendyapi.request.UserAuthenticationRequest;
import com.apboutos.spendy.spendyapi.request.UserRegistrationRequest;
import com.apboutos.spendy.spendyapi.response.user.UserAuthenticationResponse;
import com.apboutos.spendy.spendyapi.response.user.UserConfirmationResponse;
import com.apboutos.spendy.spendyapi.response.user.UserRegistrationResponse;
import com.apboutos.spendy.spendyapi.model.EmailConfirmationToken;
import com.apboutos.spendy.spendyapi.service.AuthenticationService;
import com.apboutos.spendy.spendyapi.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;


@RestController
@RequestMapping(value = "/api/v1/users",produces = "application/json")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class UserController {

    private final AuthenticationService authenticationService;
    private final UserService userService;


    @PostMapping("/register")
    ResponseEntity<UserRegistrationResponse> postUser(@RequestBody UserRegistrationRequest request) throws UsernameTakenException, UserNotSavedException {
        log.info("Called postUser method with request: {}", request);

        final EmailConfirmationToken token = userService.registerUser(request);
        return ResponseEntity.ok(
                new UserRegistrationResponse(
                        "User created, but not enabled until e-mail confirmation.",
                        token.getToken()
                )
        );
    }

    @PatchMapping("/confirm")
    public ResponseEntity<UserConfirmationResponse> confirmUser(@RequestParam String token) throws TokenNotFoundException, TokenExpiredException {
        log.info("Called confirmUser with token {}", token);

        userService.confirmUser(token, Timestamp.from(Instant.now()));

        return ResponseEntity.ok(
                new UserConfirmationResponse(
                        "Email confirmed. User enabled."
                )
        );
    }

    @PostMapping("/authenticate")
    ResponseEntity<UserAuthenticationResponse> authenticateUser(@RequestBody UserAuthenticationRequest request) throws UsernameNotFoundException {
        log.info("Called authenticateUser with request {}", request);

        final String jwToken = authenticationService.authenticateUser(request);

        return ResponseEntity.ok().body(
                new UserAuthenticationResponse(
                     "User authenticated.",
                              jwToken
                )
        );

    }
}
