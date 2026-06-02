package com.internet.banking.orchestrator.microservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.internet.banking.orchestrator.microservice.dto.DeleteManagerEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class SagaEventProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

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

    public void send(
            String queue,
            Object payload
    ) {

        String json =
                objectMapper.writeValueAsString(payload);

        rabbitTemplate.convertAndSend(
                queue,
                json
        );

    }
}