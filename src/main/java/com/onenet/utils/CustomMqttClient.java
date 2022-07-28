package com.onenet.utils;

import com.onenet.exception.OnenetExceptionHandler;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * mqtt custom client
 */
public class CustomMqttClient {

    private String broker;   //服务端
    private String userName; //产品id
    private String password; //token
    private String clientId; //设备id

    private MqttClient mqttClient;  //真正的mqtt对象
    private MqttCallback mqttCallback;
    private MqttConnectOptions options;

    public CustomMqttClient(String broker, String clientId
            , String userName
            , String password
            , MqttCallback mqttCallback) throws MqttException {
        this.broker = broker;
        this.clientId = clientId;
        this.userName = userName;
        //this.password = getAuthorization(userId, accessKey);
        this.password = password;
        this.mqttCallback = mqttCallback;
        initClient();
        initOptions();
    }

    private void initClient() throws MqttException {
        MqttClient mqttClient = new MqttClient(this.broker, this.clientId, new MemoryPersistence());
        // 配置回调
        mqttClient.setCallback(this.mqttCallback);
        this.mqttClient = mqttClient;
    }

    private void initOptions() {
        // MQTT连接项
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(this.userName);
        options.setPassword(this.password.toCharArray());
        // 连接超时时间
        options.setConnectionTimeout(30);
        // 发送或接收消息之间的最大间隔时间
        options.setKeepAliveInterval(60);
        // 不保留对话
        options.setCleanSession(true);
        // 设置mqtt版本
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        this.options = options;
    }

    public void connect() throws MqttException {
        this.mqttClient.connect(this.options);
    }

    public void disconnect() throws MqttException {
        this.mqttClient.disconnect();
    }

    public void subscribe(String productId, String deviceName) throws MqttException {
        // 设备上行topic（包括设备生命周期状态变更、属性、事件功能点及命令下发响应）
        String[] upTopics = {
                "$sys/" + productId + "/" + deviceName + "/thing/lifecycle",
                "$sys/" + productId + "/" + deviceName + "/thing/property",
                "$sys/" + productId + "/" + deviceName + "/thing/event",
                "$sys/" + productId + "/" + deviceName + "/thing/property/set/reply"
        };
        this.mqttClient.subscribe(upTopics);
    }

    public void pubProperty(String productId, String deviceName, String identifier, Object value) throws MqttException {
        // 设备属性设置topic
        String propertyTopic = "$sys/" + productId + "/" + deviceName + "/thing/property/set";
        publish(propertyTopic, getPayload(identifier, value));
    }

    public void pubDesired(String productId, String deviceName, String identifier, Object value) throws MqttException {
        // 设备期望值设置
        String desiredTopic = "$sys/" + productId + "/" + deviceName + "/thing/property/desired/set";
        publish(desiredTopic, getPayload(identifier, value));
    }

    private void publish(String topic, String payload) throws MqttException {
        this.mqttClient.publish(topic, new MqttMessage(payload.getBytes()));
    }

    public void close() throws MqttException {
        this.mqttClient.close();
    }

    private String getPayload(String identifier, Object value) {
        return "{\"data\":{\"params\":{\"" + identifier + "\":" + value + "},\"version\":\"1.0\"},\"requestId\":\"" + uuid() + "\"}";
    }

    private String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String getAuthorization(String userId, String accessKey) {
        long et = LocalDateTime.now().plusHours(12).toInstant(ZoneOffset.of("+8")).getEpochSecond();
        String method = "sha1";
        String res = "userid/" + userId;
        String version = "2020-05-29";

        String sign = null;
        String forSign = et + "\n" + method + "\n" + res + "\n" + version;
        try {
            sign = new String(Base64.getEncoder().encode(HmacEncrypt(forSign
                    , Base64.getDecoder().decode(accessKey)
                    , method)));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            OnenetExceptionHandler.getMessage(e);
        }
        if (isBlank(sign)) {
            return null;
        }
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("et", et);
        paramsMap.put("method", method);
        paramsMap.put("version", version);
        try {
            paramsMap.put("sign", URLEncoder.encode(sign, "utf-8"));
            paramsMap.put("res", URLEncoder.encode(res, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            OnenetExceptionHandler.getMessage(e);
        }
        return mapToParam(paramsMap);
    }

    private static byte[] HmacEncrypt(String data, byte[] key, String signatureMethod)
            throws NoSuchAlgorithmException, InvalidKeyException {
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKeySpec signinKey = null;
        signinKey = new SecretKeySpec(key,
                "Hmac" + signatureMethod.toUpperCase());

        //生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance("Hmac" + signatureMethod.toUpperCase());

        //用给定密钥初始化 Mac 对象
        mac.init(signinKey);

        //完成 Mac 操作
        return mac.doFinal(data.getBytes());
    }

    private String mapToParam(Map<String, Object> map) {
        Set<String> keys = map.keySet();
        StringBuilder builder = new StringBuilder();
        keys.forEach(key ->
                builder.append(key)
                        .append("=")
                        .append(map.get(key))
                        .append("&"));
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    private boolean isBlank(String str) {
        return null == str || "".equals(str.trim());
    }
}