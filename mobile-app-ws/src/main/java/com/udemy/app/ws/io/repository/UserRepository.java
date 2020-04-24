package com.udemy.app.ws.io.repository;

import com.udemy.app.ws.io.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Provides CRUD + Paging + Sort methods to communicate with Data Base
 */
@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);

    UserEntity findByUserId(String userId);

    UserEntity findUserByEmailVerificationToken(String token);


    // just for test how the Native Query works
    @Query(value = "SELECT * FROM Users u WHERE u.email_verification_status = 'true'",
            countQuery = "SELECT COUNT(*) FROM Users u WHERE u.email_verification_status = 'true'",
            nativeQuery = true)
    Page<UserEntity> findAllUsersWithConfirmedEmails(Pageable pageableRequest);

    @Query(value = "SELECT * FROM Users u WHERE u.first_name = ?1", nativeQuery = true)
    List<UserEntity> findUserByFirstName(String firstName);

    @Query(value = "SELECT * FROM Users u WHERE u.last_name = :lastName", nativeQuery = true)
    List<UserEntity> findUserByLastName(@Param("lastName") String lastName);

    @Query(value = "SELECT * FROM Users u WHERE u.first_name LIKE %:keyword% or u.last_name LIKE %:keyword%", nativeQuery = true)
    List<UserEntity> findUserByKeyword(@Param("keyword") String keyword);

    @Query(value = "SELECT u.first_name, u.last_name FROM Users u WHERE u.first_name LIKE %:keyword% or u.last_name LIKE %:keyword%", nativeQuery = true)
    List<Object[]> findUserFirstAndLastNamesByKeyword(@Param("keyword") String keyword);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Users u SET u.email_verification_status =:emailVerificationStatus where u.user_id =:userId", nativeQuery = true)
    void updateEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerificationStatus,
                                       @Param("userId") String userId);


    // just for test how the JPQL works
    @Query("SELECT user FROM UserEntity user WHERE user.userId =:userId")
    UserEntity findUserEntityByUserId(@Param("userId") String userId);

    @Query("SELECT u.firstName, u.lastName FROM UserEntity u WHERE u.userId =:userId")
    List<Object[]> getUserEntityFullNameById(@Param("userId") String userId);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.emailVerificationStatus =:emailVerificationStatus where u.userId =:userId")
    void updateUserEntityEmailVerificationStatus(
            @Param("emailVerificationStatus") boolean emailVerificationStatus,
            @Param("userId") String userId);
}
