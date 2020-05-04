package com.itodatamp.mpapigateway.query;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.itodatamp.mpapigateway.dao.IoTSensor;
import com.itodatamp.mpapigateway.service.controller.IoTSensorService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class IoTSensorQuery implements GraphQLQueryResolver {

    private final IoTSensorService ioTSensorService;

    public IoTSensorQuery(IoTSensorService ioTSensorService) {
        this.ioTSensorService = ioTSensorService;
    }


    public List<IoTSensor> getAllIoTSensors(final int count) {
        return this.ioTSensorService.getAllIoTSensors(count);
    }

    public Optional<IoTSensor> getIoTSensorByContractAddress(final String sensorContractAddress) {
        return this.ioTSensorService.getIoTSensorByContractAddress(sensorContractAddress);
    }

}
