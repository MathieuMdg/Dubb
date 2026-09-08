package com.lonelys.Dubb.repository;

import com.lonelys.Dubb.entity.Attempt;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.lonelys.Dubb.entity.User;
import com.lonelys.Dubb.entity.Clip;

public interface AttemptRepository extends CrudRepository<Attempt, Long> {

    List<Attempt> findByUser(User user);
    List<Attempt> findByClip(Clip clip);
    List<Attempt> findByAttemptDate(LocalDateTime attemptDate);
    List<Attempt> findByAttemptScore(Double attemptScore);

}
