package com.example.booksapi.publisher;

import com.example.booksapi.domain.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.annotation.Publisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventPublisher {

    @Publisher(channel = Source.OUTPUT)
    public DomainEvent sendEvent(DomainEvent domainEvent) {
        log.info("About to send {}", domainEvent);
        return domainEvent;
    }
}
