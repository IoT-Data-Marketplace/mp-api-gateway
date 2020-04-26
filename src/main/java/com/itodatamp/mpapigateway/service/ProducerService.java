package com.itodatamp.mpapigateway.service;

import com.itodatamp.mpapigateway.dao.Producer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProducerService {
    public List<Producer> getAllProducers(int count) {
        return null;
    }

    public Optional<Producer> getProducerByContractAddress(String producerContractAddress) {
        return Optional.ofNullable(Producer.builder().producerContractAddress(producerContractAddress).build());
    }

    public Producer registerProducer(String producerContractAddress) {
        return Producer.builder().producerContractAddress(producerContractAddress).build();
    }
}
