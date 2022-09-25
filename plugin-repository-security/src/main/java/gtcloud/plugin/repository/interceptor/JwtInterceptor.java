package gtcloud.plugin.repository.interceptor;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gtcloud.plugin.repository.result.Result;
import gtcloud.plugin.repository.security.JwtService;
import gtcloud.plugin.repository.security.JwtSubject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static gtcloud.plugin.repository.result.ResultStatus.*;

public class JwtInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public JwtInterceptor(JwtService jwtService, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.startsWithIgnoreCase(authorizationHeader, "Bearer")) {
            Result<Void> failure = Result.failure(INVALID_TOKEN_ERROR);
            handleFailure(response, failure);
            return false;
        }
        StringBuilder builder = new StringBuilder(authorizationHeader);
        int pos = authorizationHeader.indexOf("Bearer");
        String bearerToken = builder.delete(pos, pos + "Bearer".length() + 1).toString();
        try {
            JwtSubject subject = jwtService.verifyToken(bearerToken);
            if (subject == null) {
                Result<Void> failure = Result.failure(UNAUTHORIZED_ERROR);
                handleFailure(response, failure);
                return false;
            }
            return true;
        } catch (TokenExpiredException e) {
            Result<Void> failure = Result.failure(EXPIRED_TOKEN_ERROR);
            handleFailure(response, failure);
            return false;
        } catch (JWTVerificationException e) {
            Result<Void> failure = Result.failure(INVALID_TOKEN_ERROR);
            handleFailure(response, failure);
            return false;
        }
    }

    private void handleFailure(HttpServletResponse response, Result<Void> failure) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        String json = objectMapper.writeValueAsString(failure);
        response.getWriter().println(json);
    }
}
