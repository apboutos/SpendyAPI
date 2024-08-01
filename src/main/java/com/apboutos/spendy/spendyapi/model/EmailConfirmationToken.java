package com.apboutos.spendy.spendyapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@Entity
@Table(name = "CONFIRMATION_TOKEN")
@Data
@NoArgsConstructor
public class EmailConfirmationToken {

    @Id
    @SequenceGenerator(name = "CONFIRMATION_TOKEN_SEQ",
            sequenceName = "CONFIRMATION_TOKEN_SEQUENCE",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "CONFIRMATION_TOKEN_SEQ")
    @Column(name = "id")
    private Long id;
    @Column(name = "token", nullable = false)
    private String token;
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
    @Column(name = "expires_at", nullable = false)
    private Timestamp expiresAt;
    @Column(name = "confirmed_at")
    private Timestamp confirmedAt;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_username", nullable = false,referencedColumnName = "username")
    private User user;

    public EmailConfirmationToken(String token, User user, Timestamp createdAt, Timestamp expiresAt) {
        this.token = token;
        this.user = user;
        this.createdAt = createdAt;
        this.confirmedAt = null;
        this.expiresAt = expiresAt;
    }
}
