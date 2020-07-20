package com.itodatamp.mpapigateway.security;

import com.itodatamp.mpapigateway.config.PropertiesBean;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Optional;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class TokenAuthenticationService implements DSAuthenticationService {

    @NonNull
    TokenService tokens;

    @NonNull
    LogoutService logoutService;

    private final PropertiesBean properties;

    @Override
    public Optional<String> login(final String username, final String password) {
//        return producerRepository
//                .findProducerByUsername(username)
//                .filter(user -> passwordEncoder().matches(password, producerRepository.findProducerByUsername(username).get().getPassword()))
//                .map(user -> tokens.expiring(ImmutableMap.of("username", username)));
        return Optional.of("");
    }

    @SneakyThrows
    @Override
    public Optional<User> findByToken(final String token) {
//        return Optional
//                .of(tokens.verify(token))
//                .map(map -> map.get("username"))
//                .flatMap(producerRepository::findProducerByUsername);


        OkHttpClient client = new OkHttpClient();
        URL url = new URL(properties.getEntityManagerURL()
                .concat("/auth/jwt/exists")
                .concat("?jwt=")
                .concat(token));

        Request request = new Request.Builder()
//                .headers(tracingHeaders)
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

        if (Boolean.parseBoolean(response.body().string()))
            return Optional.of(User.builder().build());



        return Optional.empty();
    }

    @Override
    public void logout(final String token) {
        logoutService.getLogoutTokens().put(token, Calendar.getInstance());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}