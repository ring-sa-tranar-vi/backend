package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
	boolean existsByNameIgnoreCaseAndLanguageIgnoreCase(String name, String language);
}

