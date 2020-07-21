//package com.example.community.event;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class EventConsumer {
//
//    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
//
//    @KafkaListener(topics = {"test"})
//    public void handleMessage(ConsumerRecord record) {
//        System.out.println("测试：" + record.value());
//
//    }
//}
