package com.restaurant.management.repository;

import com.restaurant.management.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByLoginIgnoreCase(String login);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByLoginIgnoreCaseAndIdNot(String login, Long id);

    Optional<User> findByLoginIgnoreCase(String login);

    Optional<User> findByEmailIgnoreCase(String email);

    List<User> findAllByNameIgnoreCase(String name);

    List<User> findByNameContainingIgnoreCase(String nameFragment);

    List<User> findByEmailContainingIgnoreCase(String emailFragment);

    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :text, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<User> searchByTextInNameOrEmail(@Param("text") String text);
}
