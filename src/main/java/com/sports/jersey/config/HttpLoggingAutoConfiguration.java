package com.sports.jersey.config;

import com.sports.jersey.filter.HttpLoggingFilter;
import com.sports.jersey.util.HttpLoggingProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import jakarta.servlet.Filter;
import jakarta.servlet.DispatcherType;

@AutoConfiguration
@ConditionalOnClass(Filter.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
        prefix = "http.logging",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(HttpLoggingProperties.class)
public class HttpLoggingAutoConfiguration {

    @Bean
    @ConditionalOnClass(name = {
            "jakarta.servlet.http.HttpServletRequest",
            "org.springframework.web.servlet.DispatcherServlet"
    })
    public FilterRegistrationBean<HttpLoggingFilter> httpLoggingFilter(
            HttpLoggingProperties properties) {

        try {
            FilterRegistrationBean<HttpLoggingFilter> registrationBean =
                    new FilterRegistrationBean<>();

            registrationBean.setFilter(new HttpLoggingFilter(properties));
            registrationBean.addUrlPatterns("/*");
            registrationBean.setName("httpLoggingFilter");
            registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
            registrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);

            return registrationBean;
        } catch (Exception e) {
            // Silently fail if something goes wrong during initialization
            return null;
        }
    }
}