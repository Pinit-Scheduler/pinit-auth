package me.gg.pinit.infra.events;

import me.gg.pinit.domain.event.DomainEvent;

public interface AmqpEventMapper<T extends DomainEvent> {
    Class<T> eventType();

    String exchange();

    String routingKey();

    Object payload(T event);
}
