package com.itodatamp.mpapigateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itodatamp.mpapigateway.config.PropertiesBean;
import com.itodatamp.mpapigateway.dto.AuthDTO;
import com.itodatamp.mpapigateway.security.JWTTokenService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DSPService {

    private final PropertiesBean properties;
    private final JWTTokenService jwtTokenService;
    ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public ResponseEntity<String> getChallenge(String dspAccountAddress, String dspContractAddress) {
        OkHttpClient client = new OkHttpClient();
        URL url = new URL(properties.getBcClientURL()
                .concat("/dsp")
                .concat("?dspContractAddress=")
                .concat(dspContractAddress)
                .concat("&dspAccountAddress=")
                .concat(dspAccountAddress));

        Request request = new Request.Builder()
//                .headers(tracingHeaders)
                .url(url)
                .get()
                .build();

        log.info("Submitting GET request: ".concat(url.toString()));
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!Boolean.parseBoolean(response.body().string()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        UUID nonce = UUID.randomUUID();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody nonceBody = RequestBody.create(mediaType, mapper.writeValueAsString(
                AuthDTO.builder()
                        .contractAddress(dspContractAddress)
                        .nonce(nonce.toString())
                        .build()
        ));


        /* saving the nonce on the entity manager */
        Request nonceRequest = new Request.Builder()
//                .headers(tracingHeaders)
                .url(properties.getEntityManagerURL().concat("/auth"))
                .post(nonceBody)
                .addHeader("Content-Type", "application/json")
                .build();
        Response res = client.newCall(nonceRequest).execute();

        if (HttpStatus.valueOf(res.code()) == HttpStatus.OK)
            return ResponseEntity.ok(nonce.toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @SneakyThrows
    public ResponseEntity<String> verifyChallenge(Map<String, Object> payload) {
        /* extract body params */
        String signature = payload.get("signature").toString();
        String nonce = payload.get("nonce").toString();
        String dspAccountAddress = payload.get("dspAccountAddress").toString();
        String dspContractAddress = payload.get("dspContractAddress").toString();

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

            URL url = new URL(properties.getEntityManagerURL().concat("/auth"));
            RequestBody dspBody = RequestBody.create(mediaType, mapper.writeValueAsString(authDTO));
            Request dspRequest = new Request.Builder()
                    .url(url)
                    .post(dspBody)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response res = client.newCall(dspRequest).execute();

            if (HttpStatus.valueOf(res.code()) == HttpStatus.OK)
                return ResponseEntity.ok(jwtToken);
        }


        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
