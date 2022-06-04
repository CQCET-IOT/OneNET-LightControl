package com.onenet.wrapper;

import com.github.cm.heclouds.onenet.studio.api.IotClient;
import com.github.cm.heclouds.onenet.studio.api.IotProfile;

import java.util.HashMap;
import java.util.Map;

public class SdkHelper {
    private static SdkHelper _instance;
    private Map<String, IotClient> clients = new HashMap();

    public IotClient initClient(String userid, String apikey){
        
        IotClient client = clients.get(userid);
        if (client == null) {
            IotProfile profile = new IotProfile();
            profile.userId(userid).accessKey(apikey);
            client = IotClient.create(profile);
            clients.put(userid, client);
        }
        return client;
    }
    public static synchronized SdkHelper getInstance(){
        if (_instance == null) {
            _instance = new SdkHelper();
        }
        return _instance;
    }
}
