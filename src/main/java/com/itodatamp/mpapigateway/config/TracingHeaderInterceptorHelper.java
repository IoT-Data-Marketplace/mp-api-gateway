package com.itodatamp.mpapigateway.config;

import graphql.schema.DataFetchingEnvironment;
import graphql.servlet.GraphQLContext;
import lombok.extern.java.Log;
import okhttp3.Headers;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Log
@Component
public class TracingHeaderInterceptorHelper {

//    https://istio.io/latest/docs/tasks/observability/distributed-tracing/overview/#trace-context-propagation
    public Headers getTracingHeaders(DataFetchingEnvironment env) {
        Map<String, String> headersMap = new HashMap<>();
        GraphQLContext context =  env.getContext();
        HttpServletRequest request = context.getHttpServletRequest().get();
        String[] tracingHeaders = new String[]{
                "x-request-id",
                "x-b3-traceid",
                "x-b3-spanid",
                "x-b3-parentspanid",
                "x-b3-sampled",
                "x-b3-flags",
                "x-ot-span-context",
        };
        for (String tracingHeader : tracingHeaders) {
            try {
                if (request.getHeader(tracingHeader) != null)
                    headersMap.put(tracingHeader, request.getHeader(tracingHeader));
            } catch (Exception e) {
                log.warning("Header".concat(tracingHeader).concat(" not found"));
            }
        }
        return Headers.of(headersMap);
    }

}
