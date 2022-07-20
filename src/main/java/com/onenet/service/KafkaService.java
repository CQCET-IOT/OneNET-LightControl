package com.onenet.service;

import com.onenet.wrapper.MessageClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class KafkaService {
    @Autowired
    private KafkaTemplate kafkaTemplate;
    private boolean isStart = false;
    private static List<MessageClient> terminalLists = new CopyOnWriteArrayList<>();
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private List<MessageClient> getClients(String user) {
        List<MessageClient> lists = new ArrayList<>();
        for (MessageClient client: terminalLists) {
            //状态异常的要清理，暂时放这
            if (!client.isLife()){
                delClient(client);
            }else if(client.getUserid().equals(user)){
                lists.add(client);
            }
        }
        return lists;
    }
    private void setClient(MessageClient client) {
        if (client != null) {
            terminalLists.add(client);
        } else {
            logger.error("the MessageClient is null on setClient!");
        }
    }
    private void delClient(MessageClient client){
        if (client != null) {
            boolean suc = terminalLists.remove(client);
        } else{
            logger.info("the MessageClient is null on delClient!");
        }
    }
    @KafkaListener(topics = {"${spring.kafka.topic-name}"})
    public void listenerMessage(ConsumerRecord<String, String> record) {
        logger.info("接收到kafka消息键为:{},消息值为:{},消息头为:{},消息分区为:{},消息主题为:{}", record.key(), record.value(), record.headers(), record.partition(), record.topic());
    }

    /**
     * Kafka消息接收器
     * @apiNote 可以有多个，以groupId、topics作为接受消息匹配条件
     * @apiNote groupId:消息产生者发送的目标群组，topics:消息主题
     * @param user 暂定参数，以后重构
     */
    @KafkaListener(groupId = "topic", topics = {"test"})
    public void getMessage(String user) {
        List<MessageClient> lists = getClients(user);
        if (lists == null || lists.size() <=0) {
            //没有客户端需要，消息丢弃
            logger.info("the user:"+user +" have no client, abort message");
            return;
        }
        //临时发个消息，以后重构
        String s = "data:Testing 1,2,3------- " + new Date() ;
        for (MessageClient client: lists) {
            client.push(s);
        }
    }

    public MessageClient registeClient(String user, PrintWriter pw) throws Exception{
        MessageClient client = null;
        if (user != null && pw != null) {
            client = new MessageClient(user, pw);
            logger.info("registe client:"+user + "@" +client.toString());
            setClient(client);
            client.run();
        }
        return client;
    }
    public void unregisteClient(MessageClient client){
        logger.info("unregiste client:"+client.getUserid() + "@" +client.toString());
        delClient(client);
        client.noRun();
    }
}
