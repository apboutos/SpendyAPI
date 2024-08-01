package com.apboutos.spendy.spendyapi.service;

import com.apboutos.spendy.spendyapi.exception.*;
import com.apboutos.spendy.spendyapi.model.UserRole;
import com.apboutos.spendy.spendyapi.request.UserRegistrationRequest;
import com.apboutos.spendy.spendyapi.model.EmailConfirmationToken;
import com.apboutos.spendy.spendyapi.model.User;
import com.apboutos.spendy.spendyapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;


@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final static String USER_NOT_FOUND_MSG = "User with username %s not found";
    private final EmailConfirmationTokenService emailConfirmationTokenService;

    public User loadUserByUsername(String username) throws UsernameNotFoundException {

        return repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG,username)));
    }

    @Transactional
    public EmailConfirmationToken registerUser(UserRegistrationRequest request) throws UsernameTakenException {

        final User user = new User(request.username(), request.password(), UserRole.USER);
        user.setRegistrationDate(Timestamp.from(Instant.now()));
        user.setUserRole(UserRole.USER);

        if (repository.findByUsername(user.getUsername()).isPresent()) {
            throw new UsernameTakenException("Username already exists.");
        }

        final User databaseSavedUser = repository.save(user);

        final EmailConfirmationToken token = new EmailConfirmationToken(
                UUID.randomUUID().toString(),
                databaseSavedUser,
                Timestamp.from(Instant.now()),
                Timestamp.from(Instant.now().plusSeconds(864000))
        );


        return emailConfirmationTokenService.saveConfirmationToken(token);

    }


    @Transactional
    public void confirmUser(String token, Timestamp confirmedAt) throws TokenExpiredException, TokenNotFoundException {

        final EmailConfirmationToken confirmedToken = emailConfirmationTokenService.validateEmailConfirmationToken(token,confirmedAt);

        repository.enableUser(confirmedToken.getUser().getUsername());
    }

}
