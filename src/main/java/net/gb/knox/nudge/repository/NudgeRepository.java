package net.gb.knox.nudge.repository;

import net.gb.knox.nudge.model.Nudge;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NudgeRepository extends JpaRepository<Nudge, Long> {

    List<Nudge> findAllByUserId(String userId);

    List<Nudge> findAllByUserId(String userId, Sort sort);

    void deleteByIdAndUserId(Long id, String userId);

    boolean existsByIdAndUserId(Long id, String userId);
}
