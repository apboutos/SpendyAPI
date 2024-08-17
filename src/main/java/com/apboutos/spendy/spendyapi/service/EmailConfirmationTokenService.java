package com.apboutos.spendy.spendyapi.service;

import com.apboutos.spendy.spendyapi.exception.TokenExpiredException;
import com.apboutos.spendy.spendyapi.exception.TokenNotFoundException;
import com.apboutos.spendy.spendyapi.model.EmailConfirmationToken;
import com.apboutos.spendy.spendyapi.repository.EmailConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmailConfirmationTokenService {

    private final EmailConfirmationTokenRepository repository;

    public EmailConfirmationToken saveConfirmationToken(EmailConfirmationToken token) {
        return repository.save(token);
    }

    @Transactional
    public EmailConfirmationToken validateEmailConfirmationToken(String token, Instant confirmedAt) throws TokenNotFoundException,TokenExpiredException {

        Optional<EmailConfirmationToken> savedToken = repository.findByToken(token);

        if (savedToken.isEmpty()) {
            throw new TokenNotFoundException("Token is invalid.");
        }
        if (savedToken.get().getExpiresAt().isBefore(confirmedAt)) {
            throw new TokenExpiredException("Token has expired.");
        }

        repository.updateTokenConfirmationTimestamp(savedToken.get().getId(),confirmedAt);
        return savedToken.get();
    }

}
