package com.ipph.migratecore.jms;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ipph.migratecore.model.MigrateModel;

@Component
public class JmsSender {
    /*@Autowired
    private Queue messageQueue;
    @Autowired
    private JmsMessagingTemplate jmsTemplate;
    
    *//**
     * 发送消息对象到订阅主题
     * @param messageModel
     *//*
    public void sendMessageModelByQueue(MigrateMessageModel messageModel) {
    	this.jmsTemplate.convertAndSend(messageQueue,messageModel);
    }*/
}
