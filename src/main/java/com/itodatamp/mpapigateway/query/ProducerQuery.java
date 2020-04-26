package com.itodatamp.mpapigateway.query;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.itodatamp.mpapigateway.dao.Producer;
import com.itodatamp.mpapigateway.service.ProducerService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProducerQuery implements GraphQLQueryResolver {

    private final ProducerService producerService;

    public ProducerQuery(ProducerService producerService) {
        this.producerService = producerService;
    }


    public List<Producer> getAllProducers(final int count) {
        return this.producerService.getAllProducers(count);
    }

    public Optional<Producer> getProducerByContractAddress(final String producerContractAddress) {
        return this.producerService.getProducerByContractAddress(producerContractAddress);
    }

}
