package eap.comps.social;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.social.security.SocialAuthenticationToken;

import eap.EapContext;
import eap.base.UserDetailsVO;
import eap.util.PassportUtil;

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
public class SocialAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	private ConnectionFactoryLocator connectionFactoryLocator;
	private UsersConnectionRepository connectionRepository;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
		throws ServletException, IOException 
	{ // TODO
		Object principal = authentication.getPrincipal();
		SocialUserDetailsWrapper userDetailsWrapper = (principal instanceof SocialUserDetailsWrapper) ? (SocialUserDetailsWrapper) principal : null;
		UserDetailsVO userDetailsVO = userDetailsWrapper.getUserDetailsVO();
		if (userDetailsVO != null) {
			if (authentication instanceof SocialAuthenticationToken) {
				request.getSession().setAttribute(
					ProviderSignInAttempt.SESSION_ATTRIBUTE, 
					new ProviderSignInAttempt(((SocialAuthenticationToken) authentication).getConnection(), connectionFactoryLocator, connectionRepository)); // TODO
			}
			
			PassportUtil.sendRedirectToPassport(request, response, 
				EapContext.getEnv().getAppId(), userDetailsVO.getUserName(), "/"
			);
		} 
		else {
			super.onAuthenticationSuccess(request, response, authentication);
		}
	}

	public void setConnectionFactoryLocator(
			ConnectionFactoryLocator connectionFactoryLocator) {
		this.connectionFactoryLocator = connectionFactoryLocator;
	}

	public void setConnectionRepository(
			UsersConnectionRepository connectionRepository) {
		this.connectionRepository = connectionRepository;
	}
}