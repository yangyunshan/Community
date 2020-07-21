//package com.example.community.event;
//
//import com.alibaba.fastjson.JSONObject;
//import com.example.community.entity.Event;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class EventProducer {
//
//    @Autowired
//    private KafkaTemplate kafkaTemplate;
//
//    //处理事件
//    public void fireEvent(String topic, String content) {
//        //将事件发布到指定的主题
//        kafkaTemplate.send(topic, content);
//    }
//}
