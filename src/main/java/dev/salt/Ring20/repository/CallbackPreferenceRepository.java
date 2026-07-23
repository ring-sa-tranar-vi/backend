package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.CallbackPreference;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CallbackPreferenceRepository extends JpaRepository<CallbackPreference, Long> {
    List<CallbackPreference> findByUserId(Long userId);
}
