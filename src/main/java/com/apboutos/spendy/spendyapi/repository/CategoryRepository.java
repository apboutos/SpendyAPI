package com.apboutos.spendy.spendyapi.repository;

import com.apboutos.spendy.spendyapi.model.Category;
import com.apboutos.spendy.spendyapi.model.Type;
import com.apboutos.spendy.spendyapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;



@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    Optional<Category> findCategoryByUuid(UUID uuid);

    Optional<Category> findCategoryByTypeAndNameAndUser(Type type, String name, User user);

    List<Category> findCategoriesByUser(User user);

    @Modifying(clearAutomatically = true,flushAutomatically = true)
    @Query("UPDATE Category c " +
            "SET c.uuid = :uuid, c.name = :name,c.type = :type, c.isDeleted = :isDeleted, c.lastUpdate = :lastUpdate " +
            "WHERE c.uuid = :uuid")
    void updateCategory(@Param(value = "uuid") UUID uuid,
                        @Param(value = "name") String name,
                        @Param(value = "type") Type type,
                        @Param(value = "lastUpdate") Instant lastUpdate,
                        @Param(value = "isDeleted") boolean isDeleted);

}
