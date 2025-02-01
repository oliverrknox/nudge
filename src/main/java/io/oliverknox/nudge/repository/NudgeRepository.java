package io.oliverknox.nudge.repository;

import io.oliverknox.nudge.model.Nudge;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NudgeRepository extends JpaRepository<Nudge, Long> {

    List<Nudge> findAllByUserId(String userId);

    List<Nudge> findAllByUserId(String userId, Sort sort);

    Optional<Nudge> findByIdAndUserId(Long id, String userId);

    void deleteByIdAndUserId(Long id, String userId);

    boolean existsByIdAndUserId(Long id, String userId);
}
