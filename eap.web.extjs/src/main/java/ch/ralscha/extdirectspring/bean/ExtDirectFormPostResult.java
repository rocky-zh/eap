/**
 * Copyright 2010-2012 Ralph Schaer <ralphschaer@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.ralscha.extdirectspring.bean;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import eap.util.MessageUtil;

/**
 * Class representing the result of a FORM_POST method call.
 * 
 * @author Ralph Schaer
 */
public class ExtDirectFormPostResult {

    private final Map<String, Object> result = new HashMap<String, Object>();

    public ExtDirectFormPostResult() {
        setSuccess(true);
    }

    public ExtDirectFormPostResult(boolean success) {
        setSuccess(success);
    }

    public ExtDirectFormPostResult(BindingResult bindingResult) {
        addErrors(null, null, bindingResult);
    }

    public ExtDirectFormPostResult(BindingResult bindingResult, boolean success) {
        addErrors(null, null, bindingResult);
        setSuccess(success);
    }

    public ExtDirectFormPostResult(Locale locale, MessageSource messageSource, BindingResult bindingResult) {
        addErrors(locale, messageSource, bindingResult);
    }

    public ExtDirectFormPostResult(Locale locale, MessageSource messageSource, BindingResult bindingResult,
            boolean success) {
        addErrors(locale, messageSource, bindingResult);
        setSuccess(success);
    }

    private void addErrors(Locale locale, MessageSource messageSource, BindingResult bindingResult) {
        if (bindingResult != null && bindingResult.hasFieldErrors()) {
            Map<String, String> errorMap = new HashMap<String, String>();
            for (ObjectError objectError : bindingResult.getAllErrors()) {
                FieldError fieldError = (FieldError) objectError;
                String message = null;
                for (String code : fieldError.getCodes()) {
                    message = MessageUtil.getMessage(code, null, locale); // , new Object[] {(fieldError.getObjectName() +"." + fieldError.getField()), fieldError.getRejectedValue()}
                    if (StringUtils.isNotBlank(message)) {
                        break;
                    }
                }
                errorMap.put(fieldError.getField(), StringUtils.defaultIfBlank(message, fieldError.getDefaultMessage()));
            }
            if (errorMap.isEmpty()) {
                addResultProperty("success", true);
            } else {
                addResultProperty("errors", errorMap);
                addResultProperty("success", false);
            }
        } else {
            setSuccess(true);
        }
        
//        if (bindingResult != null && bindingResult.hasFieldErrors()) {
//            Map<String, List<String>> errorMap = new HashMap<String, List<String>>();
//            for (FieldError fieldError : bindingResult.getFieldErrors()) {
//                String message = fieldError.getDefaultMessage();
//                if (messageSource != null) {
//                    Locale loc = (locale != null ? locale : Locale.getDefault());
//                    message = messageSource.getMessage(fieldError.getCode(), fieldError.getArguments(), loc);
//                }
//                List<String> fieldErrors = errorMap.get(fieldError.getField());
//
//                if (fieldErrors == null) {
//                    fieldErrors = new ArrayList<String>();
//                    errorMap.put(fieldError.getField(), fieldErrors);
//                }
//
//                fieldErrors.add(message);
//            }
//            if (errorMap.isEmpty()) {
//                addResultProperty("success", true);
//            } else {
//                addResultProperty("errors", errorMap);
//                addResultProperty("success", false);
//            }
//        } else {
//            setSuccess(true);
//        }
    }

    public void addResultProperty(String key, Object value) {
        result.put(key, value);
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setSuccess(boolean flag) {
        result.put("success", flag);
    }

}
