package com.itodatamp.mpapigateway.security;
//
//import com.food.chain.server.fcserver.domain.Producer;

import java.util.Optional;

public interface UserAuthenticationService {

    Optional<String> login(final String username, final String password);

//    Optional<Producer> findByToken(final String token);

    void logout(final String token);

}
