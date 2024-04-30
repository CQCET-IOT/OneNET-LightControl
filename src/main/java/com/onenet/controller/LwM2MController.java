package com.onenet.controller;

import com.example.iot.datapush.Util;
import com.onenet.domain.Light;
import com.onenet.dto.TokenParams;
import com.onenet.exception.OnenetExceptionHandler;
import com.onenet.service.UserService;
import com.onenet.utils.TokenUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
public class LwM2MController {
    @Autowired
    Light light;
    @Autowired
    UserService userService;

    @Value("${http-push.token}")
    private String token;
    @Value("${http-push.aesKey}")
    private String aeskey;

    private static Logger logger = LoggerFactory.getLogger(LwM2MController.class);
    /**
     * 功能描述：第三方平台数据接收。<p>
     * <ul>注:
     *   <li>1.OneNet平台为了保证数据不丢失，有重发机制，如果重复数据对业务有影响，数据接收端需要对重复数据进行排除重复处理。</li>
     *   <li>2.OneNet每一次post数据请求后，等待客户端的响应都设有时限，在规定时限内没有收到响应会认为发送失败。
     *   接收程序接收到数据时，尽量先缓存起来，再做业务逻辑处理。</li>
     * </ul>
     * @param body 数据消息
     * @return 任意字符串。OneNet平台接收到http 200的响应，才会认为数据推送成功，否则会重发。
     */
    @PostMapping("/receive")
    public String receive(@RequestBody String body) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        logger.info("data receive:  body String --- " +body);
        //{"msg":"{\"dev_name\":\"NB_12208\",\"at\":1698317863217,\"imei\":\"777957800456909\",\"pid\":\"AjIrTSO3B7\",\"type\":1,\"ds_id\":\"3301_0_5700\",\"value\":200.0}","signature":"xzcBtBqzfH23a+8xRptJJA==","time":1698317863225,"id":"7eac9202f8c74bf389ac5c42c16f946d","nonce":"ZaTkjA8d"}
        /************************************************
         *  解析数据推送请求，非加密模式。
         *  如果是明文模式使用以下代码
         **************************************************/
        /*************明文模式  start****************/
        Util.BodyObj obj = Util.resolveBody(body);
        if (obj != null){
            boolean dataRight = Util.checkSignature(obj, token);
            if (dataRight){

                try {
                    JSONObject object = new JSONObject(obj.toString());
                    JSONObject illumi = getIlluminance(object);
                    if (illumi != null) {
                        logger.info("data receive: " + obj.toString());
                        //匹配IMEI，找到userid，计算token
                        String imei = getIMEI(illumi);
                        String userid = userService.getUserIdByImei(imei);
                        String key = userService.getKey(userid);
                        //logger.info("userid:"+ userid + ",imei:" + imei + ",key:" +key);
                        TokenParams params = new TokenParams();
                        params.setApikey(key);
                        params.setUserid(userid);
                        params.setSignmethod("md5");
                        params.setVersion("2022-05-01");
                        String token = handleToken(params);

                        /* 解析光照度，根据光照度的值调用LED控制API */
                        light.setImei(imei);
                        light.setToken(token);
                        float value = illumi.getFloat("value");
                        float max = light.getThresholdMax();
                        float min = light.getThresholdMin();
                        logger.info("userid:"+ userid + ",imei:" + imei + ",illuminance value: " + value + ", max limit:"+ max + ", min limit:"+ min);
                        if (value > light.getThresholdMax()) {
                            // 调用写资源API关闭LED灯
                            light.switchLight(false);
                        } else if (value < light.getThresholdMin()) {
                            // 调用写资源API打开LED灯
                            light.switchLight(true);
                        }
                    } else {
                        logger.info("receiving NOT illuminace msg, ignore it.");
                    }
                }
                catch (Exception ex) {
                    logger.error("JSON parameter required:"+ex.getMessage());
                }
            } else {
                logger.info("data receive: signature error");
            }

        } else {
            logger.info("data receive: body empty error");
        }
        /*************明文模式  end****************/


        /********************************************************
         *  解析数据推送请求，加密模式
         *
         *  如果是加密模式使用以下代码
         ********************************************************/
        /*************加密模式  start****************/
//        Util.BodyObj obj1 = Util.resolveBody(body);
//        if (obj1 != null){
//            boolean dataRight1 = Util.checkSignature(obj1, token);
//            if (dataRight1){
//                String msg = Util.decryptMsg(obj1.getMsg(), aeskey.getBytes());
//                logger.info("data receive: content" + msg);
//            }else {
//                logger.info("data receive:  signature error " );
//            }
//        }else {
//            logger.info("data receive: body empty error" );
//        }
        /*************加密模式  end****************/
        return "ok";
    }

    /**
     * 功能说明： URL&Token验证接口。如果验证成功返回msg的值，否则返回其他值。
     * @param msg 验证消息
     * @param nonce 随机串
     * @param signature 签名
     * @return msg值
     */
    @GetMapping("/receive")
    public String check(@RequestParam(value = "msg") String msg,
                        @RequestParam(value = "nonce") String nonce,
                        @RequestParam(value = "signature") String signature) throws UnsupportedEncodingException {
        // { "msg":{"dev_name":"NB_12208","at":1698225805722,"imei":"777957800456909","pid":"AjIrTSO3B7","type":1,"ds_id":"3301_0_5700","value":117.5}, "nonce":"VKUfMrzX", "signature":"uMby3KFSuVZf6LOsQJwIlQ=="}

        logger.info("url&token check: msg:{} nonce:{} signature:{}",msg,nonce,signature);
        logger.info("local token:{},aesKey:{}", token, aeskey);
        if (Util.checkToken(msg, nonce, signature, token)){
            return msg;
        } else {
            return "error";
        }
    }

    /**
     * 从推送消息中获取IMEI号
     * @param object
     * @return
     */
    public String getIMEI(JSONObject object) {
        try {
            if (object != null) {
                String imei = object.getString("imei");
                return imei;
            }
        }
        catch (Exception ex) {
        }
        return null;
    }

    /**
     * 从推送消息中获取光照对象
     * @param object
     * @return
     */
    public JSONObject getIlluminance(JSONObject object) {
        try {
            JSONObject msg = object.getJSONObject("msg");
            if (msg != null) {
                //适配http全局数据推送情况，直接判断  \"ds_id\":\"3301_0_5700   ，取值 \"value\":129.166672
                if(!msg.isNull("ds_id")){
                    String ds_id = msg.getString("ds_id");
                    if (ds_id != null && "3301_0_5700".equals(ds_id)) {
                        return msg;
                    }
                } else {
                    //适配单独的规则引擎+推送资源的情况（也是http推送，但坑爹的是post报文不同） \"datastream\":\"3301_0_5700    \"body\":129.166672
                    String ds_id = msg.getJSONObject("appProperty").getString("datastream");
                    if (ds_id != null && "3301_0_5700".equals(ds_id)) {
                        float value = msg.getFloat("body");
                        msg.put("value", value);
                        return msg;
                    }
                }
            }
        }
        catch (Exception ex) {
        }

        return null;
    }
    private String handleToken(TokenParams params) {
        String token = "";
        String res = "userid/" + params.getUserid();
        String version = params.getVersion();
        String expirationTime = System.currentTimeMillis() / 1000 + params.getEt() * 24 * 60 * 60 + "";
        //logger.info("Token expiration time:" + expirationTime);
        String method = params.getSignmethod();
        String apiKey = params.getApikey();
        try {
            token = TokenUtil.assembleToken(version, res, expirationTime, method, apiKey);
            //logger.info("Token:" + token);
        } catch (UnsupportedEncodingException e) {
            OnenetExceptionHandler.getMessage(e);
        } catch (NoSuchAlgorithmException e) {
            OnenetExceptionHandler.getMessage(e);
        } catch (InvalidKeyException e) {
            OnenetExceptionHandler.getMessage(e);
        }
        return token;
    }}
