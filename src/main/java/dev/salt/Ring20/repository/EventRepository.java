package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
