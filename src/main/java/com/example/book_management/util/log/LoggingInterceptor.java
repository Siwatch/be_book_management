package com.example.book_management.util.log;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String correlationId = UUID.randomUUID().toString();
        request.setAttribute("correlationId", correlationId);

        log.info("[{}] --> {} {}", correlationId, request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String correlationId = (String) request.getAttribute("correlationId");
        log.info("[{}] <-- {} {} (Status: {})", correlationId, request.getMethod(), request.getRequestURI(), response.getStatus());
    }
}