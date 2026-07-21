package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.CallbackPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CallbackPreferenceRepository extends JpaRepository<CallbackPreference, Long> {
    List<CallbackPreference> findByUserId(Long userId);
}