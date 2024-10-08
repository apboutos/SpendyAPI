package com.apboutos.spendy.spendyapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



@Entity
@Table(name = "CATEGORY")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@ToString(of = {"id"})
@EqualsAndHashCode(of = {"id"})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORY_SEQ")
    @SequenceGenerator(name = "CATEGORY_SEQ", sequenceName = "CATEGORY_SEQUENCE", allocationSize = 1)
    @Column(name = "id")
    private long id;
    @NotNull
    @Column(name = "uuid", unique = true)
    private final UUID uuid;
    @Column(name = "name")
    private String name;
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "type")
    private Type type;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "last_update")
    private Instant lastUpdate;
    @NotNull
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_username", referencedColumnName = "username")
    private final User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
    private List<Entry> entries;

    /**
     * Constructor that allows the creation of a {@link Category} object without knowing its database ID.
     *
     * @param uuid the {@link UUID} of the Category.
     * @param name the name of the Category.
     * @param type the {@link Type} of the Category.
     * @param user the name of the {@link User}
     * @param createdAt the creation date (DD/MM/YYYY) of the Category as a {@link Instant} object.
     * @param lastUpdate the last update timestamp (DD/MM/YYYY hh:mm:ss) of the Category as a {@link Instant} object.
     * @param isDeleted whether the Category is marked as deleted.
     */
    public Category(UUID uuid, String name, Type type, User user, Instant createdAt, Instant lastUpdate, boolean isDeleted){
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.user = user;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.isDeleted = isDeleted;
        this.entries = new ArrayList<>();
    }

    /**
     * Constructor that allows the creation of a {@link Category} object with an initialized database ID.
     *
     * @param id the database ID of the Category.
     * @param uuid the {@link UUID} of the Category.
     * @param name the name of the Category.
     * @param type the {@link Type} of the Category.
     * @param user the name of the {@link User}
     * @param createdAt the creation date (DD/MM/YYYY) of the Category as a {@link Instant} object.
     * @param lastUpdate the last update timestamp (DD/MM/YYYY hh:mm:ss) of the Category as a {@link Instant} object.
     * @param isDeleted whether the Category is marked as deleted.
     */
    public Category(long id, UUID uuid, String name, Type type, User user, Instant createdAt, Instant lastUpdate, boolean isDeleted){
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.user = user;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.isDeleted = isDeleted;
        this.entries = new ArrayList<>();
    }

}
