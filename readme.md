# OneNET 数据推送服务器
（包含：1.推送验证功能及接收光照数据并自动发送开关API功能；2.控制设备自服务页面功能）

---

> 代码托管在 [https://github.com/CQCET-IOT/OneNET-LightControl](https://github.com/CQCET-IOT/OneNET-LightControl)

**基本功能**
中移物联网开放平台 提供了数据推送服务能力，需要自建应用服务作为推送接收端。
> A.推送验证功能（按中移物联网开放平台的验证算法实现，验证通过后平台才会数据推送）
> B.接收推送数据并筛选出光照数据功能（暂未提供网页展示）
> C.根据光照阈值发送光控开关API请求到中移物联网平台实现自动光照控制

**自服务页面功能**
基本功能的实现要支持不同的设备，故实现用户自服务页面配置设备信息（按用户账号密钥登陆）
> **注意**：本自服务页面需要用户按自己的账号密钥登陆后配置设备的IMEI信息（用于推送校验时自动生成Token）。但实际的应用中，不建议直接在页面上填写密钥做登录，因此这个项目仅仅作为培训演示使用。

**关注点**：通过WEB服务，提供用户更加易用的业务体验和随时随地控制设备的能力。
> 我们可以搭建WEB服务，实现网页访问，远程控制采用LwM2M协议的设备（这里只演示了通过应用API实现自动控制不同IMEI号设备的功能，实际设备多种协议都可以通过OneNET的协议封装，在应用层提供一致的API服务及配置网页）。



## 开发环境
1. jdk 1.8
2. maven 3.0+，构建工具
3. IntelliJ IDEA，IDE
4. git，版本维护工具

## 应用搭建 

![image](https://github.com/CQCET-IOT/OneNET-LightControl/assets/7342641/76982db4-bdfa-4f26-8a19-936644c82d37)

--使用 IDEA 导入本项目目录中 *pom.xml* 文件：
-- 服务端口参看配置文件
-- 服务映射路径修改，参看java代码文件


## 核心代码

### com.onenet.controller.LwM2MController 
该类实现基于注解 * @RestController * 的映射 * /receive * 路径，分别以注解 * @PostMapping * 和 * @GetMapping * 匹配不同请求方式对应的不同方法，来分别实现推送验证及接收光照数据功能。

```
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

        logger.info("url&token check: msg:{} nonce:{} signature:{}",msg,nonce,signature);
        logger.info("local token:{},aesKey:{}", token, aeskey);
        if (Util.checkToken(msg, nonce, signature, token)){
            return msg;
        } else {
            return "error";
        }
    }
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

        /************************************************
         *  解析数据推送请求，非加密模式。
         *  如果是明文模式使用以下代码
         **************************************************/
        /*************明文模式  start****************/
        Util.BodyObj obj = Util.resolveBody(body);
        if (obj != null){
            boolean dataRight = Util.checkSignature(obj, token);
            if (dataRight){
              //代码略
            } else {
                logger.info("data receive: signature error");
            }

        } else {
            logger.info("data receive: body empty error");
        }
        /*************明文模式  end****************/

        return "ok";
    }

```
### com.onenet.controller.MqttController 
该类实现基于网页服务的用户自服务管理功能，提供登录、登出、配置IMEI设备以及其他在线工具等管理功能。

```
		protected void addResourceHandlers(ResourceHandlerRegistry registry) {
			//配置静态资源（js/css/html等）路径
			registry.addResourceHandler("/static/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/static/");
			registry.addResourceHandler("/hplus/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/hplus/");
			super.addResourceHandlers(registry);
		}
```
### \MQTT_control\src\main\resources\templates\login.html
该文件实现WEB页面，供参数填写以及请求提交。

```
    private void initPage(Map<String, Object> map) {
        map.put("title", "我的物联网世界");
        map.put("mainmsg", "物联网云平台运用用户门户");
        map.put("secondmsg", "连接您的OneNET物联网应用项目和设备");

    }

    @RequestMapping("/")
    public String login(Map<String, Object> map) {
        initPage(map);
        return "login";
    }
```

## 编译打包

    略

## 部署

将生成的 *light_control-0.0.1-SNAPSHOT.jar* 拷贝到服务器上。
在服务器上安装 JDK1.8，然后运行（默认WEB服务端口8080）

```
java -jar light_control-0.0.1-SNAPSHOT.jar
```

这样，在 *www.xxxx.top* 服务器上，Web 程序监听 8080 端口

## 使用

> 浏览器访问 *www.xxxx.top* 地址，打开如下页面，输入OneNET物联网开放平台的用户ID及密钥作为登录账号密码（用于后台推送校验时生成Token）
![image](https://github.com/CQCET-IOT/OneNET-LightControl/assets/7342641/33855338-4d7f-491c-aca6-a562303231dc)

> 设备IMEI配置页面如下，点击绑定，用于从推送的数据中按IMEI号对应解析该设备的业务数据
![image](https://github.com/CQCET-IOT/OneNET-LightControl/assets/7342641/751973fd-c1b6-4c49-ad85-1a1219bd02c8)


