package com.apboutos.spendy.spendyapi.repository;

import com.apboutos.spendy.spendyapi.model.EmailConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken,Long>, CrudRepository<EmailConfirmationToken,Long> {

    Optional<EmailConfirmationToken> findByToken(String token);

    @Modifying(clearAutomatically = true,flushAutomatically = true)
    @Query("UPDATE EmailConfirmationToken token SET token.confirmedAt = :confirmedAt WHERE token.id = :id")
    void updateTokenConfirmationTimestamp(@Param(value = "id") Long id , @Param(value = "confirmedAt") Instant confirmedAt);


}
