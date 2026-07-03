package dev.salt.Ring20.service;

import dev.salt.Ring20.entity.GreetingMessage;
import dev.salt.Ring20.repository.GreetingMessageRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class HiService {
    private static final String DEFAULT_MESSAGE = "This is the default message to test the system.";
    private GreetingMessageRepository repository;

    public HiService(GreetingMessageRepository repository) {
        this.repository = repository;
    }

    public GreetingMessage getOrCreateMessage() {
        return repository.findTopByOrderByIdAsc()
                .orElseGet(()-> repository.save(new GreetingMessage(DEFAULT_MESSAGE)));
    }

}
