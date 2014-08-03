package org.springframework.security.web.access.expression;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

/**
 *
 * @author Luke Taylor
 * @since 3.0
 */
public class WebSecurityExpressionRoot extends SecurityExpressionRoot {
    //private FilterInvocation filterInvocation;
    /** Allows direct access to the request object */
    public final HttpServletRequest request;

    public WebSecurityExpressionRoot(Authentication a, FilterInvocation fi) {
        super(a);
        //this.filterInvocation = fi;
        this.request = fi.getRequest();
    }

    /**
     * Takes a specific IP address or a range using the IP/Netmask (e.g. 192.168.1.0/24 or 202.24.0.0/14).
     *
     * @param ipAddress the address or range of addresses from which the request must come.
     * @return true if the IP address of the current request is in the required range.
     */
    public boolean hasIpAddress(String ipAddress) {
//      return (new IpAddressMatcher(ipAddress).matches(request)); // delete by chiknin
    	
    	// add by chiknin
    	String remoteAddr = getRemoteAddr(request);
    	if (remoteAddr == null || remoteAddr.length() == 0) {
    		return false;
    	}
    	
    	return remoteAddr.matches(ipAddress);
    }
    
    private String getRemoteAddr(HttpServletRequest request) { // add by chiknin (@see eap.util.HttpUtil)
		String ip = request.getHeader("x-forwarded-for");
		if(StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		} 
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");  
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR"); 
		}
		if(StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		
		if (StringUtils.isNotBlank(ip)) {
			String[] ipArr = ip.split(" |,");
			if (ipArr != null && ipArr.length > 0) {
				return ipArr[ipArr.length - 1];
			}
		}
		
		return ip;
	}
}
