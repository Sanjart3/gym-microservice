package org.example.trainingservice.config;

import lombok.RequiredArgsConstructor;
import org.example.trainingservice.filter.Slf4jMDCFilter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@Configurable
@RequiredArgsConstructor
public class ApplicationConfig {
    private final Slf4jMDCFilter slf4jMDCFilter;

    @Bean
    public FilterRegistrationBean<Slf4jMDCFilter> servletRegistrationBean() {
        final FilterRegistrationBean<Slf4jMDCFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(slf4jMDCFilter);
        filterRegistrationBean.setOrder(2);
        return filterRegistrationBean;
    }
}
