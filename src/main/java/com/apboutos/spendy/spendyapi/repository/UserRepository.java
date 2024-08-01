package com.apboutos.spendy.spendyapi.repository;

import com.apboutos.spendy.spendyapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {


    Optional<User> findByUsername(String username);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE User u SET u.enabled = true WHERE u.username = :username")
    void enableUser(@Param(value = "username") String username);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE User u SET u.password = :password WHERE u.username = :username")
    void updatePassword(@Param(value = "password") String password, @Param(value = "username") String username);

}
