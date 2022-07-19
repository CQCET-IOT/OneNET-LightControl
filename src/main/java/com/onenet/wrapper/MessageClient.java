package com.onenet.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;

public class MessageClient extends Thread {
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    Object _lock = new Object();
    String _user;
    PrintWriter _pw;
    String _s;
    boolean _life = true;

    public void noRun(){
        _life = false;
    }
    public MessageClient(String _user, PrintWriter _pw) {
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
        logger.info("client thread "+_user+"@" +this.toString()+" run to end ");
    }
}

