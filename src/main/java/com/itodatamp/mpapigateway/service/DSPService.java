package com.itodatamp.mpapigateway.service;

import com.itodatamp.mpapigateway.config.PropertiesBean;
import com.itodatamp.mpapigateway.dto.AuthDTO;
import com.itodatamp.mpapigateway.dto.HttpResponseDTO;
import com.itodatamp.mpapigateway.security.JWTTokenService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import static com.itodatamp.mpapigateway.constants.Contants.AUTH_DSE;

@Slf4j
@Service
@RequiredArgsConstructor
public class DSPService {

    private final PropertiesBean properties;
    private final JWTTokenService jwtTokenService;
    private final AuthService authService;

    @SneakyThrows
    public HttpResponseDTO getAuthNonce(String dspAccountAddress, String dspContractAddress, Headers tracingHeaders) {
        OkHttpClient client = new OkHttpClient();
        URL url = new URL(properties.getBcClientURL()
                .concat("/dsp")
                .concat("?dspContractAddress=")
                .concat(dspContractAddress)
                .concat("&dspAccountAddress=")
                .concat(dspAccountAddress));

        Request request = new Request.Builder()
                .headers(tracingHeaders)
                .url(url)
                .get()
                .build();

        log.debug("Submitting GET request: ".concat(url.toString()));
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!Boolean.parseBoolean(response.body().string()))
            return HttpResponseDTO.builder()
                    .responseBody("Smart Contract not found")
                    .statusCode(HttpStatus.FORBIDDEN.value())
                    .build();

        UUID nonce = UUID.randomUUID();

        HttpResponseDTO httpResponseDTO = authService.saveAuthDTO(AuthDTO.builder()
                .contractAddress(dspContractAddress)
                .nonce(nonce.toString())
                .build(), AUTH_DSE, tracingHeaders);

        if (HttpStatus.valueOf(httpResponseDTO.getStatusCode()) != HttpStatus.OK)
            return HttpResponseDTO.builder()
                    .responseBody("Something went wrong while generating and saving the nonce")
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();

        return HttpResponseDTO.builder()
                .responseBody(nonce.toString())
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    @SneakyThrows
    public HttpResponseDTO verifyChallenge(String signature, String nonce, String dspAccountAddress, String dspContractAddress, Headers tracingHeaders) {
        /* validate that the dsp really has signed the nonce */
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("nonce", nonce);
        jsonObject.put("publicAddress", dspAccountAddress);
        jsonObject.put("signature", signature);

        RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
        Request request = new Request.Builder()
                .url(properties.getSignatureVerifierURL().concat("/api/verify"))
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .headers(tracingHeaders)
                .build();
        Response response = client.newCall(request).execute();

        /* if ok, create, save and return the jwt token to dsp */
        if (HttpStatus.valueOf(response.code()) == HttpStatus.OK) {
            Map<String, String> tokenAttributes = Map.of(
                    "dspAccountAddress", dspAccountAddress,
                    "dspContractAddress", dspContractAddress
            );

            String jwtToken = jwtTokenService.permanent(tokenAttributes);
            AuthDTO authDTO = AuthDTO.builder()
                    .contractAddress(dspContractAddress)
                    .nonce(nonce)
                    .jwt(jwtToken)
                    .build();

            HttpResponseDTO httpResponseDTO = authService.saveAuthDTO(authDTO, AUTH_DSE, tracingHeaders);

            if (HttpStatus.valueOf(httpResponseDTO.getStatusCode()) == HttpStatus.OK)
                return HttpResponseDTO.builder()
                        .responseBody(jwtToken)
                        .statusCode(HttpStatus.OK.value())
                        .build();
        }

        return HttpResponseDTO.builder()
                .responseBody("Challenge not correct")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }
}
