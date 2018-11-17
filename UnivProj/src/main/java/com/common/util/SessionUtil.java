package com.common.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author: EMMA
 * @date: 2018/6/25
 * @description:
 * @since:
 */
public class SessionUtil {

    public static HttpSession getSession() {
        HttpSession session = null;
        try {
            session = getRequest().getSession();
        } catch (Exception e) {}
        return session;
    }

    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }
    public static Object getAttribute(String key) {
        return getSession().getAttribute( key );
    }
    public static void setAttribute(String key, Object value) {
        getSession().setAttribute( key, value );
    }
}
