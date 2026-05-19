package com.internet.banking.orchestrator.microservice.producer;

import com.internet.banking.orchestrator.microservice.dto.DeleteManagerEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SagaEventProducer {

    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE =
            "manager.exchange";

    private static final String ROUTING_KEY =
            "manager.delete.requested";

    public void sendDeleteValidation(DeleteManagerEvent event) {

        rabbitTemplate.convertAndSend(
                EXCHANGE,
                ROUTING_KEY,
                event
        );

        System.out.println(
                "Evento enviado para remover gerente: "
                        + event.getCpf()
        );
    }
}