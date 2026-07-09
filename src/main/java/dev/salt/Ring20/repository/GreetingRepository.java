package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.GreetingMessage;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GreetingRepository extends JpaRepository<GreetingMessage, Long> {

    Optional<GreetingMessage> findTopByOrderByIdAsc();
}
