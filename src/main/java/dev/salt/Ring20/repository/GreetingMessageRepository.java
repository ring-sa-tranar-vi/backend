package dev.salt.Ring20.repository;

import dev.salt.Ring20.entity.GreetingMessage;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GreetingMessageRepository extends ListCrudRepository<GreetingMessage, Long> {
  <T> Optional<GreetingMessage> findTopByOrderByIdAsc();
}
