package me.gg.pinit.infra.events;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
public abstract class Outbox {

    @Id
    @GeneratedValue
    private Long id;
}
