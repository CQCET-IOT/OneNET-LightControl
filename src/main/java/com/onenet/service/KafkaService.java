package com.onenet.service;

import com.onenet.wrapper.MessageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaService {
    @Autowired
    private KafkaTemplate kafkaTemplate;
    private boolean isStart = false;
    private static Map<String, MessageClient> map = new HashMap<>();
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private synchronized MessageClient getClient(String user) {
        return user==null?null: map.get(user);
    }
    private synchronized void setClient(String user, MessageClient client) {
        if (user != null && client != null) {
            map.put(user, client);
        } else {
            logger.error("the user :"+user +" client is null");
        }
    }
    private synchronized void delClient(String user){
        MessageClient client = map.remove(user);
        if (client != null) {
            client.noRun();
        } else{
            logger.info("No client match:"+user +" when delete it");
        }
    }

    @KafkaListener(groupId = "topic", topics = {"test"})
    public void getMessage(String user) {
        MessageClient client = getClient(user);
        if (client == null) {
            //没有客户端需要，消息丢弃
            logger.info("the user:"+user +" have no client, abort message");
            return;
        }
        //临时发个消息，以后重构
        String s = "data:Testing 1,2,3------- " + new Date() + "\n\n";
        client.push(s);
    }

    public MessageClient registeClient(String user, PrintWriter pw) {
        MessageClient client = getClient(user);
        if (client != null) {
            unregisteClient(user);
        }
        if (user != null && pw != null) {
            client = new MessageClient(user, pw);
            logger.info("Set client:"+user + "@" +client.toString());
            setClient(user, client);
            client.start();
        }
        return client;
    }
    public void unregisteClient(String user){
        if (user != null) {
            delClient(user);
        } else {
            logger.error("No client match:"+user +" in unregisteClient");
        }
    }
}
