package com.sports.jersey.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "http.logging")
public class HttpLoggingProperties {
    
    /**
     * Enable or disable HTTP logging
     */
    private boolean enabled = true;
    
    /**
     * Include query string in request logging
     */
    private boolean includeQueryString = false;
    
    /**
     * Include authenticated user information in logs
     */
    private boolean includeUser = true;
    
    /**
     * Paths to exclude from logging
     */
    private List<String> excludePaths = new ArrayList<>();
    
    public HttpLoggingProperties() {
        // Default excluded paths
        excludePaths.add("/actuator");
        excludePaths.add("/health");
        excludePaths.add("/metrics");
    }
    
    // Getters and setters
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isIncludeQueryString() {
        return includeQueryString;
    }
    
    public void setIncludeQueryString(boolean includeQueryString) {
        this.includeQueryString = includeQueryString;
    }
    
    public boolean isIncludeUser() {
        return includeUser;
    }
    
    public void setIncludeUser(boolean includeUser) {
        this.includeUser = includeUser;
    }
    
    public List<String> getExcludePaths() {
        return excludePaths;
    }
    
    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths;
    }
}