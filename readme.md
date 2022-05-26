# OneNET 网页控制MQTT设备

---

> 代码托管在 https://gitee.com/hexuyi/onenetapp

中移物联网 多协议接入能力，支持包括MQTT协议在内的多种协议接入。我们可以搭建WEB服务，实现网页访问，远程控制采用MQTT协议的设备（这里只演示了MQTT协议，实际多种协议都可以通过OneNET的协议封装，提供一致的网页服务。

**关注点**：通过WEB服务，将MQTT的技术细节屏蔽，提供用户更加易用的业务体验和随时随地控制设备的能力。

> **注意**：本程序因为没有固化 apiKey ，因此任何人只要有产品的apiKey，即可控制该产品下所有设备（通过设备的ID）实现远程控制能力。但实际的应用中，不建议直接在页面上填写产品apiKey，因此这个项目仅仅作为培训演示使用。


## 开发环境
1. jdk 1.8
2. maven 3.0+，构建工具
3. IntelliJ IDEA，IDE
4. git，版本维护工具

## 应用搭建 


![image_1dg88vpft152l1vtrb9817ca1g151t.png-82.8kB][1]


--使用 IDEA 导入本项目目录中 *pom.xml* 文件：
- 服务端口参看配置文件
- 服务Context修改，参看java代码文件


## 核心代码

### com.onenet.controller.MqttController 
该类实现url映射服务context方法，以及实现控制参数获取，向OneNET平台发送MQTT控制命令等逻辑。

```
    public String hello(Map<String, Object> map, HttpServletRequest request){
        //提交的数据
        String deviceid = request.getParameter("deviceid");
        String apiKey = request.getParameter("apikey");
        String command = request.getParameter("command");
        logger.info("deviceid = " + deviceid+" apiKey = " + apiKey+ " command = " + command);
        String url = Config.getDomainName() + "/cmds?device_id=" + deviceid;
        JSONObject re = HttpSendCenter.postStr(apiKey, url, command);
        logger.info("return info = " + re.toString());
        map.put("msg", re.toString());
        return "login";
    }
```
### com.onenet.MqttControlApplication 
该类实现SpringBoot服务发现和运行入口，以及配置页面静态资源。

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
		代码略
```

## 编译打包

    略

## 部署

将生成的 *-control-0.0.1-SNAPSHOT.jar* 拷贝到服务器上。

在服务器上安装 JDK1.8，然后运行：

```
java -jar MQTT-light-control-0.0.1-SNAPSHOT.jar --server.port=80
```

这样，在 *www.xxxx.top* 服务器上，Web 程序监听 80 端口

## 使用

浏览器访问 *www.xxxx.top* 地址，打开如下页面：

![image_1dg89i3ad2t0m54epg19qn3u.png-91.8kB][4]



  [1]: http://static.zybuluo.com/morgen/9kpytohh0repuei796jmlhzn/image_1dg88vpft152l1vtrb9817ca1g151t.png
  [2]: http://static.zybuluo.com/morgen/z5abplk3ajc7iq7wfxzzg4bk/image_1dg891hi2q2n1sp41d4q1u8f15kf2a.png
  [3]: http://static.zybuluo.com/morgen/3qygz5v6c73p7z9klvwfd12u/image_1dg82ljtcouq1e6d1lvs1q8u1o5s13.png
  [4]: http://static.zybuluo.com/morgen/418v7sykaby1okzcqxu2navn/image_1dg89i3ad2t0m54epg19qn3u.png
