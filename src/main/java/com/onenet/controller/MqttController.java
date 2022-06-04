package com.onenet.controller;

import com.onenet.dto.Msg;
import com.onenet.dto.TokenParams;
import com.onenet.service.UserService;
import com.onenet.utils.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MqttController {
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    @Autowired
    UserService userService;

    @RequestMapping("/")
    public String login(Map<String, Object> map) {
        map.put("msg", "需要提供您的用户ID和密钥以访问您的设备");
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
    public String token(Map<String, Object> map) { return "token"; }

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
            logger.info("Token:"+token);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return token;
    }

    @RequestMapping("dologin")
    public String dologin(Map<String, Object> map, HttpServletRequest request) {
        //提交的数据
        try {
            String userid = request.getParameter("userid");
            String apiKey = request.getParameter("apikey");
            String command = request.getParameter("command");
            logger.info("userid = " + userid + " apiKey = " + apiKey + " command = " + command);
            HttpSession session = request.getSession();
            String confirm = (String)session.getAttribute("confirm");
            if(userid.equals(confirm)){
                session.removeAttribute("confirm");
                userService.setKey(userid,apiKey);
                session.setAttribute("userid", userid);
                session.setAttribute("apiKey", apiKey);
                map.put("msg", userid);
                return "index";
            }
            //从持久化中校验
            int res = userService.auth(userid,apiKey);
            if(UserService.AUTH_NONEEXIST== res){
                map.put("msg", "该ID还未使用本系统，再次提交可自动创建该ID。");
                session.setAttribute("confirm",userid);
            } else if(UserService.AUTH_FAIL == res){
                map.put("msg", "ID和密钥与第一次创建时不一致，再次提交可覆盖原数据。");
                session.setAttribute("confirm",userid);
            } else if(UserService.AUTH_SUCCESS == res) {
                session.setAttribute("userid", userid);
                session.setAttribute("apiKey", apiKey);
                map.put("msg", userid);
                return "index";
            }
        } catch (RuntimeException e){
            e.printStackTrace();
            map.put("msg","系统维护中，请稍后使用。");
        }
        //String url = Config.getDomainName() + "/cmds?device_id=" + deviceid;
        //JSONObject re = HttpSendCenter.postStr(apiKey, url, command);
        //logger.info("return info = " + re.toString());
        return "login";
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

    @RequestMapping(value = "/push")
    @ResponseBody
    public void getStreamDataImprove(HttpServletResponse httpServletResponse) {
        httpServletResponse.setContentType("text/event-stream");
        httpServletResponse.setCharacterEncoding("utf-8");
        PrintWriter pw = null;
        int i = 0;
        while (true) {
            try {
                pw=httpServletResponse.getWriter();
                Thread.sleep(60000L);
                String s = "data:Testing 1,2,3------- "+new Date() +"\n\n";
                System.out.println("执行了while循环"+(++i)+"次");
                pw.write(s);
                if(pw.checkError()) {
                    System.out.println("客户端断开连接");
                    pw.close();
                    return ;
                }
            } catch (IOException | InterruptedException e) {
                if(null != pw) {
                    pw.close();
                }
                e.printStackTrace();

            }
        }

    }

}
