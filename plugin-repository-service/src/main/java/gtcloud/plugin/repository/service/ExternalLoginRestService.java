package gtcloud.plugin.repository.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gtcloud.plugin.repository.result.ResultException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static gtcloud.plugin.repository.result.ResultStatus.MIFS_SERVER_ERROR;
import static gtcloud.plugin.repository.result.ResultStatus.USERNAME_OR_PASSWORD_ERROR;
import static java.util.Collections.emptyList;

@Service
public class ExternalLoginRestService {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalLoginRestService.class);

    private final Environment environment;

    private final OkHttpClient okHttpClient;

    private final ObjectMapper objectMapper;

    @Autowired
    public ExternalLoginRestService(Environment environment, OkHttpClient okHttpClient, ObjectMapper objectMapper) {
        this.environment = environment;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
    }

    public String getToken(String username, String password) throws ResultException {
        String BASE_MIFS_URL = environment.getProperty("mifs.url");
        String url = String.format("%s/MIFS/auth/token/login?name=%s&password=%s", BASE_MIFS_URL, username, password);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(null, ""))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ResultException(MIFS_SERVER_ERROR);
            }
            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            int code = jsonNode.get("code").asInt();
            if (code == 200) {
                JsonNode data = jsonNode.get("data");
                if (!data.isNull()) {
                    JsonNode token = data.get("access_token");
                    return token.asText();
                }
            }
            if (code == 400) {
                throw new ResultException(USERNAME_OR_PASSWORD_ERROR);
            }
            throw new ResultException(MIFS_SERVER_ERROR);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new ResultException(MIFS_SERVER_ERROR);
        }
    }

    public List<String> getRoles(String token) throws ResultException {
        String BASE_MIFS_URL = environment.getProperty("mifs.url");
        String url = String.format("%s/MIFS/auth/token/checkToken?token=%s", BASE_MIFS_URL, token);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(null, ""))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ResultException(MIFS_SERVER_ERROR);
            }
            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            int code = jsonNode.get("code").asInt();
            if (code == 200) {
                JsonNode data = jsonNode.get("data");
                JsonNode roles = data.get("role");
                if (roles.isArray()) {
                    List<String> roleNames = new ArrayList<>();
                    for (JsonNode role : roles) {
                        JsonNode name = role.get("NAME");
                        roleNames.add(name.asText());
                    }
                    return roleNames;
                }
            }
            return emptyList();
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new ResultException(MIFS_SERVER_ERROR);
        }
    }
}
