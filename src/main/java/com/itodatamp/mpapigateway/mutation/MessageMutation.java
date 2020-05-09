package com.itodatamp.mpapigateway.mutation;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.itodatamp.mpapigateway.dto.HttpResponseDTO;
import com.itodatamp.mpapigateway.dto.NewMessagesDTO;
import com.itodatamp.mpapigateway.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMutation implements GraphQLMutationResolver {

    private final MessageService messageService;

    public HttpResponseDTO sendMessages(@NotNull final String sensorContractAddress, @NotNull NewMessagesDTO newMessagesDTO) {
        return messageService.sendMessages(sensorContractAddress, newMessagesDTO);
    }

}
