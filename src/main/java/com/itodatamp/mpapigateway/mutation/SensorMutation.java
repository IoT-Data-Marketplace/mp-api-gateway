package com.itodatamp.mpapigateway.mutation;


import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.itodatamp.mpapigateway.dto.HttpResponseDTO;
import com.itodatamp.mpapigateway.service.SensorService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;


@Component
public class SensorMutation implements GraphQLMutationResolver {

    private final SensorService sensorService;

    public SensorMutation(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    public HttpResponseDTO registerSensor(@NotNull final String sensorContractAddress) {
        return this.sensorService.activateSensor(sensorContractAddress);
    }
}