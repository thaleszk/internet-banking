package com.internet.banking.microservice_manager.producer;

import com.internet.banking.microservice_manager.dto.DeleteManagerEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.internet.banking.microservice_manager.config.RabbitConfig.EXCHANGE;

@Component
@RequiredArgsConstructor
public class ManagerSagaProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendValidationFailed(
            String cpf
    ) {

        rabbitTemplate.convertAndSend(
                EXCHANGE,
                "manager.delete.failed",
                cpf
        );
    }

    public void sendTransferRequest(
            DeleteManagerEvent event
    ) {

        rabbitTemplate.convertAndSend(
                EXCHANGE,
                "manager.delete.validated",
                event
        );
    }
}