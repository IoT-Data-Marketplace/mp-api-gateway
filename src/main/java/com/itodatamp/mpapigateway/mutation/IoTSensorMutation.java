package com.itodatamp.mpapigateway.mutation;


import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.itodatamp.mpapigateway.dao.IoTSensor;
import com.itodatamp.mpapigateway.service.controller.IoTSensorService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;


@Component
public class IoTSensorMutation implements GraphQLMutationResolver {

    private final IoTSensorService ioTSensorService;

    public IoTSensorMutation(IoTSensorService ioTSensorService) {
        this.ioTSensorService = ioTSensorService;
    }

    public IoTSensor registerIoTSensor(@NotNull final IoTSensor sensor) {
        return this.ioTSensorService.registerIoTSensor(sensor);
    }
}