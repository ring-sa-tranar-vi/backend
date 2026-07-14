package dev.salt.Ring20.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Event extends JpaRepository<Event, Long> {
}
