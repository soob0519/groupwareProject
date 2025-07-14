package com.groupware.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer  {

	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        
		// 세션미처리 접근 방지 및 감지
		registry.addInterceptor(new LoginInterceptor())     
    	
		// 로그인 처리 안되면 무조건 감지해야 할 웹 경로들
    	.addPathPatterns("/Inotice/**")
        .addPathPatterns("/Gnotice/**")
            
        // 세션감지 제외 파일들
        .excludePathPatterns("/login/**", "/css/**", "/js/**");
    }
}
