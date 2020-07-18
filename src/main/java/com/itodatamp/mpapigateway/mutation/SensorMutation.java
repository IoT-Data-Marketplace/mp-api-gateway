package com.itodatamp.mpapigateway.mutation;


import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.itodatamp.mpapigateway.config.TracingHeaderInterceptorHelper;
import com.itodatamp.mpapigateway.dto.HttpResponseDTO;
import com.itodatamp.mpapigateway.service.SensorService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class SensorMutation implements GraphQLMutationResolver {

    private final SensorService sensorService;
    private final TracingHeaderInterceptorHelper tracingHeaderInterceptorHelper;

    public HttpResponseDTO registerSensor(@NotNull final String sensorContractAddress, DataFetchingEnvironment env) {
        return this.sensorService.activateSensor(sensorContractAddress, tracingHeaderInterceptorHelper.getTracingHeaders(env));
    }
}