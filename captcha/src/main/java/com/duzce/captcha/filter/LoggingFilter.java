package com.duzce.captcha.filter;

import com.duzce.captcha.dao.CaptchaRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Map;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        filterChain.doFilter(requestWrapper, responseWrapper);
        long timeTaken = System.currentTimeMillis() - startTime;



        String contentType = response.getContentType();
        String responseBody = null;
        if (contentType != null && !contentType.startsWith("image/") && !contentType.startsWith("text/html")) {
            responseBody = getStringValue(responseWrapper.getContentAsByteArray(),
                    response.getCharacterEncoding());
        }
        String requestBody = getStringValue(requestWrapper.getContentAsByteArray(),
                request.getCharacterEncoding());

        LOGGER.info(MessageFormat.format(
                "METHOD={0};" +
                        " REMOTE ADDRESS={1}; " +
                        " REQUEST URI={2};" +
                        " PARAMETERS={3}" +
                        " REQUEST PAYLOAD={4};" +
                        " RESPONSE CODE={5};" +
                        " RESPONSE={6};" +
                        " TIME TAKEN={7} milliseconds;",
                request.getMethod(), request.getRemoteAddr(),
                request.getRequestURI(),
                getParametersString(request.getParameterMap()),
                requestBody,
                response.getStatus(),
                responseBody,
                timeTaken));
        responseWrapper.copyBodyToResponse();
    }

    private String getParametersString(Map<String, String[]> parameterMap) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            sb.append(entry.getKey()).append("=");
            for (String value : entry.getValue()) {
                sb.append(value).append(",");
            }
            sb.deleteCharAt(sb.length() - 1); // Remove the trailing comma
            sb.append("; ");
        }
        return sb.toString();
    }

    private String getStringValue(byte[] contentAsByteArray, String characterEncoding) {
        try {
            return new String(contentAsByteArray, 0, contentAsByteArray.length, characterEncoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

}