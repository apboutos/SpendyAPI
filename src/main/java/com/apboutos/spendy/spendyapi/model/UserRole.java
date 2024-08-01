package com.apboutos.spendy.spendyapi.model;



import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


import static com.apboutos.spendy.spendyapi.model.UserRolePermission.*;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    USER(new HashSet<>(Arrays.asList(USER_READ,ENTRY_READ,ENTRY_WRITE,CATEGORY_READ,CATEGORY_WRITE))),
    ADMIN(new HashSet<>(Arrays.asList(USER_READ,USER_WRITE,ENTRY_READ,ENTRY_WRITE,CATEGORY_READ,CATEGORY_WRITE)));

    private final Set<UserRolePermission> permissions;
}
