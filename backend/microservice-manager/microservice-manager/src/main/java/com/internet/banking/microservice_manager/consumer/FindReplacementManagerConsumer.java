package com.internet.banking.microservice_manager.consumer;

import com.internet.banking.microservice_manager.command.FindReplacementManagerCommand;
import com.internet.banking.microservice_manager.config.DeleteManagerRabbitConstants;
import com.internet.banking.microservice_manager.event.ReplacementManagerFoundEvent;
import com.internet.banking.microservice_manager.event.ReplacementManagerNotFoundEvent;
import com.internet.banking.microservice_manager.model.ManagerModel;
import com.internet.banking.microservice_manager.producer.DeleteManagerProducer;
import com.internet.banking.microservice_manager.service.ManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class FindReplacementManagerConsumer {

    private final ManagerService managerService;
    private final DeleteManagerProducer producer;

    public FindReplacementManagerConsumer(
            ManagerService managerService,
            DeleteManagerProducer producer
    ) {
        this.managerService = managerService;
        this.producer = producer;
    }

    @RabbitListener(
            queues =
                    DeleteManagerRabbitConstants
                            .FIND_REPLACEMENT_MANAGER_COMMAND_QUEUE
    )
    public void consume(
            FindReplacementManagerCommand command
    ) {

        try {

            ManagerModel manager =
                    managerService.getManagerByCpf(
                            command.managerCpf()
                    );

            ManagerModel replacement =
                    managerService.findReplacementManager(
                            command.managerCpf()
                    );

            producer.publishReplacementManagerFound(
                    new ReplacementManagerFoundEvent(
                            command.sagaId(),
                            manager.getId(),
                            replacement.getId()
                    )
            );

        } catch (Exception ex) {

            producer.publishReplacementManagerNotFound(
                    new ReplacementManagerNotFoundEvent(
                            command.sagaId(),
                            ex.getMessage()
                    )
            );

        }

    }
}