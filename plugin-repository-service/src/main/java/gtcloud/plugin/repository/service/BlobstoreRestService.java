package gtcloud.plugin.repository.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gtcloud.plugin.repository.domain.entity.Attachment;
import gtcloud.plugin.repository.exception.*;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BlobstoreRestService {

    private static final Logger LOG = LoggerFactory.getLogger(BlobstoreRestService.class);

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType BLOB = MediaType.parse("application/octet-stream");

    private final Environment environment;

    private final OkHttpClient httpClient;

    private final ObjectMapper objectMapper;

    @Autowired
    public BlobstoreRestService(Environment environment, OkHttpClient httpClient, ObjectMapper objectMapper) {
        this.environment = environment;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * 创建BlobEntity
     * @param jsonText 实体描述
     * @param domainCode 领域码
     * @param majorCode 主分类码
     * @param minorCode 子分类码，可以为空
     * @param id 实体id，可以不提供，不提供则自动生成
     * @return 实体id
     * @throws Exception 异常
     */
    public String uploadBlobEntity(String jsonText,
                                   String domainCode,
                                   String majorCode,
                                   String minorCode,
                                   String id) throws Exception {
        String property = environment.getProperty("blobstore.url");
        String blobstoreRestBaseURL = (property != null) ? property : "http://127.0.0.1:39231";
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(String.format("%s/blobstore/v1/entity?domaincode=%s&majorcode=%s",
                blobstoreRestBaseURL, domainCode, majorCode));
        if (!StringUtils.isEmpty(minorCode)) {
            urlBuilder.append(String.format("&minorcode=%s", minorCode));
        }
        if (!StringUtils.isEmpty(id)) {
            urlBuilder.append(String.format("&id=%s", id));
        }
        String url = urlBuilder.toString();

        RequestBody partBody = RequestBody.create(JSON, jsonText.getBytes(StandardCharsets.UTF_8));
        MultipartBody multipartBody = new MultipartBody.Builder()
                .addFormDataPart("blobentity.mf.v1", null, partBody)
                .setType(MultipartBody.FORM)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new AddPluginFailedException();
            }
            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            return jsonNode.get("retdata").get("identifier").get("id").asText();
        }
    }

    /**
     * 上传指定BlobEntity关联的文件
     * @param entityId 实体id
     * @param fileName 文件名
     * @param file 要上传的文件
     * @throws Exception 异常
     */
    public void uploadBlobEntityFile(String entityId, String fileName, File file) throws Exception {
        String property = environment.getProperty("blobstore.url");
        String blobstoreRestBaseURL = (property != null) ? property : "http://127.0.0.1:39231";
        String url = String.format("%s/blobstore/v1/entity/%s/files", blobstoreRestBaseURL, entityId);
        RequestBody partBody = RequestBody.create(BLOB, file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .addFormDataPart(fileName, file.getAbsolutePath(), partBody)
                .setType(MultipartBody.FORM)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new UploadPluginFailedException();
            }
            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            int retcode = jsonNode.get("retcode").asInt();
            if (retcode != 0) {
                LOG.error("Upload plugin failed");
                throw new UploadPluginFailedException();
            }
        }
    }

    public List<String> listBlobEntityIds(String domainCode, String majorCode) throws Exception {
        return listBlobEntityIds(domainCode, majorCode, null);
    }

    /**
     * 按（领域码、主分类码）列出所有BlobEntity的id
     * @param domainCode 领域码
     * @param majorCode 主分类码
     * @param minorCode 子分类码
     * @return 实体id列表
     * @throws Exception 异常
     */
    public List<String> listBlobEntityIds(String domainCode, String majorCode, String minorCode) throws Exception {
        String property = environment.getProperty("blobstore.url");
        String blobstoreRestBaseURL = (property != null) ? property : "http://127.0.0.1:39231";
        String url;
        if (minorCode == null) {
            url = String.format("%s/blobstore/v1/entity/ids/%s/%s",
                    blobstoreRestBaseURL, domainCode, majorCode);
        } else {
            url = String.format("%s/blobstore/v1/entity/ids/%s/%s/%s",
                    blobstoreRestBaseURL, domainCode, majorCode, minorCode);
        }
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String bodyString = response.body().string();
                JsonNode jsonNode = objectMapper.readTree(bodyString);
                JsonNode retdata = jsonNode.get("retdata");
                List<String> ids = new ArrayList<>();
                if (retdata.isArray()) {
                    for (JsonNode item : retdata) {
                        ids.add(item.asText());
                    }
                }
                return ids;
            }
        }
        return Collections.emptyList();
    }

    /**
     * 列举指定entityID中包含的Content列表
     * @param entityId 实体id
     * @return 实体名列表
     * @throws Exception 异常
     */
    public List<String> listBlobEntityFile(String entityId) throws Exception {
        String property = environment.getProperty("blobstore.url");
        String blobstoreRestBaseURL = (property != null) ? property : "http://127.0.0.1:39231";
        String url = String.format("%s/blobstore/v1/entity/content/%s/list", blobstoreRestBaseURL, entityId);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String bodyString = response.body().string();
                JsonNode jsonNode = objectMapper.readTree(bodyString);
                JsonNode retdata = jsonNode.get("retdata");
                List<String> fileNames = new ArrayList<>();
                if (retdata.isArray()) {
                    for (JsonNode item : retdata) {
                        fileNames.add(item.asText());
                    }
                }
                return fileNames;
            }
        }
        return Collections.emptyList();
    }

    /**
     * 下载指定id的文件
     * @param entityId 实体id
     * @throws Exception 异常
     * @return 附件
     */
    public Attachment downloadBlobEntityFile(String entityId) throws Exception {
        String property = environment.getProperty("blobstore.url");
        String blobstoreRestBaseURL = (property != null) ? property : "http://127.0.0.1:39231";
        String url = String.format("%s/blobstore/v1/entity/content/%s", blobstoreRestBaseURL, entityId);
        Request request = new Request.Builder()
                .url(url)
                .build();

        Attachment attachment = new Attachment();
        String tmpdir = System.getProperty("java.io.tmpdir");
        File file = new File(tmpdir, entityId + ".tmp");
        attachment.setFile(file);

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new DownloadPluginFailedException();
            }
            attachment.setContentDisposition(response.header("Content-Disposition"));
            try (OutputStream outputStream = new FileOutputStream(file);
                 BufferedInputStream input = new BufferedInputStream(response.body().byteStream())) {
                final byte[] buffer = new byte[8192];
                int bytes;
                while ((bytes = input.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytes);
                }
            }
        }
        return attachment;
    }

    /**
     * 按Id更新实体
     * @param entityId 实体Id
     * @param jsonText JSON文本
     * @throws Exception 异常
     */
    public void updateBlobEntityById(String entityId, String jsonText) throws Exception {
        String property = environment.getProperty("blobstore.url");
        String blobstoreRestBaseURL = (property != null) ? property : "http://127.0.0.1:39231";
        String url = String.format("%s/blobstore/v1/entity/manifest/%s", blobstoreRestBaseURL, entityId);

        RequestBody requestBody = RequestBody.create(JSON, jsonText);

        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new ModifyPluginFailedException();
            }
            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            int retcode = jsonNode.get("retcode").asInt();
            if (retcode != 0) {
                LOG.error("Upload plugin failed");
                throw new UploadPluginFailedException();
            }
        }
    }

    /**
     * 删除指定id的BlobEntity
     * @param entityId 实体id
     * @throws Exception 异常
     */
    public void removeBlobEntityById(String entityId) throws Exception {
        String property = environment.getProperty("blobstore.url");
        String blobstoreRestBaseURL = (property != null) ? property : "http://127.0.0.1:39231";
        String url = String.format("%s/blobstore/v1/entity/%s", blobstoreRestBaseURL, entityId);

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new DeleteBlobEntityFailedException();
            }
            String bodyString = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(bodyString);
            int retcode = jsonNode.get("retcode").asInt();
            if (retcode != 0) {
                LOG.error("Delete plugin failed");
                throw new DeletePluginFailedException();
            }
        }
    }

    /**
     * 删除BlobEntity关联的文件
     * @param entityId 实体Id
     * @param fileName 实体文件名
     * @throws Exception 异常
     */
    public void removeBlobEntityFile(String entityId, String fileName) throws Exception {
        String property = environment.getProperty("blobstore.url");
        String blobstoreRestBaseURL = (property != null) ? property : "http://127.0.0.1:39231";
        String url = String.format("%s/blobstore/v1/entity/%s/%s", blobstoreRestBaseURL, entityId, fileName);

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String bodyString = response.body().string();
                JsonNode jsonNode = objectMapper.readTree(bodyString);
                int retcode = jsonNode.get("retcode").asInt();
                if (retcode != 0) {
                    LOG.error("Delete plugin file failed");
                    throw new DeletePluginFailedException();
                }
            }
        }
    }

    /**
     * 判断BlobEntity是否存在
     * @param entityId 实体Id
     * @return 是否存在
     * @throws Exception 异常
     */
    public boolean exists(String entityId) throws Exception {
        String property = environment.getProperty("blobstore.url");
        String blobstoreRestBaseURL = (property != null) ? property : "http://127.0.0.1:39231";
        String url = String.format("%s/blobstore/v1/entity/manifest/%s", blobstoreRestBaseURL, entityId);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String bodyString = response.body().string();
                JsonNode jsonNode = objectMapper.readTree(bodyString);
                int retcode = jsonNode.get("retcode").asInt();
                if (retcode == 0) {
                    return true;
                }
            }
            return false;
        }
    }
}
