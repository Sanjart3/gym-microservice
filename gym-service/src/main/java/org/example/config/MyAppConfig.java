package org.example.config;

import org.modelmapper.Condition;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyAppConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        Condition<?, ?> skipNulls = Conditions.isNotNull();

        modelMapper.getConfiguration().setPropertyCondition(skipNulls);

        return modelMapper;
    }

    @Bean
    public LogFilter logFilter(){
        return new LogFilter();
    }
}
