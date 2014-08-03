package eap.comps.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import eap.EapContext;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * @作者 chiknin@gmail.com
 * @创建时间 
 * @版本 1.00
 * @修改记录
 * <pre>
 * 版本       修改人         修改时间         修改内容描述
 * ----------------------------------------
 * 
 * ----------------------------------------
 * </pre>
 */
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
	@Override
	protected void handle(HttpServletRequest request,HttpServletResponse response, Authentication authentication)
		throws IOException, ServletException 
	{
		super.handle(request, response, authentication);
		EapContext.publish("#logout.success", authentication);
	}
}
