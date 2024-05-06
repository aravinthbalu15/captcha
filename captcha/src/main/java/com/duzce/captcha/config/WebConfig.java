package com.duzce.captcha.config;

import com.duzce.captcha.interceptor.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

        stringConverter.setSupportedMediaTypes(stringMediaTypeList);
        converters.add(stringConverter);

        ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
        List<MediaType> byteArrayMediaTypeList = new ArrayList<MediaType>();
        byteArrayMediaTypeList.add(MediaType.IMAGE_JPEG);
        byteArrayMediaTypeList.add(MediaType.IMAGE_PNG);

        arrayHttpMessageConverter.setSupportedMediaTypes(byteArrayMediaTypeList);
        converters.add(arrayHttpMessageConverter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestInterceptor()).addPathPatterns("/*");
        registry.addInterceptor(localeInterceptor()).addPathPatterns("/*");
    }

    @Bean
    public LocaleChangeInterceptor localeInterceptor(){
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Bean(name = "messageSource")
    public ReloadableResourceBundleMessageSource getMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(true);
        return messageSource;
    }

    @Bean
    public SessionLocaleResolver localeResolver(){
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.forLanguageTag("tr-TR"));
        return localeResolver;
    }

}