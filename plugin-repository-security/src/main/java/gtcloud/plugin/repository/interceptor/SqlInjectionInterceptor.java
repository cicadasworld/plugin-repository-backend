package gtcloud.plugin.repository.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import gtcloud.plugin.repository.result.Result;
import gtcloud.plugin.repository.utils.CachedBodyHttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static gtcloud.plugin.repository.result.ResultStatus.SQL_INJECTION_DETECTED;
import static java.util.stream.Collectors.joining;

public class SqlInjectionInterceptor implements HandlerInterceptor {

    private static final String[] SQL_REGEX = {
        "(?i)(.*)(\\b)+SELECT(\\b)+\\s.*(\\b)+FROM(\\b)+\\s.*(.*)",
        "(?i)(.*)(\\b)+DROP(\\b)+\\s.*(.*)",
    };

    private final ObjectMapper objectMapper;
    private final List<Pattern> sqlValidationPatterns;

    public SqlInjectionInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.sqlValidationPatterns = new ArrayList<>();
        for (String sqlStatement : SQL_REGEX) {
            Pattern pattern = Pattern.compile(sqlStatement, Pattern.CASE_INSENSITIVE);
            sqlValidationPatterns.add(pattern);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
        String queryString = URLDecoder.decode(Optional.ofNullable(cachedRequest.getQueryString()).orElse(""), StandardCharsets.UTF_8.toString());
        String pathVariable = URLDecoder.decode(Optional.ofNullable(cachedRequest.getRequestURI()).orElse(""), StandardCharsets.UTF_8.toString());
        String requestBody = null;
        try (BufferedReader reader = cachedRequest.getReader()) {
            requestBody = reader.lines().collect(joining());
        }

        if (isSqlInjectionSafe(queryString) && isSqlInjectionSafe(pathVariable) && isSqlInjectionSafe(requestBody)) {
            return true;
        } else {
            Result<Void> failure = Result.failure(SQL_INJECTION_DETECTED);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            String json = objectMapper.writeValueAsString(failure);
            response.getWriter().println(json);
            return false;
        }
    }

    private boolean isSqlInjectionSafe(String stringToValidate) {
        if (stringToValidate == null || stringToValidate.trim().isEmpty()) {
            return true;
        }

        for (Pattern pattern : sqlValidationPatterns) {
            if (pattern.matcher(stringToValidate).find()) {
                return false;
            }
        }

        return true;
    }
}
