package com.onenet.api.online;

import com.onenet.entity.CommonEntity;
import com.onenet.utils.HttpSendCenter;
import okhttp3.Callback;
import org.json.JSONObject;

/**
 * Created by zhuocongbin
 * date 2018/3/16
 * apiKey: the product of api-key which can be found on OneNET
 */
public class CreateDeviceOpe extends BasicOpe{
    public CreateDeviceOpe(String apiKey) {
        super(apiKey);
    }
    @Override
    public JSONObject operation(CommonEntity commonEntity, JSONObject body) {
        return HttpSendCenter.post(this.apiKey, commonEntity.toUrl(), body);
    }
    @Override
    public void operation(CommonEntity commonEntity, JSONObject body, Callback callback) {
        HttpSendCenter.postAsync(this.apiKey, commonEntity.toUrl(), body, callback);
    }

}
