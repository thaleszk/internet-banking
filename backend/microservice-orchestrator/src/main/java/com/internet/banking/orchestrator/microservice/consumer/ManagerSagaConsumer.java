package com.internet.banking.orchestrator.microservice.consumer;

import com.internet.banking.orchestrator.microservice.event.DeleteManagerEvent;
import com.internet.banking.orchestrator.microservice.service.ManagerSagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.internet.banking.orchestrator.microservice.config.RabbitConfig.DELETE_QUEUE;

@Component
@RequiredArgsConstructor
public class ManagerSagaConsumer {

    private final ManagerSagaService sagaService;

    @RabbitListener(queues = DELETE_QUEUE)
    public void startSaga(DeleteManagerEvent event) {

        System.out.println(
                "Iniciando saga para gerente: " + event.getCpf()
        );

        sagaService.start(event);
    }
}
