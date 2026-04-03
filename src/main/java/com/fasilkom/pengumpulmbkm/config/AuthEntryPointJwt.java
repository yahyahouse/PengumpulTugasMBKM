package com.fasilkom.pengumpulmbkm.config;

import com.fasilkom.pengumpulmbkm.model.response.BaseResponse;
import com.yahya.commonlogger.StructuredLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private final StructuredLogger structuredLogger;


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        structuredLogger.newLog()
                .withLogLevel(LogLevel.ERROR)
                .onFailure(authException, 0);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        BaseResponse baseResponse = new BaseResponse();
        BaseResponse.Transaction transaction = new BaseResponse.Transaction();
        transaction.setStatus("401");
        transaction.setMessage(authException.getMessage());
        baseResponse.setTransaction(transaction);

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), baseResponse);
    }
}
