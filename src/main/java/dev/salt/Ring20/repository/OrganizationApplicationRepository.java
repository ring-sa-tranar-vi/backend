package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.OrganizationApplication;
import dev.salt.Ring20.entity.enums.ApplicationStatus;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrganizationApplicationRepository
        extends JpaRepository<OrganizationApplication, Long> {

    boolean existsByUser_IdAndApplicationStatusIn(
            Long userId, Collection<ApplicationStatus> statuses);

    Optional<OrganizationApplication> findTopByUser_ClerkIdOrderByCreatedAtDesc(String clerkId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT application FROM OrganizationApplication application WHERE application.id = :id")
    Optional<OrganizationApplication> findByIdForUpdate(@Param("id") Long id);
}
