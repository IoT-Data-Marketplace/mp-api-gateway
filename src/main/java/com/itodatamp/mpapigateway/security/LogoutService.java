package com.itodatamp.mpapigateway.security;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Service
@EnableScheduling
public class LogoutService {

    private Map<String, Calendar> logoutTokens = new HashMap<>();

    public Map<String, Calendar> getLogoutTokens() {
        return logoutTokens;
    }


    //https://dzone.com/articles/running-on-time-with-springs-scheduled-tasks
    //    @Scheduled(cron = "0/20 * * * * ?")  // every 20sec for testing
    @Scheduled(cron = "0 0 1 * * ?") // every day at 1AM
    public void cleanUpTokens() {
        Calendar dayBefore = Calendar.getInstance();
        dayBefore.add(Calendar.DATE, -1);

        getLogoutTokens().forEach((token, date) -> {
            if (date.before(dayBefore)) {
                getLogoutTokens().remove(token);
            }
        });
    }

}
