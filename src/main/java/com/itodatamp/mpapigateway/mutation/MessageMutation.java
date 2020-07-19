package com.itodatamp.mpapigateway.mutation;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.itodatamp.mpapigateway.config.TracingHeaderInterceptorHelper;
import com.itodatamp.mpapigateway.dto.HttpResponseDTO;
import com.itodatamp.mpapigateway.dto.NewMessagesDTO;
import com.itodatamp.mpapigateway.service.MessageService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMutation implements GraphQLMutationResolver {

    private final MessageService messageService;
    private final TracingHeaderInterceptorHelper tracingHeaderInterceptorHelper;

    public HttpResponseDTO sendMessages(@NotNull final String sensorContractAddress, @NotNull NewMessagesDTO newMessagesDTO, DataFetchingEnvironment env) {
        return messageService.publishMessages(sensorContractAddress, newMessagesDTO, tracingHeaderInterceptorHelper.getTracingHeaders(env));
    }

}
