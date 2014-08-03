package eap.comps.webflow;


import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.TypeMismatchException;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ValueCoercionException;
import org.springframework.binding.mapping.MappingResults;
import org.springframework.binding.mapping.results.TargetAccessError;
import org.springframework.binding.message.Message;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.webflow.engine.RequestControlContext;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.mvc.view.ViewActionStateHolder;

import eap.EapContext;
import eap.comps.webevent.WebEvents;
import eap.comps.webevent.WebEventsHelper;
import eap.comps.webevent.WebFormVO;
import eap.util.BeanUtil;
import eap.util.MessageUtil;
import eap.util.StringUtil;

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
public class WebFlowHandlerInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) 
		throws Exception 
	{
		WebFormVO webFormVO = new WebFormVO();
		
		ViewActionStateHolder viewActionStateHolder = (ViewActionStateHolder) request.getAttribute(View.USER_EVENT_STATE_ATTRIBUTE);
		if (viewActionStateHolder != null) {
			MappingResults mappingResults = viewActionStateHolder.getMappingResults();
			if (mappingResults.getErrorResults().size() > 0) {
				Locale locale = EapContext.getLocale();
				String typeMismatchMsg = MessageUtil.getMessage("valid.typeMismatch", null, "数据类型错误", locale);
				
				TargetAccessError tae = null;
				Throwable cause = null;
				String field = null;
				for (Object er : mappingResults.getErrorResults()) {
					tae = (TargetAccessError) er;
					cause = tae.getErrorCause();
					field = tae.getMapping().getTargetExpression().getExpressionString();
					if (cause instanceof ValueCoercionException) {
						ValueCoercionException vce = (ValueCoercionException) cause;
						if (vce.getCause() instanceof TypeMismatchException) {
							webFormVO.addError(field, typeMismatchMsg);
						}
					}
//					else if (cause instanceof PropertyNotFoundException) {
						// not handle
//					}  
				}
			}
		}
		
		RequestControlContext requestControlContext = (RequestControlContext) request.getAttribute("flowRequestContext");
		if (requestControlContext != null) {
			// JSR 303
//			String bindingModelKey = ((Expression) requestControlContext.getCurrentState().getAttributes().get("model")).getExpressionString();
//			BindingModel bm = (BindingModel) request.getAttribute(BindingResult.MODEL_KEY_PREFIX + bindingModelKey);
//			if (bm != null) {
//				System.out.println(bm);
////				BindException be = (BindException) cause;
////				webEvents.setForm("xxxFormId", WebEventsHelper.toWebFormVO(request, be));
//			}
			
			String errorMessageKey = EapContext.getEnv().getProperty("webflow.validation.errorMessageKey", "wbf_validate_errors");
			Message[] msgs = (Message[]) requestControlContext.getViewScope().get(errorMessageKey);
			if (msgs != null && msgs.length > 0) {
				for (Message msg : msgs) {
					webFormVO.addError(msg.getSource().toString(), msg.getText());
				}
				requestControlContext.getViewScope().remove(errorMessageKey);
			}
			
			if (webFormVO.hasError()) {
				String bindingModelKey = ((Expression) requestControlContext.getCurrentState().getAttributes().get("model")).getExpressionString();
				Object modelObj = request.getAttribute(bindingModelKey);
				String formId =StringUtil.defaultIfBlank(BeanUtil.getPropertyAsString(modelObj, "formId"), bindingModelKey + "Form");
				
				WebEvents webEvents = WebEventsHelper.getWebEvents(request);
				webEvents.setForm(formId, webFormVO);
			}
		}
	}
}