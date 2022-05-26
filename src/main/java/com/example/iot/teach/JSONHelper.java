package com.example.iot.teach;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.*;

public class JSONHelper {

    static Logger log = LoggerFactory.getLogger(JSONHelper.class);

   /* public static void main(String[] args) {
//        String s =ResolveJsonFileToString("node.json");
//        System.out.println("sss="+s);

//        Map map = (Map) ResolveJsonFileToObject("node.json");
//        System.out.println("map="+map.get("res"));
    }
*/


    /**
     *  将文件流转为json对象，文件存放路径与配置文件路径规范一致
     * @param
     * @return
     * @throws
     */
    public static Object ResolveJsonFileToObject(String filename){
        String str= ResolveJsonFileToString(filename);
        JSONObject jo = JSONObject.parseObject(str);
        return jo;
    }


    /**
     *  通过文件名获取获取json格式字符串，
     * @param filename 文件存放路径与配置文件路径规范一致
     * @return ResolveJsonFileToString
     * @throws
     */
    public static String ResolveJsonFileToString(String filename){

        BufferedReader br = null;
        String result = null;
        try {

//            br = new BufferedReader(new InputStreamReader(getInputStream(path)));
            br = new BufferedReader(new InputStreamReader(getResFileStream(filename),"UTF-8"));
            StringBuffer message=new StringBuffer();
            String line = null;
            while((line = br.readLine()) != null) {
                message.append(line);
            }
            if (br != null) {
                br.close();
            }
            String defaultString=message.toString();
            result=defaultString.replace("\r\n", "").replaceAll(" +", "");
            log.info("result={}",result);

        } catch (IOException e) {
            try {
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                InputStream in = classloader.getResourceAsStream(filename);
                br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                StringBuffer message=new StringBuffer();
                String line = null;
                while((line = br.readLine()) != null) {
                    message.append(line);
                }
                if (br != null) {
                    br.close();
                }
                if (in != null){
                    in.close();
                }
                String defaultString=message.toString();
                result=defaultString.replace("\r\n", "").replaceAll(" +", "");
                log.debug("for jar result={}",result);
            }catch (Exception e1){
                e1.printStackTrace();
            }

        }

        return result;
    }



    private static File getResFile(String filename) throws FileNotFoundException {
        File file = new File(filename);
        if (!file.exists()) { // 如果同级目录没有，则去config下面找
            log.debug("不在同级目录，进入config目录查找");
            file = new File("config/"+filename);
        }
        Resource resource = new FileSystemResource(file);
        if (!resource.exists()) { //config目录下还是找不到，那就直接用classpath下的
            log.debug("不在config目录，进入classpath目录查找");
            file = ResourceUtils.getFile("classpath:"+filename);
        }
        return file;
    }

    /**
     *  通过文件名获取classpath路径下的文件流
     * @param
     * @return
     * @throws
     */
    private static FileInputStream getResFileStream(String filename) throws FileNotFoundException {
        FileInputStream fin = null;
        File file = getResFile(filename);
        log.info("getResFile path={}",file);
        fin = new FileInputStream(file);
        return fin;
    }

}

