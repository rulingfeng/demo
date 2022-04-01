package com.example.demo.login;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

//登录token验证，为验证通过则直接返回 没有权限
//这个针对单机版，
// 如果是微服务， 那么 需要把这些login包里面这些 拦截器、用户上下文、LoginConfiguration配置抽到一个公共的包， 供其他模块来引用
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if(StringUtils.isBlank(token)) {
            output(response,"未登录,请先登录");
            return false;
        }
        boolean verify = JWTUtils.verify(token);
        if(!verify){
            output(response,"token过期，重新登录");
        }
        String mobile = JWTUtils.getTokenStr(token, "mobile");

        Long userId = Long.valueOf(mobile);
        UserContextHolder.getInstance().setContext(userId);
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserContextHolder.getInstance().clear();
    }

    public void output(HttpServletResponse response,String str){
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        ServletOutputStream os = null;
        try {
            os = response.getOutputStream();
            os.write(str.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
