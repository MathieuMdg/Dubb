package com.lonelys.Dubb.repository;

import com.lonelys.Dubb.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByUserJoindate(LocalDate userJoindate);

    // Detect if a user already exist in the database
    boolean existsByUserMail(String UserMail);
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);

}
