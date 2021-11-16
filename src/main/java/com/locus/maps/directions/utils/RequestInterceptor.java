package com.locus.maps.directions.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class RequestInterceptor implements HandlerInterceptor {
    protected static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (Optional.ofNullable(request.getHeader("request-id")).isPresent())
            MDC.put("request-id", request.getHeader("request-id"));
        else {
            logger.debug("Using Random UUID requestId");
            MDC.put("request-id", UUID.randomUUID().toString());
        }
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) {
        MDC.clear();
    }
}
