package com.onenet.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public void sendSyncMessage(String topic, String data) throws ExecutionException, InterruptedException {
        SendResult sendResult = kafkaTemplate.send(topic, data).get();
        RecordMetadata recordMetadata = sendResult.getRecordMetadata();
        logger.info("发送同步消息成功!发送的主题为:{}", recordMetadata.topic());
    }

    public void sendMessage(String topic, String data) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, data);
        future.addCallback(success -> logger.info("发送消息成功!"), failure -> logger.error("发送消息失败!失败原因是:{}", failure.getMessage()));
    }

    public void sendMessage(ProducerRecord<String, String> record) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(record);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.error("发送消息失败!失败原因是:{}", throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> sendResult) {
                RecordMetadata metadata = sendResult.getRecordMetadata();
                logger.info("发送消息成功!消息主题是:{},消息分区是:{}", metadata.topic(), metadata.partition());
            }
        });
    }

    public void sendMessage(Message<String> message) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(message);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.error("发送消息失败!失败原因是:{}", throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> sendResult) {
                RecordMetadata metadata = sendResult.getRecordMetadata();
                logger.info("发送消息成功!消息主题是:{},消息分区是:{}", metadata.topic(), metadata.partition());
            }
        });
    }

    public void sendMessage(String topic, String key, String data) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, data);
        future.addCallback(success -> logger.info("发送消息成功!"), failure -> logger.error("发送消息失败!失败原因是:{}", failure.getMessage()));
    }

    public void sendMessage(String topic, Integer partition, String key, String data) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, partition, key, data);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.error("发送消息失败!失败原因是:{}", throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> sendResult) {
                RecordMetadata metadata = sendResult.getRecordMetadata();
                logger.info("发送消息成功!消息主题是:{},消息分区是:{}", metadata.topic(), metadata.partition());
            }
        });
    }

    public void sendMessage(String topic, Integer partition, Long timestamp, String key, String data) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, partition, timestamp, key, data);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.error("发送消息失败!失败原因是:{}", throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> sendResult) {
                RecordMetadata metadata = sendResult.getRecordMetadata();
                logger.info("发送消息成功!消息主题是:{},消息分区是:{}", metadata.topic(), metadata.partition());
            }
        });
    }
}

