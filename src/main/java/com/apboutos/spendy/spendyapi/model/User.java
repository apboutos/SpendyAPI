package com.apboutos.spendy.spendyapi.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "SPENDY_USER")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@EqualsAndHashCode(of = {"username"})
@ToString(of = {"username","password","enabled","lastLogin"})
public class User {

    @Id
    @Email
    @Size(max = 100)
    @Column(name = "username")
    private String username;
    //@Size(min = 60, max = 60)
    @Column(name = "password")
    private String password;
    @Column(name = "registrationDate")
    private Timestamp registrationDate;
    @Column(name = "lastLogin")
    private Timestamp lastLogin;

    @Enumerated(EnumType.STRING)
    @Column(name = "userRole")
    private UserRole userRole;
    @Column(name = "locked")
    private Boolean locked = false;
    @Column(name = "enabled")
    private Boolean enabled = false;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Category> categories;

    public User(String username, String password, UserRole userRole) {
        this.username = username;
        this.password = password;
        this.registrationDate = new Timestamp(System.currentTimeMillis());
        this.lastLogin = new Timestamp(System.currentTimeMillis());
        this.userRole = userRole;
        this.locked = false;
        this.enabled = false;
    }

}
