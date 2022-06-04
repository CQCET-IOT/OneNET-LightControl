package com.onenet.utils;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * mqtt custom callback
 */
public class CustomMqttCallback implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("连接丢失，可自己实现重新连接");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("接收到订阅消息:" + message);
        System.out.println("消息内容:" + new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("发送完成:" + token.isComplete());
    }
}
