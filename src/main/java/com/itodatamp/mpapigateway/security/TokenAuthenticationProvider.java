package com.itodatamp.mpapigateway.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @NonNull
    DSAuthenticationService dsAuthenticationService;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        final Object token = authentication.getCredentials();
        return Optional.ofNullable(token)
                .map(String::valueOf)
                .flatMap(dsAuthenticationService::findByToken)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Cannot find producer with authentication token=" + token));
    }
}
