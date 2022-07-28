package com.onenet.exception;

import com.onenet.dto.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
public class OnenetExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(OnenetExceptionHandler.class);
    //通用异常全局处理：返回指定页面
    @ExceptionHandler(value = Exception.class)
    public Object exception(Exception e, HttpServletRequest request){
        log.error(OnenetExceptionHandler.getMessage(e));
        Msg msg = Msg.build().title("error").content(e.getLocalizedMessage()).etraInfo(e.toString());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error.html"); // 指定错误跳转页面 需要在templates里面新建 一个error.html
        modelAndView.addObject("msg", msg);
        modelAndView.addObject("url", request.getRequestURL());
        return modelAndView;
    }
    //指定异常全局处理1：返回指定页面
    @ExceptionHandler(value = OnenetException.class)
    public Object exception(OnenetException e, HttpServletRequest request){
        Msg msg = Msg.build().title("error").content(e.getError()).etraInfo(e.toString());
        log.error(OnenetExceptionHandler.getMessage(e));
        return msg;
    }
    public static String getMessage(Exception e){
        String str = "";
        try(StringWriter sw = new StringWriter();PrintWriter pw = new PrintWriter(sw)){
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
            str = sw.toString();
        } catch (IOException ex){
            log.error(ex.getMessage());
        }
        return str;
    }
}
