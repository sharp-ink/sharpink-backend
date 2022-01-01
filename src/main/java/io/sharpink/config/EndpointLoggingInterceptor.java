package io.sharpink.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class EndpointLoggingInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info(prettyPrinting(request));
        return true;
    }

    private String prettyPrinting(HttpServletRequest request) {
        String res = request.getMethod() + ' ' + request.getRequestURI();

        // display query params for GET requests
        if (StringUtils.isNotEmpty(request.getQueryString())) {
            res += '?' + request.getQueryString();
        }

        return res;
    }
}
