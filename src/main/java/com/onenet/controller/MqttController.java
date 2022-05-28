package com.onenet.controller;

import com.onenet.config.Config;
import com.onenet.utils.HttpSendCenter;
import com.onenet.utils.TokenUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MqttController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @RequestMapping("/")
    public String login(Map<String, Object> map) {
        map.put("msg", "随时随地控制您的设备");
        return "login";
    }
    @RequestMapping("index")
    public String index(Map<String, Object> map) {
        return "index";
    }

    @RequestMapping("userlogin")
    public String userlogin(Map<String, Object> map, HttpServletRequest request) {
        //提交的数据
        try {
            String userid = request.getParameter("userid");
            String apiKey = request.getParameter("apikey");
            String command = request.getParameter("command");
            logger.info("userid = " + userid + " apiKey = " + apiKey + " command = " + command);
            HttpSession session = request.getSession();
            session.setAttribute("userid", userid);
            session.setAttribute("apiKey", apiKey);
            String version = Config.getAppVersion();
            String res = "userid/" + userid;
            //String expirationTime = System.currentTimeMillis() / 1000 + 100 * 24 * 60 * 60 + "";
            String expirationTime = "1626956590";
            String method = "md5";
            String token = "";
            token = TokenUtil.assembleToken(version, res, expirationTime, method, apiKey);
            session.setAttribute("token", token);
            logger.info("token:" + token);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            map.put("msg", "参数错误，鉴权失败");
            e.printStackTrace();
        }
        //String url = Config.getDomainName() + "/cmds?device_id=" + deviceid;
        //JSONObject re = HttpSendCenter.postStr(apiKey, url, command);
        //logger.info("return info = " + re.toString());
        return "index";
    }

    @RequestMapping("mqtt")
    @ResponseBody
    public String mqtt(HttpServletRequest request) {
        //提交的数据http://url?msg=xxx&nonce=xxx&signature=xxx
        String msg = request.getParameter("msg");
        String nonce = request.getParameter("nonce");
        String signature = request.getParameter("signature");
        logger.info("msg = " + msg + " nonce = " + nonce + " signature = " + signature);
        Map<String, Object> map = new HashMap<String, Object>();
        //先不做校验
        map.put("msg", msg);
        return msg;
    }

    @RequestMapping("devicecreate")
    @ResponseBody
    public Map<String, Object> createdevice(HttpServletRequest request) {
        //提交的数据http://url?msg=xxx&nonce=xxx&signature=xxx
        String msg = request.getParameter("msg");
        String nonce = request.getParameter("nonce");
        String signature = request.getParameter("signature");
        logger.info("msg = " + msg + " nonce = " + nonce + " signature = " + signature);
        Map<String, Object> map = new HashMap<String, Object>();
        String url = Config.getOneNETDomainName() + "/common?action=" + "CreateDevice&version=1";
        String auth = "version=2020-05-29&res=userid%2F148373&et=1737314191&method=md5&sign=Gx6tO9WBob1sVkThWSEddg%3D%3D";
        String command = "{\"product_id\":\"6uPWaT37Ca\",\"device_name\":\"KIRINTEST\"}";
        logger.info("url = " + url + " auth = " + auth + " command = " + command);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Cache-Control", "no-cache");
        //headers.put("Host", "openapi.heclouds.com");
        headers.put("authorization", auth);
        JSONObject re = HttpSendCenter.postStr(headers, url, command);
        logger.info("return info = " + re.toString());
        map.put("msg", re.toString());
        return map;
    }
}
