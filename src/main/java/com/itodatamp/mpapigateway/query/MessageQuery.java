package com.itodatamp.mpapigateway.query;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.itodatamp.mpapigateway.config.TracingHeaderInterceptorHelper;
import com.itodatamp.mpapigateway.dto.ResponseMessagesDTO;
import com.itodatamp.mpapigateway.service.MessageService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageQuery implements GraphQLQueryResolver {

    private final MessageService messageService;
    private final TracingHeaderInterceptorHelper tracingHeaderInterceptorHelper;

    public ResponseMessagesDTO getMessagesForSensor(final String sensorContractAddress, final int offset, final int count, DataFetchingEnvironment env) {
        return messageService.getMessagesForSensor(sensorContractAddress, offset, count, tracingHeaderInterceptorHelper.getTracingHeaders(env));
    }

}