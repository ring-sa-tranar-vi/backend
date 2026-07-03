package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.GreetingMessage;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GreetingMessageRepository extends ListCrudRepository<GreetingMessage, Long> {
    <T> Optional<GreetingMessage> findTopByOrderByIdAsc();
}
