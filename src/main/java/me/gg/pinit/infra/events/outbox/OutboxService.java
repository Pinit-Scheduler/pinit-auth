package me.gg.pinit.infra.events.outbox;

import me.gg.pinit.infra.events.rabbitmq.RabbitEvent;
import me.gg.pinit.infra.events.rabbitmq.RabbitEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final RabbitEventPublisher rabbitEventPublisher;

    public OutboxService(OutboxRepository outboxRepository, RabbitEventPublisher rabbitEventPublisher) {
        this.outboxRepository = outboxRepository;
        this.rabbitEventPublisher = rabbitEventPublisher;
    }

    @Transactional
    public void save(Outbox outbox) {
        outboxRepository.save(outbox);
        rabbitEventPublisher.publish(new RabbitEvent(outbox.getId()));
    }

    @Transactional
    public void delete(Long eventId) {
        outboxRepository.deleteById(eventId);
    }

    @Transactional
    public Outbox findById(Long eventId) {
        return outboxRepository.findById(eventId).orElseThrow(RuntimeException::new);
    }
}
