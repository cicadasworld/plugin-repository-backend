package gtcloud.plugin.repository.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class EntityTypeJsonTest {

    @Test
    public void createEntityTypesJson() throws JsonProcessingException {
        Map<String, String> fields = new HashMap<>();
        fields.put("name", "矢量切图插件2");
        fields.put("version", "1.0.0");

        Map<String, Map<String, String>> descriptor = new HashMap<>();
        descriptor.put("fields", fields);
        ObjectMapper objectMapper = new ObjectMapper();
        String resut = objectMapper.writeValueAsString(descriptor);
        System.out.println(resut);
    }
}
