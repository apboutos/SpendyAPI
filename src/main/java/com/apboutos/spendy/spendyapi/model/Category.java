package com.apboutos.spendy.spendyapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static com.apboutos.spendy.spendyapi.model.Entry.Type;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Setter
@Getter
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@Table(name = "CATEGORY")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORY_SEQ")
    @SequenceGenerator(name = "CATEGORY_SEQ", sequenceName = "CATEGORY_SEQUENCE", allocationSize = 1)
    private long id;
    @NotNull
    @Column(unique = true)
    private final UUID uuid;
    private String name;
    private Type type;
    private Date createdAt;
    private Timestamp lastUpdate;
    @NotNull
    private Boolean isDeleted;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "usr_username" ,referencedColumnName = "username")
    private final User user;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "category")
    private List<Entry> entries;

    public Category(UUID uuid, String name, Type type, User user, Date createdAt, Timestamp lastUpdate, boolean isDeleted){
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.user = user;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.isDeleted = isDeleted;
    }

    public Category(long id, UUID uuid, String name, Type type, User user, Date createdAt, Timestamp lastUpdate, boolean isDeleted){
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.type = type;
        this.user = user;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.isDeleted = isDeleted;
    }

}
