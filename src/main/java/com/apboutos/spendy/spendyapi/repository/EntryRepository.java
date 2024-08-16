package com.apboutos.spendy.spendyapi.repository;

import com.apboutos.spendy.spendyapi.model.Category;
import com.apboutos.spendy.spendyapi.model.Entry;
import com.apboutos.spendy.spendyapi.model.Type;
import com.apboutos.spendy.spendyapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EntryRepository extends JpaRepository<Entry, String> {

    Optional<Entry> findEntryByUuid(UUID uuid);

    List<Entry> findEntriesByUsernameAndLastUpdateAfter(User user, Timestamp lastPullRequestTimeStamp);

    List<Entry> findEntryByUsernameAndCreatedAt(User user, Date createdAt);

    List<Entry> findEntriesByUsernameAndCategory(User user, Category category);

    @Query("SELECT SUM(e.price) FROM Entry e WHERE e.username = :user AND e.category.id = (SELECT c.id FROM Category c WHERE c.uuid = :categoryUUID) AND EXTRACT(DAY FROM e.createdAt) = :dayOfMonth AND EXTRACT(MONTH FROM e.createdAt) = :month AND EXTRACT(YEAR FROM e.createdAt) = :year")
    Integer getSumOfPricesByUsernameAndCategoryAndDayOfMonth(@Param(value = "user") User user, @Param("categoryUUID") UUID categoryUUID, @Param("dayOfMonth") int dayOfMonth, @Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(e.price) FROM Entry e WHERE e.username = :user AND e.category.id = (SELECT c.id FROM Category c WHERE c.uuid = :categoryUUID) AND EXTRACT(MONTH FROM e.createdAt) = :month AND EXTRACT(YEAR FROM e.createdAt) = :year")
    Integer getSumOfPricesByUsernameAndCategoryAndMonth(@Param(value = "user") User user, @Param("categoryUUID") UUID categoryUUID, @Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(e.price) FROM Entry e WHERE e.username = :user AND e.category.id = (SELECT c.id FROM Category c WHERE c.uuid = :categoryUUID) AND EXTRACT(YEAR FROM e.createdAt) = :year")
    Integer getSumOfPricesByUsernameAndCategoryAndYear(@Param(value = "user") User user, @Param("categoryUUID") UUID categoryUUID, @Param("year") int year);

    @Query("SELECT SUM(e.price) FROM Entry e WHERE e.username = :user AND e.category.id = (SELECT c.id FROM Category c WHERE c.uuid = :categoryUUID)")
    Integer getSumOfPricesByUsernameAndCategory(@Param(value = "user") User user, @Param("categoryUUID") UUID categoryUUID);

    Integer
    countEntriesByUsernameAndCategory(User user, Category category);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteEntryByUuid(UUID uuid);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteByCategory(Category category);

    @Modifying(clearAutomatically = true,flushAutomatically = true)
    @Query("UPDATE Entry e SET e.description = :description, e.type = :type, e.price = :price, e.category = :category," +
            "e.lastUpdate = :lastUpdate, e.isDeleted = :isDeleted WHERE e.uuid = :uuid")
    void updateEntry(@Param(value = "uuid") UUID uuid,
                     @Param(value = "type") Type type,
                     @Param(value = "description") String description,
                     @Param(value = "price") long price,
                     @Param(value = "category") Category category,
                     @Param(value = "lastUpdate") Timestamp lastUpdate,
                     @Param(value = "isDeleted") Boolean isDeleted);

}
