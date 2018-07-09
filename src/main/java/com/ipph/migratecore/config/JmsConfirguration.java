package com.ipph.migratecore.config;

import javax.jms.Queue;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JmsConfirguration {
    public static final String QUEUE_MESSAGEMODEL = "activemq_messagemodel_queue";
    @Bean
    public Queue messageTopic() {
        return new ActiveMQQueue(QUEUE_MESSAGEMODEL);
    }
}