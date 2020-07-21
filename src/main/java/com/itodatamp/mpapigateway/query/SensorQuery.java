package com.itodatamp.mpapigateway.query;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.itodatamp.mpapigateway.config.HeaderInterceptorHelper;
import com.itodatamp.mpapigateway.dto.SensorDTO;
import com.itodatamp.mpapigateway.dto.SensorSummaryDTO;
import com.itodatamp.mpapigateway.service.SensorService;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SensorQuery implements GraphQLQueryResolver {

    private final SensorService sensorService;
    private final HeaderInterceptorHelper headerInterceptorHelper;

    public SensorQuery(SensorService sensorService, HeaderInterceptorHelper headerInterceptorHelper) {
        this.sensorService = sensorService;
        this.headerInterceptorHelper = headerInterceptorHelper;
    }


    public List<SensorDTO> getAllSensors(final int count, DataFetchingEnvironment env) {
        return this.sensorService.getAllSensors(count, headerInterceptorHelper.getTracingHeaders(env));
    }

    public Optional<SensorDTO> getSensorByContractAddress(final String sensorContractAddress, DataFetchingEnvironment env) {
        return this.sensorService.getSensorByContractAddress(sensorContractAddress, headerInterceptorHelper.getTracingHeaders(env));
    }

    public SensorSummaryDTO getSensorSummary(final String sensorContractAddress, DataFetchingEnvironment env) {
        return this.sensorService.getSensorSummary(sensorContractAddress, headerInterceptorHelper.getTracingHeaders(env));
    }

}
