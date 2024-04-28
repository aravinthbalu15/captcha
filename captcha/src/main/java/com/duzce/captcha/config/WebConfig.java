package com.duzce.captcha.config;

import com.duzce.captcha.interceptor.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.duzce.captcha")
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public InternalResourceViewResolver jspViewResolver() { InternalResourceViewResolver viewResolver = new
            InternalResourceViewResolver(); viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/view/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setContentType("text/html;charset=UTF-8");
        return viewResolver;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        WebMvcConfigurer.super.configureMessageConverters(converters);
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        List<MediaType> stringMediaTypeList = new ArrayList<>();
        stringMediaTypeList.add(MediaType.TEXT_PLAIN);
        stringMediaTypeList.add(MediaType.TEXT_HTML);
        stringMediaTypeList.add(MediaType.APPLICATION_JSON);
        stringMediaTypeList.add(new MediaType("text", "javascript", Charset.forName("UTF-8")));
        stringConverter.setSupportedMediaTypes(stringMediaTypeList);
        converters.add(stringConverter);

        ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
        List<MediaType> byteArrayMediaTypeList = new ArrayList<MediaType>();
        byteArrayMediaTypeList.add(MediaType.IMAGE_JPEG);
        byteArrayMediaTypeList.add(MediaType.IMAGE_PNG);
        byteArrayMediaTypeList.add(MediaType.APPLICATION_OCTET_STREAM);
        arrayHttpMessageConverter.setSupportedMediaTypes(byteArrayMediaTypeList);
        converters.add(arrayHttpMessageConverter);
    }
}