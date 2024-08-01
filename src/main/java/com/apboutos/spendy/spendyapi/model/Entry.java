package com.apboutos.spendy.spendyapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Table(name = "ENTRY")
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENTRY_SEQ")
    @SequenceGenerator(name = "ENTRY_SEQ", sequenceName = "ENTRY_SEQUENCE", allocationSize = 1)
    private long id;
    @NotNull
    @Column(unique = true)
    private final UUID uuid;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(referencedColumnName = "username")
    @NotNull
    private final User username;
    @Enumerated(EnumType.STRING)
    @NotNull
    private Type type;
    @ManyToOne(targetEntity = Category.class)
    @NotNull
    private Category category;
    @Size(max = 45)
    @NotBlank
    private String description;
    @NotNull
    private long price;
    @NotNull
    private final Date createdAt;
    private Timestamp lastUpdate;
    @NotNull
    private Boolean isDeleted;

    public enum Type{
        Income,Expense
    }

    public Entry(UUID uuid,
                 User username,
                 Type type,
                 Category category,
                 String description,
                 long price,
                 Date createdAt,
                 Timestamp lastUpdate,
                 Boolean isDeleted) {
        this.uuid = uuid;
        this.username = username;
        this.type = type;
        this.category = category;
        this.description = description;
        this.price = price;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.isDeleted = isDeleted;
    }
}
