package com.onenet.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 对某些接口进行放行
 */
public class SessionInterceptor implements HandlerInterceptor {
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
            throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
        //首页路径以及登录放行
        logger.info("URI:"+arg0.getRequestURI());
        if ("/login".equals(arg0.getRequestURI()) || "/userlogin".equals(arg0.getRequestURI())) {
            logger.info("Allow URI:"+arg0.getRequestURI());
            return true;
        }
        //重定向
        Object object = arg0.getSession().getAttribute("userid");
        if (null == object) {
            arg1.sendRedirect("/");
            logger.info("Deny URI:"+arg0.getRequestURI());
            return false;
        } else {
            logger.info("URL auth id:" + object.toString());
        }
        return true;
    }
}
