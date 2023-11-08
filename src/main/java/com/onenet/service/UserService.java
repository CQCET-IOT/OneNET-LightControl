package com.onenet.service;

import com.onenet.config.Config;
import com.onenet.utils.FileUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    public final static int AUTH_FAIL=0;
    public final static int AUTH_SUCCESS=1;
    public final static int AUTH_NONEEXIST=-1;
    public static String USERKEYFILE = "userkey.txt";
    public static String USERDEVICEFILE = "userdevice.txt";
    public static Map<String, Object> User_Keys = new ConcurrentHashMap<String, Object>();
    public static Map<String, Object> User_Imeis = new ConcurrentHashMap<String, Object>();

    public UserService() {
        List<String> dirs = FileUtil.pullDirs(Config.getFileBase());
        if(dirs != null && dirs.size() > 0) {
            for (String userid : dirs) {
                //初始化
                getKey(userid);
                getImei(userid);
            }
        }
    }
    public String getKey(String userid){
        //先读缓存，否则取文件内容放入缓存
        Object key = User_Keys.get(userid);
        if(null == key){
            String path = Config.getFileBase() + "/" + userid + "/" + USERKEYFILE;
            StringBuffer sb = FileUtil.readByStream(path);
            if(null == sb){
                key = "";
            } else {
                String[] keys = sb.toString().split("\n");
                //一个用户目前只支持1一个key
                if (keys != null & keys.length >= 1) {
                    key = keys[0];
                    User_Keys.put(userid, key);
                }
            }
        }
        return (String)key;
    }
    public boolean setKey(String userid, String key){
        String path = Config.getFileBase() + "/" + userid + "/" + USERKEYFILE;
        boolean ret = FileUtil.writeByWriter(path, key, false,true);
        if(ret){
            User_Keys.put(userid,key);
        }
        return ret;
    }

    /**
     * 登录校验
     * @param userid 用户ID
     * @param key 密钥
     * @return int AUTH_NONEEXIST：用户不存在，AUTH_FAIL：用户密钥不一致，AUTH_SUCCESS：校验通过
     */
    public int auth(String userid, String key){
        int result = AUTH_FAIL;
        String filekey = getKey(userid);
        if (filekey == null || "".equals(filekey)) {
            result = AUTH_NONEEXIST;
        } else if(key.equals(filekey)){
            result = AUTH_SUCCESS;
        }
        return result;
    }
    /**
     * 绑定设备IMEI
     * @param userid 用户ID
     * @param imei IMEI
     * @return boolean true or false
     */
    public boolean setImei(String userid, String imei){
        int result = AUTH_FAIL;
        String path = Config.getFileBase() + "/" + userid + "/" + USERDEVICEFILE;
        boolean ret = FileUtil.writeByWriter(path, imei, false,true);
        if(ret){
            User_Imeis.put(userid,imei);
        }
        return ret;
    }
    public String getImei(String userid){
        //先读缓存，否则取文件内容放入缓存
        Object imei = User_Imeis.get(userid);
        if(null == imei) {
            String path = Config.getFileBase() + "/" + userid + "/" + USERDEVICEFILE;
            StringBuffer sb = FileUtil.readByStream(path);
            if(null == sb){
                imei = "";
            } else {
                String[] imeis = sb.toString().split("\n");
                //一个用户目前只支持1一个imei
                if (imeis != null & imeis.length >= 1) {
                    imei = imeis[0];
                    User_Imeis.put(userid, imei);
                }
            }
        }
        return (String)imei;
    }

    /**
     * 根据IMEI反查User id（只从缓存）
     * @param imei String
     * @return String userid
     */
    public String getUserIdByImei(String imei){
        String userid = "";
        if(User_Imeis.containsValue(imei)){
            Set<String> keys = User_Imeis.keySet();
            for(String uid : keys){
                String imei_mem = (String)User_Imeis.get(uid);
                if(imei_mem.equals(imei)){
                    userid = uid;
                }
            }
        }
        return userid;
    }
}
