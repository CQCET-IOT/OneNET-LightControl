package com.onenet.api.online;

import com.onenet.entity.CommonEntity;
import com.onenet.utils.HttpSendCenter;
import okhttp3.Callback;
import org.json.JSONObject;

/**
 * Created by zhuocongbin
 * date 2018/3/15
 * apiKey: the product of api-key which can be found on OneNET
 */
public class ResourcesOpe extends BasicOpe{
    public ResourcesOpe(String apiKey) {
        super(apiKey);
    }
    @Override
    public JSONObject operation(CommonEntity commonEntity, JSONObject body) {
        return HttpSendCenter.get(apiKey, commonEntity.toUrl());
    }
    @Override
    public void operation(CommonEntity commonEntity, JSONObject body, Callback callback) {
        HttpSendCenter.getAsync(apiKey, commonEntity.toString(), callback);
    }

}
