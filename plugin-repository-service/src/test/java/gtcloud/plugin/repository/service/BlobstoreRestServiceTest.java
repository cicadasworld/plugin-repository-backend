package gtcloud.plugin.repository.service;

import gtcloud.plugin.repository.domain.entity.Attachment;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BlobstoreRestServiceTest {

    @Autowired
    private BlobstoreRestService service;

    @Test
    public void uploadBlobEntity() throws Exception {
        String jsonText = "{\n" +
                "    \"fields\": {\n" +
                "        \"name\": \"test1\",\n" +
                "        \"description\": \"test2\"\n" +
                "    }\n" +
                "}";
        String id = service.uploadBlobEntity(jsonText, "repository", "plugin", "data", null);
        System.out.println(id);
    }

    @Test
    public void uploadBlobEntityFile() throws Exception {
        File file = new File("D:\\99-Temp\\开发环境要求.txt");
        service.uploadBlobEntityFile("369837e8131c4b5e8d88db935c56a1ba", "开发环境要求.txt", file);
    }

    @Test
    public void listBlobEntityIds() throws Exception {
        List<String> ids = service.listBlobEntityIds("repository", "plugin");
        System.out.println(ids);
    }

    @Test
    public void downloadBlobEntityFile() throws Exception {
        Attachment attachment = service.downloadBlobEntityFile("369837e8131c4b5e8d88db935c56a1ba");
        System.out.println(attachment.getContent());
    }

    @Test
    public void removeBlobEntityById() throws Exception {
        service.removeBlobEntityById("369837e8131c4b5e8d88db935c56a1ba");
    }
}
