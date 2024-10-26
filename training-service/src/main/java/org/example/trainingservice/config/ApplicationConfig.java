package org.example.trainingservice.config;

import lombok.RequiredArgsConstructor;
import org.example.trainingservice.filter.SLF4JMDCFilter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@Configurable
@RequiredArgsConstructor
public class ApplicationConfig {
    private final SLF4JMDCFilter slf4jMDCFilter;

    @Bean
    public FilterRegistrationBean<SLF4JMDCFilter> servletRegistrationBean() {
        final FilterRegistrationBean<SLF4JMDCFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(slf4jMDCFilter);
        filterRegistrationBean.setOrder(2);
        return filterRegistrationBean;
    }
}
