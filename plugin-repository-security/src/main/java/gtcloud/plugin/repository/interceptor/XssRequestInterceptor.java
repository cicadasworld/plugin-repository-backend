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

import static gtcloud.plugin.repository.result.ResultStatus.XSS_DETECTED;
import static java.util.stream.Collectors.joining;

public class XssRequestInterceptor implements HandlerInterceptor {

    private static final String[] XSS_REGEX = {
        "onclick|onkeypress|onkeydown|onkeyup|onerror|onchange|onmouseover|onmouseout|onblur|onselect|onfocus",
        "<\\s*script\\b[^>]*>(.*?)<\\s*/script\\b[^>]*>",
        "script\\s+src\\s*=",
        "<\\s*script\\b[^>]*>",
        "<\\s*/script\\b[^>]*>",
        "javascript.*:",
    };

    private final ObjectMapper objectMapper;
    private final List<Pattern> xssValidationPatterns;

    public XssRequestInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.xssValidationPatterns = new ArrayList<>();
        for (String xss : XSS_REGEX) {
            Pattern pattern = Pattern.compile(xss, Pattern.CASE_INSENSITIVE);
            xssValidationPatterns.add(pattern);
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

        if (isXssSafe(queryString) && isXssSafe(pathVariable) && isXssSafe(requestBody)) {
            return true;
        } else {
            Result<Void> failure = Result.failure(XSS_DETECTED);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            String json = objectMapper.writeValueAsString(failure);
            response.getWriter().println(json);
            return false;
        }
    }

    private boolean isXssSafe(String stringToValidate) {
        if (stringToValidate == null || stringToValidate.trim().isEmpty()) {
            return true;
        }

        for (Pattern pattern : xssValidationPatterns) {
            if (pattern.matcher(stringToValidate).find()) {
                return false;
            }
        }

        return true;
    }
}
