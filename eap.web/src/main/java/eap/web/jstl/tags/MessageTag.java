package eap.web.jstl.tags;

import org.springframework.context.MessageSource;

import eap.util.MessageUtil;

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
public class MessageTag extends org.springframework.web.servlet.tags.MessageTag {
	
	@Override
	protected MessageSource getMessageSource() {
		MessageSource messageSource = MessageUtil.getMessageSource();
		if (messageSource == null) {
			messageSource = super.getMessageSource();
		}
		
		return messageSource;
	}
}
