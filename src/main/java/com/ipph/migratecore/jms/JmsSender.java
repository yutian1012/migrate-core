package com.ipph.migratecore.jms;


import javax.jms.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class JmsSender {
    @Autowired
    private Queue messageQueue;
    @Autowired
    private JmsMessagingTemplate jmsTemplate;
    
    /**
     * 发送消息对象到订阅主题
     * @param messageModel
     */
    public void sendMigrateTableInfoByQueue(MigrateMessageModel messageModel) {
    	this.jmsTemplate.convertAndSend(messageQueue,messageModel);
    }
}
