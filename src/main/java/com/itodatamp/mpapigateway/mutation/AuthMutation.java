package com.itodatamp.mpapigateway.mutation;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.itodatamp.mpapigateway.config.HeaderInterceptorHelper;
import com.itodatamp.mpapigateway.dto.HttpResponseDTO;
import com.itodatamp.mpapigateway.service.DSPService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthMutation implements GraphQLMutationResolver {


    private final DSPService dspService;
    private final HeaderInterceptorHelper headerInterceptorHelper;

    public HttpResponseDTO getAuthNonce(@NotNull final String dspAccountAddress, @NotNull String dspContractAddress, DataFetchingEnvironment env) {
        return dspService.getAuthNonce(dspAccountAddress, dspContractAddress, headerInterceptorHelper.getTracingHeaders(env));
    }

    public HttpResponseDTO verifyAuthChallenge(@NotNull final String signature, @NotNull final String nonce, @NotNull final String dspAccountAddress, @NotNull String dspContractAddress, DataFetchingEnvironment env) {
        return dspService.verifyChallenge(signature, nonce, dspAccountAddress, dspContractAddress, headerInterceptorHelper.getTracingHeaders(env));
    }
}
