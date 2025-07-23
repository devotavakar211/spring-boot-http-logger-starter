package com.sports.jersey.filter;

import com.sports.jersey.config.CachedBodyHttpServletRequest;
import com.sports.jersey.util.HttpLoggingProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class HttpLoggingFilter extends OncePerRequestFilter {
    
    private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String MDC_TRACE_ID = "traceId";
    private static final String MDC_CORRELATION_ID = "correlationId";
    private static final String MDC_USER = "user";
    
    private final HttpLoggingProperties properties;
    
    public HttpLoggingFilter(HttpLoggingProperties properties) {
        this.properties = properties;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // Generate or extract trace ID
        String traceId = extractOrGenerateTraceId(request);
        
        // Extract correlation ID if present
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        
        // Set MDC context
        MDC.put(MDC_TRACE_ID, traceId);
        if (correlationId != null) {
            MDC.put(MDC_CORRELATION_ID, correlationId);
        }
        
        // Extract user information if available
        if (properties.isIncludeUser()) {
            extractAndSetUserInfo();
        }
        response.setHeader(TRACE_ID_HEADER, traceId);
        // Wrap the request to cache the body
        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);

        // Log incoming request
        logIncomingRequest(wrappedRequest, traceId);
        
        // Track start time
        long startTime = System.currentTimeMillis();
        
        try {
            // Continue filter chain
            filterChain.doFilter(wrappedRequest, response);
        } finally {
            // Calculate duration
            long duration = System.currentTimeMillis() - startTime;
            
            // Log completed request
            logCompletedRequest(wrappedRequest, response, traceId, duration);
            
            // Clear MDC
            MDC.remove(MDC_TRACE_ID);
            MDC.remove(MDC_CORRELATION_ID);
            MDC.remove(MDC_USER);
        }
    }
    
    private String extractOrGenerateTraceId(HttpServletRequest request) {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }
        return traceId;
    }
    
    private void extractAndSetUserInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
                String username = authentication.getName(); // Safe and consistent across providers
                MDC.put(MDC_USER, username);
            }
        } catch (Exception e) {
            // Security context not available, skip user extraction
            log.trace("Could not extract user information: {}", e.getMessage());
        }
    }
    
    private void logIncomingRequest(HttpServletRequest request, String traceId) {
        String clientIp = getClientIp(request);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String username = MDC.get(MDC_USER);

        String fullPath = queryString != null ? uri + "?" + queryString : uri;

        StringBuilder params = new StringBuilder();
        request.getParameterMap().forEach((key, values) ->
                params.append(key).append("=").append(Arrays.toString(values)).append(" ")
        );

        // Read body from the wrapped request
        String body = "";
        try {
            body = new String(request.getInputStream().readAllBytes());
            if (body.length() > 1000) {
                body = body.substring(0, 1000) + "...(truncated)";
            }
        } catch (IOException e) {
            log.warn("Could not read request body: {}", e.getMessage());
        }

        if (username != null) {
            log.info("[{}] [{}] Incoming {} request to {} from IP {} | Params: {} | Body: {}",
                    traceId, username, method, fullPath, clientIp, params.toString().trim(), body);
        } else {
            log.info("[{}] Incoming {} request to {} from IP {} | Params: {} | Body: {}",
                    traceId, method, fullPath, clientIp, params.toString().trim(), body);
        }
    }
    
    private void logCompletedRequest(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   String traceId, 
                                   long duration) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();
        String username = MDC.get(MDC_USER);


        if (username != null) {
            log.info("[{}] [{}] Completed {} request to {} with status {} in {} ms", traceId, username, method, uri, status, duration);
        } else {
            log.info("[{}] Completed {} request to {} with status {} in {} ms", traceId, method, uri, status, duration);
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        // Check for forwarded headers (when behind proxy/load balancer)
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        };
        
        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Handle comma-separated IPs (take the first one)
                return ip.split(",")[0].trim();
            }
        }
        
        // Fall back to remote address
        return request.getRemoteAddr();
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip logging for excluded paths
        return properties.getExcludePaths().stream()
                .anyMatch(excludePath -> path.startsWith(excludePath));
    }
}