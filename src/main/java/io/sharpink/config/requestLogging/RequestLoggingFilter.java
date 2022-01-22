package io.sharpink.config.requestLogging;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Filter to intercept all incoming requests to log them (including path params, query params, or body payloads).
 * Automatically registered for the application since Spring detects it during component-scanning.
 */
@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isEndpointWithPayload(request)) {
            var cachedPayloadHttpServletRequest = new CachedPayloadRequestWrapper(request);
            log.info(getRequestMethodAndUrl(request) + " payload = " + new String(cachedPayloadHttpServletRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
            filterChain.doFilter(cachedPayloadHttpServletRequest, response);
        } else {
            log.info(getRequestMethodAndUrl(request));
            filterChain.doFilter(request, response);
        }
    }

    private boolean isEndpointWithPayload(HttpServletRequest request) {
        var requestMethod = request.getMethod();
        return requestMethod.equals("PATCH") || requestMethod.equals("POST") || requestMethod.equals("PUT");
    }

    private String getRequestMethodAndUrl(HttpServletRequest request) {
        String res = request.getMethod() + ' ' + request.getRequestURI();

        // display query params for GET requests
        if (StringUtils.isNotEmpty(request.getQueryString())) {
            res += '?' + request.getQueryString();
        }

        return res;
    }
}
