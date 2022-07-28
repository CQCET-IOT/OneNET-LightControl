package com.onenet.utils;

import com.onenet.exception.OnenetExceptionHandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 一、BufferedReader类 public class BufferedReader extends Reader
 * 从字符输入流中读取文本，缓冲各个字符，从而实现字符、数组和行的高效读取。 可以指定缓冲区的大小，或者可使用默认的大小。大多数情况下，默认值足够大。
 * 通常，Reader 所作的每个读取请求都会导致对底层字符或字节流进行相应的读取请求。因此，建议用 BufferedReader包装所有其 read()
 * 操作可能开销很高的 Reader(如 FileReader和 InputStreamReader)。
 * BufferedReader流能够读取文本行,通过向BufferedReader传递一个Reader对象,来创建一个BufferedReader对象,之所以这样做是因为FileReader没有提供读取文本行的功能.
 * 二、InputStreamReader类
 * InputStreamReader 将字节流转换为字符流。是字节流通向字符流的桥梁。如果不指定字符集编码，该解码过程将使用平台默认的字符编码，如：GBK。
 * 构造方法：
 * InputStreamReader isr = new InputStreamReader(InputStream in);//构造一个默认编码集的InputStreamReader类
 * InputStreamReader isr = new InputStreamReader(InputStream in,String charsetName);//构造一个指定编码集的InputStreamReader类。
 * 参数 in对象通过 InputStream in = System.in;获得。//读取键盘上的数据。
 * 或者 InputStream in = new FileInputStream(String fileName);//读取文件中的数据。可以看出FileInputStream 为InputStream的子类。
 * 主要方法：int read();//读取单个字符。 int read(char []cbuf);//将读取到的字符存到数组中。返回读取的字符数。
 * 三、FileWriter(少量文字) 和 BufferedWriter(大量文字)实现简单文件写操作
 *
 * @author
 */
public class FileUtil {
    private static String PATH = "/readme.txt";
    private static boolean debugMode = false;

    /**
     * @param args
     */
    public static void main(String[] args) {
// readSystemInputText();//读取键盘输入文字信息
// testBufferReader();
        String aaaaPath = "c:/home/hulk/aaaa.txt";
        String aaaPath = "c:/home/bar/aaa.txt";
        makeFile(aaaaPath);
        String text = readByStream(aaaaPath).toString();// 读取文件资源
        //String text = "AAAAAAAAAAAAaaaaaaaaaaaaaaaaBBBBBBBBBBBBBBBBbbbbbbbbbbbbb\n";
        writeByWriter(aaaPath, text, true, true);
    }

    public static void setDebugMode(boolean debugMode) {
        FileUtil.debugMode = debugMode;
    }

    /**
     * 向文件写入字符串.
     * 注意\n不一定在各种计算机上都能产生换行的效果:
     * @param filePath 目标文件
     * @param text 写入文字
     * @param append 是否追加写入
     * @param isline 写入后是否添加新行
     * @return The BufferedWriter object, null if throws Exception
     */
    public static boolean writeByWriter(String filePath, String text, boolean append, boolean isline) {
        makeFile(filePath);
        BufferedWriter out = null;
        boolean ret = false;
        try {
            out = new BufferedWriter(new FileWriter(filePath, append));
            out.write(text);
            if(isline){
                out.newLine();
            }
            ret = true;
        } catch (IOException e) {
            OnenetExceptionHandler.getMessage(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    OnenetExceptionHandler.getMessage(e);
                }
            }
        }
        return ret;
    }

    public static boolean makeFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            if (!file.isDirectory()) {
                File p = file.getParentFile();
                if (!p.exists()) {
                    makeDir(p.getAbsolutePath());
                }
                try {
                    return file.createNewFile();
                } catch (IOException e) {
                    OnenetExceptionHandler.getMessage(e);
                    return false;
                }
            } else {
                log("Not a file path: " + filePath);
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean makeDir(String dir) {
        File p = new File(dir);
        if (!p.exists()) {
            return p.mkdirs();
        } else {
            return true;
        }
    }

    /**
     * 通过Reader按行读取文件全部
     * @param filePath
     * @return StringBuffer 其中换行用“\n”间隔
     */
    public static StringBuffer readByReader(String filePath) {
        BufferedReader br = null;
        StringBuffer buffer = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            String line = null;
            buffer = new StringBuffer();
            while ((line = br.readLine()) != null) {
                buffer.append(line).append("\n");
                log(line);
            }
        } catch (IOException e) {
            OnenetExceptionHandler.getMessage(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    OnenetExceptionHandler.getMessage(e);
                }
            }
        }
        return buffer;
    }

    /**
     * 通过文件流按行读取文件全部
     * @param path 文件存放路径,；如果文件在项目根目录下，直接用文件名即可eg. "readme.txt"
     * @return StringBuffer 其中换行用“\n”间隔
     */
    public static StringBuffer readByStream(String path) {
        makeFile(path);
        StringBuffer buffer = null;
        BufferedReader bufread = null;
        BufferedInputStream bis = null;
        try {
            // 读取文件，并且以utf-8的形式写出去
            bis = getResourceInputStream(path);
            if (bis == null)
                return null;
            String read;
            bufread = new BufferedReader(new InputStreamReader((bis)));
            buffer = new StringBuffer();
            while ((read = bufread.readLine()) != null) {
                buffer.append(read).append("\n");
                log(read);
            }
            bufread.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if(bufread != null){
                try {
                    bufread.close();
                } catch (IOException e) {
                    OnenetExceptionHandler.getMessage(e);
                }
            }
            if(bis != null){
                try {
                    bis.close();
                } catch (IOException e) {
                    OnenetExceptionHandler.getMessage(e);
                }
            }
        }
        return buffer;
    }

    private static BufferedInputStream getResourceInputStream(String filePath) {
        try {
            return new BufferedInputStream(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            OnenetExceptionHandler.getMessage(e);
        }
        return null;
    }
    private static void log(String text) {
        if (debugMode) {
            System.out.println(text);
        }
    }

}