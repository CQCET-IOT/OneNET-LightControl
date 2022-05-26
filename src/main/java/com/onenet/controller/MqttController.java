package com.onenet.controller;

import com.onenet.config.Config;
import com.onenet.utils.HttpSendCenter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MqttController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @RequestMapping("/")
    public String login(Map<String, Object> map){
        map.put("msg","随时随地控制您的设备");
        return "login";
    }
    @RequestMapping("command")
    public String hello(Map<String, Object> map, HttpServletRequest request){
        //提交的数据
        String deviceid = request.getParameter("deviceid");
        String apiKey = request.getParameter("apikey");
        String command = request.getParameter("command");
        logger.info("deviceid = " + deviceid+" apiKey = " + apiKey+ " command = " + command);
        String url = Config.getDomainName() + "/cmds?device_id=" + deviceid;
        JSONObject re = HttpSendCenter.postStr(apiKey, url, command);
        logger.info("return info = " + re.toString());
        map.put("msg", re.toString());
        return "login";
    }

    @RequestMapping("mqtt")
    @ResponseBody
    public String mqtt(HttpServletRequest request){
        //提交的数据http://url?msg=xxx&nonce=xxx&signature=xxx
        String msg = request.getParameter("msg");
        String nonce = request.getParameter("nonce");
        String signature = request.getParameter("signature");
        logger.info("msg = " + msg+" nonce = " + nonce+ " signature = " + signature);
        Map<String,Object> map = new HashMap<String,Object>();
        //先不做校验
        map.put("msg", msg);
        return msg;
    }

    @RequestMapping("devicecreate")
    @ResponseBody
    public Map<String,Object> createdevice(HttpServletRequest request){
        //提交的数据http://url?msg=xxx&nonce=xxx&signature=xxx
        String msg = request.getParameter("msg");
        String nonce = request.getParameter("nonce");
        String signature = request.getParameter("signature");
        logger.info("msg = " + msg+" nonce = " + nonce+ " signature = " + signature);
        Map<String,Object> map = new HashMap<String,Object>();
        String url = Config.getOneNETDomainName() + "/common?action=" + "CreateDevice&version=1";
        String auth = "version=2020-05-29&res=userid%2F148373&et=1737314191&method=md5&sign=Gx6tO9WBob1sVkThWSEddg%3D%3D";
        String command = "{\"product_id\":\"6uPWaT37Ca\",\"device_name\":\"KIRINTEST\"}";
        logger.info("url = " + url+" auth = " + auth+ " command = " + command);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");
        headers.put("Cache-Control","no-cache");
        //headers.put("Host", "openapi.heclouds.com");
        headers.put("authorization",auth);
        JSONObject re = HttpSendCenter.postStr(headers, url, command);
        logger.info("return info = " + re.toString());
        map.put("msg", re.toString());
        return map;
    }
}
