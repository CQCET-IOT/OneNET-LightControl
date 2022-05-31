package com.onenet.controller;

import com.onenet.config.Config;
import com.onenet.dto.Msg;
import com.onenet.dto.TokenParams;
import com.onenet.utils.HttpSendCenter;
import com.onenet.utils.TokenUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    @RequestMapping("logout")
    public String logout(Map<String, Object> map, HttpServletRequest request) {
        request.getSession().invalidate();
        return "login";
    }

    @RequestMapping(value="token")
    public String token(Map<String, Object> map) {

        return "token";
    }

    @RequestMapping(value="dotoken", method = RequestMethod.POST)
    public String dotoken(Map<String, Object> map, TokenParams params) {
        if(null != params){
            logger.info(params.toString());
            String token = handleToken(params);
            map.put("msg",token);
        }
        return "token";
    }
    private String handleToken(TokenParams params){
        String token = "";
        String res = "";
        if(TokenUtil.SourceType.user.name().equals(params.getSourcetype())){
            res = "userid/" + params.getUserid();
        } else if(TokenUtil.SourceType.project.name().equals(params.getSourcetype())){
            res = "projectid/" + params.getProjectid() + "/groupid/" + params.getGroupid();
        } else if(TokenUtil.SourceType.product.name().equals(params.getSourcetype())){
            res = "products/" + params.getProductid() + "/devices/" + params.getDeviceid();
        }
        String version = params.getVersion();
        String expirationTime = System.currentTimeMillis() / 1000 + params.getEt() * 24 * 60 * 60 + "";
        logger.info("Token expiration time:"+ expirationTime);
        String method = params.getSignmethod();
        String apiKey = params.getApikey();
        try {
            token = TokenUtil.assembleToken(version, res, expirationTime, method, apiKey);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return token;
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
            TokenParams params = new TokenParams();
            params.setApikey(apiKey);
            params.setUserid(userid);
            params.setEt(1);
            params.setVersion(Config.getAppVersion());
            params.setSignmethod(TokenUtil.SignatureMethod.MD5.name().toLowerCase());
            String token = handleToken(params);
            session.setAttribute("token", token);
            map.put("msg", userid);
            logger.info("token:" + token);
        } catch (NullPointerException e){
            e.printStackTrace();
        } catch (RuntimeException e){
            e.printStackTrace();
        }
        //String url = Config.getDomainName() + "/cmds?device_id=" + deviceid;
        //JSONObject re = HttpSendCenter.postStr(apiKey, url, command);
        //logger.info("return info = " + re.toString());

        return "index";
    }
    @RequestMapping("form_basic")
    public String basic(HttpServletRequest request) {
        //提交的数据http://url?msg=xxx&nonce=xxx&signature=xxx
        return "form_basic";
    }

    @RequestMapping(value = "ajaxToken",method = RequestMethod.POST)
    @ResponseBody
    public Msg ajaxToken(TokenParams params) {
        logger.info("ajaxToken:"+params.toString());
        Msg msg =  new Msg("error","操作失败!","");
        if(null != params){
            String token = handleToken(params);
            msg.setTitle("success");
            msg.setContent("已生成Token，请返回页面查看");
            msg.setEtraInfo(token);
        }
        logger.info("ret:"+msg.toString());
        return msg;
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
}
