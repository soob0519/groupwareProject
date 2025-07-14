package com.groupware.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import com.groupware.entity.EmpDto;

public class LoginInterceptor implements HandlerInterceptor {

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
		// 세션불러오기
        HttpSession session = request.getSession(false);

        // 세션이 없거나 "user" 속성 없으면 로그인 페이지로 리다이렉트
        if (session == null || session.getAttribute("user") == null) {
            
        	response.sendRedirect("/login");  // 로그인 페이지 경로
            
        	return false;
        }
        
        // 해당 uri 페이지 불러오기
        String uri = request.getRequestURI();

        // 관리자 페이지 접근 시 권한 체크
        if (uri.startsWith("/Inotice/Admin") || (uri.startsWith("/Gnotice/Admin"))) {
            
        	// 로그인한 사용자 정보 불러오기
        	EmpDto user = (EmpDto) session.getAttribute("user");

            // 인사팀장이 아닐 경우 응답 메시지 출력
        	if (!("팀장".equals(user.getPosition()) && "인사부".equals(user.getDept()))) {
                
            	// uri 화면 종류 (관리자가 아닌 사용자 들어올시 백지처리)
            	response.setContentType("text/html; charset=UTF-8");
            	
            	// 알림창 출력(script)
            	response.getWriter().write (
            	      " <html> "
            	    + " 	<head><meta charset='UTF-8'></head> "
            	    + " 	<body> " 
            	    + "			<script> " 
            	    + "				alert('접근 권한이 없습니다. 관리자(인사팀장)만 접근 가능합니다.'); " 
            	    + "				history.back(); " 
            	    + "			</script> " 
            	    + "		</body> "
            	    + "	</html> "
            	);
            	
            	return false;
            }
        }

        return true;
	}
}