package com.itodatamp.mpapigateway.query;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.itodatamp.mpapigateway.dto.SensorDTO;
import com.itodatamp.mpapigateway.service.controller.SensorService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SensorQuery implements GraphQLQueryResolver {

    private final SensorService sensorService;

    public SensorQuery(SensorService sensorService) {
        this.sensorService = sensorService;
    }


    public List<SensorDTO> getAllSensors(final int count) {
        return this.sensorService.getAllSensors(count);
    }

    public Optional<SensorDTO> getSensorByContractAddress(final String sensorContractAddress) {
        return this.sensorService.getSensorByContractAddress(sensorContractAddress);
    }

}
