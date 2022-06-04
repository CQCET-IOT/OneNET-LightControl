package com.onenet.service;

import com.onenet.config.Config;
import com.onenet.utils.FileUtil;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public final static int AUTH_FAIL=0;
    public final static int AUTH_SUCCESS=1;
    public final static int AUTH_NONEEXIST=-1;
    public static String USERKEYFILE = "userkey.txt";
    public String getKey(String userid){
        String path = Config.getFileBase() + "/" + userid + "/" + USERKEYFILE;
        String[] keys = FileUtil.readByStream(path).toString().split("\n");
        //一个用户目前只支持1一个key
        String key = "";
        if (keys != null&keys.length>=1) {
            key = keys[0];
        }
        return key;
    }
    public boolean setKey(String userid, String key){
        String path = Config.getFileBase() + "/" + userid + "/" + USERKEYFILE;
        boolean ret = FileUtil.writeByWriter(path, key, false,true);
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

}
