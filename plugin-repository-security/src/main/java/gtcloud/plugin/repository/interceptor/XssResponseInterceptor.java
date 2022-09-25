package gtcloud.plugin.repository.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class XssResponseInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setHeader("X-XSS-Protection", "0");
        response.setHeader("X-Content-Type-Options", "nosniff");
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
