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
    private String username;
    @Size(min = 60, max = 60)
    private String password;
    private Timestamp registrationDate;
    private Timestamp lastLogin;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private Boolean locked = false;
    private Boolean enabled = false;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Category> categories;

}
