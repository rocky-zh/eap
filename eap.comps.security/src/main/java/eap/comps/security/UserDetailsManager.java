package eap.comps.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

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
public class UserDetailsManager extends JdbcUserDetailsManager {
	
	@Override
	protected List<UserDetails> loadUsersByUsername(String username) {
		return getJdbcTemplate().query(this.getUsersByUsernameQuery(), new String[] {username}, new RowMapper<UserDetails>() {
			public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
				String userNum = rs.getString("user_num"); // 用户编号
				String userName = rs.getString("user_name"); // 用户名/登录账号
				String password = rs.getString("password"); // 登录密码
				String accountType = rs.getString("account_type"); // 账号类型
				String status = rs.getString("status"); // 账号状态
				
				UserDetailsVO userDetailsVO = new UserDetailsVO(); 
				userDetailsVO.setUserNum(userNum);
				userDetailsVO.setUserName(userName);
				userDetailsVO.setAccountType(accountType);
				userDetailsVO.setStatus(status);
				
				boolean enabled = true;
				boolean accountNonExpired = true;
				boolean accountNonLocked = !userDetailsVO.isAccountLocked();
				boolean credentialsNonExpired = true;
				UserDetailsWrapper userDetailsWrapper = new UserDetailsWrapper(
					userName, password, enabled, 
					accountNonExpired, credentialsNonExpired, accountNonLocked, 
					AuthorityUtils.NO_AUTHORITIES
				);
				userDetailsWrapper.setUserDetailsVO(userDetailsVO);
				
				return userDetailsWrapper;
			}
		});
	}
	
	@Override
	protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery, List<GrantedAuthority> combinedAuthorities) {
		String returnUsername = userFromUserQuery.getUsername();
		if (!this.isUsernameBasedPrimaryKey()) {
			returnUsername = username;
		}
		
		UserDetailsWrapper userDetailsWrapper = (UserDetailsWrapper) userFromUserQuery;
		UserDetailsWrapper newUserDetailsWrapper = new UserDetailsWrapper(returnUsername, userFromUserQuery.getPassword(), 
			userFromUserQuery.isEnabled(), userFromUserQuery.isAccountNonExpired(), 
			userFromUserQuery.isCredentialsNonExpired(), userFromUserQuery.isAccountNonLocked(), combinedAuthorities);
		newUserDetailsWrapper.setUserDetailsVO(userDetailsWrapper.getUserDetailsVO());
		
		List<String> roleCdList = new ArrayList<String>(combinedAuthorities.size());
		for (GrantedAuthority ga : combinedAuthorities) {
			roleCdList.add(ga.getAuthority());
		}
		Collections.sort(roleCdList); // TODO
		newUserDetailsWrapper.getUserDetailsVO().setRoleCdList(roleCdList);
		
		return newUserDetailsWrapper;
	}
}