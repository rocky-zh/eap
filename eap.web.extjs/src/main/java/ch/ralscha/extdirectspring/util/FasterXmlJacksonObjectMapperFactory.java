package ch.ralscha.extdirectspring.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.FactoryBean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;

import eap.util.DateUtil;

public class FasterXmlJacksonObjectMapperFactory implements FactoryBean<ObjectMapper> {
    
    private SimpleDateFormat fullDateSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public ObjectMapper getObject() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.setDateFormat(fullDateSdf);
        objectMapper.setSerializerProvider(new DefaultSerializerProviderImpl());
        
        return objectMapper;
    }
    
    @Override
    public Class<?> getObjectType() {
        return ObjectMapper.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
    
    public final static class DefaultSerializerProviderImpl extends DefaultSerializerProvider
    {
        private static final long serialVersionUID = 1L;

        public DefaultSerializerProviderImpl() { super(); }

        protected DefaultSerializerProviderImpl(SerializerProvider src,
                SerializationConfig config,SerializerFactory f) {
            super(src, config, f);
        }

        @Override
        public DefaultSerializerProviderImpl createInstance(SerializationConfig config,
                SerializerFactory jsf) {
            return new DefaultSerializerProviderImpl(this, config, jsf);
        }
        
        @Override
        public void defaultSerializeDateKey(Date date, JsonGenerator jgen)
                throws IOException, JsonProcessingException {
        	
        	System.out.println(date);
            
            if (isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)) {
                jgen.writeFieldName(String.valueOf(date.getTime()));
            } else {
//                jgen.writeFieldName(_dateFormat().format(date));
                jgen.writeFieldName(DateUtil.format(date));
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        
        FasterXmlJacksonObjectMapperFactory f = new FasterXmlJacksonObjectMapperFactory();
        ObjectMapper m = f.getObject();
        
        Object r = m.readValue("{\"a\":\"1\", \"b\": 2, \"c\": \"2010-11-10 00:00:00\"}", A.class);
        System.out.println(ToStringBuilder.reflectionToString(r));
        
        System.out.println(m.writeValueAsString(r));
    }
    
    static class A {
        private String a;
        private Integer b;
        private Date c;
        private Double d;
        private BigDecimal e;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public Integer getB() {
            return b;
        }

        public void setB(Integer b) {
            this.b = b;
        }

        public Date getC() {
            return c;
        }

        public void setC(Date c) {
            this.c = c;
        }

        public Double getD() {
            return d;
        }

        public void setD(Double d) {
            this.d = d;
        }

        public BigDecimal getE() {
            return e;
        }

        public void setE(BigDecimal e) {
            this.e = e;
        }
        
    }
}
