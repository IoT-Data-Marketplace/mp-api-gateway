package com.itodatamp.mpapigateway.mutation;


import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.itodatamp.mpapigateway.dao.Producer;
import com.itodatamp.mpapigateway.service.ProducerService;
import org.springframework.stereotype.Component;


@Component
public class ProducerMutation implements GraphQLMutationResolver {

    private final ProducerService producerService;

    public ProducerMutation(ProducerService producerService) {
        this.producerService = producerService;
    }

    public Producer registerProducer(final String producerContractAddress) {
        return this.producerService.registerProducer(producerContractAddress);
    }
}