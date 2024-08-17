package com.apboutos.spendy.spendyapi.service;

import com.apboutos.spendy.spendyapi.exception.TokenExpiredException;
import com.apboutos.spendy.spendyapi.exception.TokenNotFoundException;
import com.apboutos.spendy.spendyapi.exception.UsernameTakenException;
import com.apboutos.spendy.spendyapi.model.UserRole;
import com.apboutos.spendy.spendyapi.request.UserRegistrationRequest;
import com.apboutos.spendy.spendyapi.model.EmailConfirmationToken;
import com.apboutos.spendy.spendyapi.model.User;
import com.apboutos.spendy.spendyapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;


@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final static String USER_NOT_FOUND_MSG = "User with username %s not found";
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailConfirmationTokenService emailConfirmationTokenService;

    @Override
    public User loadUserByUsername(String username) {

        return repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG,username)));
    }

    @Transactional
    public EmailConfirmationToken registerUser(UserRegistrationRequest request) throws UsernameTakenException {

        final User user = new User(request.username(), passwordEncoder.encode(request.password()), UserRole.USER);
        user.setRegistrationDate(Instant.now());
        user.setUserRole(UserRole.USER);

        if (repository.findByUsername(user.getUsername()).isPresent()) {
            throw new UsernameTakenException("Username already exists.");
        }

        final User databaseSavedUser = repository.save(user);

        final EmailConfirmationToken token = new EmailConfirmationToken(
                UUID.randomUUID().toString(),
                databaseSavedUser,
                Instant.now(),
                Instant.now().plusSeconds(864000)
        );


        return emailConfirmationTokenService.saveConfirmationToken(token);

    }


    @Transactional
    public void confirmUser(String token, Instant confirmedAt) throws TokenExpiredException, TokenNotFoundException {

        final EmailConfirmationToken confirmedToken = emailConfirmationTokenService.validateEmailConfirmationToken(token,confirmedAt);

        repository.enableUser(confirmedToken.getUser().getUsername());
    }

}
