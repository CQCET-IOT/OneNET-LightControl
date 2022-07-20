package com.onenet.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;

public class MessageClient /*extends Thread*/ {
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final static int checkTerm = 10;
    private final static int checkTimes = 10;  //keep alive time : checkTerm * checkTimes * 1000
    Object _lock = new Object();
    String _user;
    PrintWriter _pw;
    String _s;
    boolean _life = true;
    int check = 0;

    public boolean isLife() {
        return _life;
    }

    public String getUserid() {
        return _user;
    }

    public void noRun() {
        _life = false;
    }

    public MessageClient(String _user, PrintWriter _pw) {
        this._user = _user;
        this._pw = _pw;
    }

    public void push(String s) {
        _s = s;
        logger.info("push message:" + _s + " to user:" + _user);
        synchronized (_lock) {
            _lock.notify();
        }
    }

    private boolean fetchMessage() throws Exception {
        boolean fetchRes = true;
        check++;
        if (check >= checkTimes) {
            check = 0;
            write(":test"); //keep alive
            fetchRes = false;
        }else if (_s == null || "".equals(_s)) {
            fetchRes = false;
        } else {
            logger.info("fetch message:" + _s + " from user:" + _user);
            if (!write(_s)) {
                fetchRes = false;
            }
            _s ="";
        }
        return fetchRes;
    }

    private boolean write(String str) {
        _pw.write(str + "\n\n");
        _pw.flush();
        if (_pw.checkError()) {
            logger.warn("http push error, this message：" + str + "  may need retry for user:" + _user);
            _pw.close();
            _life = false;
            return false;
        }
        return true;
    }

    //@Override
    public void run() throws Exception {
        //super.run();
        while (_life) {
            if (!fetchMessage()) {
                logger.info("next handle..." + check);
                try {
                    synchronized (_lock) {
                        _lock.wait(checkTerm * 1000);
                    }
                } catch (InterruptedException e) {
                }
            }
        }
        logger.info("client thread " + _user + "@" + this.toString() + " run to end ");
    }
}

