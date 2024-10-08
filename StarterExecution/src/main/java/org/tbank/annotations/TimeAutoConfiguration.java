package org.tbank.annotations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class TimeAutoConfiguration {
    @Bean
    public TimeExecutionAspect timeExecutionAspect() {
        return new TimeExecutionAspect();
    }
}
