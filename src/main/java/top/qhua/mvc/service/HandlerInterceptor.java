package top.qhua.mvc.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 实现拦截器接口
 */
public interface HandlerInterceptor {

    /**
     * 在Controller method 之前执行
     */
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception{
        return true;
    }

    /**
     * 在Controller执行之后执行
     */
    default void afterHandler(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    }

}
