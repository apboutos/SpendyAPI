package com.apboutos.spendy.spendyapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "ENTRY")
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@ToString(of = {"id"})
@EqualsAndHashCode(of = {"id"})
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

    /**
     * Constructor that allows the creation of an {@link Entry} object without knowing its database ID.
     *
     * @param uuid the {@link UUID} of the Entry.
     * @param username the name of the {@link User}
     * @param type the {@link Type} of the Entry.
     * @param category the {@link Category} of the Entry.
     * @param description the description of the Entry.
     * @param price the price of the Entry in {@code long} format (cents). Divide by 100 to get the value in €
     * @param createdAt the creation date (DD/MM/YYYY) of the Entry as a {@link Date} object.
     * @param lastUpdate the last update timestamp (DD/MM/YYYY hh:mm:ss) of the Entry as a {@link Timestamp} object.
     * @param isDeleted whether the Entry is marked as deleted.
     */
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
