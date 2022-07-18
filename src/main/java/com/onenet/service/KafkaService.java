package com.onenet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaService {
    @Autowired
    private KafkaTemplate kafkaTemplate;
    private boolean isStart = false;
    static Map<String, KafkaClient> map = new HashMap<>();
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public void petchMessage(String user) {

    }

    @KafkaListener(groupId = "topic", topics = {"test"})
    public void getMessage(String user) {
        KafkaClient client = map.get(user);
        if (client == null) {
            //没有客户端需要，消息丢弃
            logger.info("No client need, abort:"+user);
            return;
        }
        String s = "data:Testing 1,2,3------- " + new Date() + "\n\n";
        client.push(s);
    }

    public KafkaClient registeClient(String user, PrintWriter pw) {
        KafkaClient client = null;
        if (user != null && pw != null) {
            client = new KafkaClient(user, pw);
            logger.info("Set client:"+user + "@" +client.toString());
            map.put(user, client);
            client.start();
        }
        return client;
    }
    public void unregisteClient(String user){
        if (user != null) {
            KafkaClient client = map.remove(user);
            client = null;
        }
    }
    public class KafkaClient extends Thread {
        Object _lock = new Object();
        String _user;
        PrintWriter _pw;
        String _s;
        boolean _life = true;

        public KafkaClient(String _user, PrintWriter _pw) {
            this._user = _user;
            this._pw = _pw;
        }
        public void push(String s) {
            _s = s;
            logger.info("push message:"+ _s + " to user:"+_user);
            synchronized (_lock) {
                _lock.notify();
            }
        }

        private boolean fetchMessage() {
            logger.info("fetch message:"+ _s + " from user:"+_user);
            if (_s == null || "".equals(_s)) {
                return false;
            } else {
                _pw.write(_s);
                _s = "";
                if (_pw.checkError()) {
                    logger.info("push message:"+ _s + " to user:"+_user);
                    _pw.close();
                    _life = false;
                }
                return true;
            }
        }

        @Override
        public void run() {
            super.run();
            while (_life) {
                if (!fetchMessage()) {
                    try {
                        synchronized (_lock) {
                            _lock.wait(10000);
                        }
                    } catch (InterruptedException e) {
                        logger.info("fetchMessage have waiting one more 10s");
                    }
                }
            }
        }
    }
}
