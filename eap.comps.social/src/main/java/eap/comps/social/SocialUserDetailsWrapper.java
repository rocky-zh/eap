package eap.comps.social;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.security.SocialUser;

import eap.base.IUserDetailsVOWrapper;
import eap.base.UserDetailsVO;

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
public class SocialUserDetailsWrapper extends SocialUser implements IUserDetailsVOWrapper {
	
	private UserDetailsVO userDetailsVO;
	
	public SocialUserDetailsWrapper(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
	}

	public SocialUserDetailsWrapper(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

	public void setUserDetailsVO(UserDetailsVO userDetailsVO) {
		this.userDetailsVO = userDetailsVO;
	}
	public UserDetailsVO getUserDetailsVO() {
		return userDetailsVO;
	}
}
