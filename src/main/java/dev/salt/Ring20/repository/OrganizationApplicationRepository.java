package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.OrganizationApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationApplicationRepository
        extends JpaRepository<OrganizationApplication, Long> {}
