package gtcloud.plugin.repository.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gtcloud.plugin.repository.dao.CategoryDao;
import gtcloud.plugin.repository.dao.PluginDao;
import gtcloud.plugin.repository.domain.entity.Attachment;
import gtcloud.plugin.repository.domain.entity.Category;
import gtcloud.plugin.repository.domain.entity.Plugin;
import gtcloud.plugin.repository.domain.vo.QueryVo;
import gtcloud.plugin.repository.exception.*;
import gtcloud.plugin.repository.result.ResultException;
import gtcloud.plugin.repository.service.BlobstoreRestService;
import gtcloud.plugin.repository.service.PluginService;
import gtcloud.plugin.repository.utils.VersionComparator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gtcloud.plugin.repository.result.ResultStatus.*;

@Service
public class PluginServiceImpl implements PluginService {

    private static final Logger LOG = LoggerFactory.getLogger(PluginServiceImpl.class);

    private static final String domainCode = "repository";
    private static final String majorCode = "plugin";
    private static final String minorCode = "data";

    private final ObjectMapper objectMapper;
    private final BlobstoreRestService blobstoreRest;
    private final PluginDao pluginDao;
    private final CategoryDao categoryDao;

    @Autowired
    public PluginServiceImpl(ObjectMapper objectMapper,
                             BlobstoreRestService blobstoreRest,
                             PluginDao pluginDao,
                             CategoryDao categoryDao) {
        this.objectMapper = objectMapper;
        this.blobstoreRest = blobstoreRest;
        this.pluginDao = pluginDao;
        this.categoryDao = categoryDao;
    }

    @Override
    public String add(Plugin plugin) throws ResultException {
        validate(plugin);

        String name = plugin.getName();
        String version = plugin.getVersion();
        String os = plugin.getOs();
        String arch = plugin.getArch();
        if (pluginDao.existsByNameVersionOsArch(name, version, os, arch)) {
            throw new DuplicatePluginException();
        }

        plugin.setPublished(false);
        plugin.setDeleted(false);

        String jsonText;
        try {
            jsonText = this.createEntityTypesJson(plugin);
            LOG.debug("EntityTypes: {}", jsonText);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage());
            throw new AddPluginFailedException();
        }

        try {
            String pluginId = UUID.randomUUID().toString().replace("-", "");
            blobstoreRest.uploadBlobEntity(jsonText, domainCode, majorCode, minorCode, pluginId);
            plugin.setPluginId(pluginId);
            pluginDao.insert(plugin);
            return pluginId;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new AddPluginFailedException();
        }
    }

    @Override
    public void upload(String pluginId, String fileName, File file) throws ResultException {
        try {
            blobstoreRest.uploadBlobEntityFile(pluginId, fileName, file);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new UploadPluginFailedException();
        }
    }

    @Override
    public void modify(Plugin plugin) throws ResultException {
        validate(plugin);

        String pluginId = plugin.getPluginId();
        if (!pluginDao.existsById(pluginId)) {
            throw new PluginNotFoundException();
        }

        String name = plugin.getName();
        String version = plugin.getVersion();
        String os = plugin.getOs();
        String arch = plugin.getArch();
        QueryVo queryVo = new QueryVo();
        queryVo.setName(name);
        queryVo.setVersion(version);
        queryVo.setOs(os);
        queryVo.setArch(arch);
        List<Plugin> plugins = pluginDao.findExactByProperties(queryVo);
        if (!plugins.isEmpty()) {
            Plugin p = plugins.get(0);
            if (!pluginId.equals(p.getPluginId())) {
                throw new DuplicatePluginException();
            }
        }

        pluginDao.update(plugin);
        String jsonText = null;
        try {
            jsonText = this.createEntityTypesJson(plugin);
        } catch (JsonProcessingException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        LOG.debug("EntityTypes: {}", jsonText);

        try {
            if (jsonText != null) {
                blobstoreRest.updateBlobEntityById(pluginId, jsonText);
            } else {
                throw new ModifyPluginFailedException();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new ModifyPluginFailedException();
        }
    }

    @Override
    public void publish(String pluginId, Boolean published) throws ResultException {
        Plugin plugin = pluginDao.findById(pluginId).orElseThrow(PluginNotFoundException::new);
        plugin.setPublished(published);
        pluginDao.update(plugin);
    }

    @Override
    public Attachment download(String pluginId) throws ResultException {
        try {
            return blobstoreRest.downloadBlobEntityFile(pluginId);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new DownloadPluginFailedException();
        }
    }

    @Override
    public List<Plugin> search(
        String keyword,
        Boolean published,
        Boolean deleted,
        Integer page,
        Integer size
    ) {
        QueryVo queryVo = new QueryVo();
        queryVo.setPluginId(keyword);
        queryVo.setName(keyword);
        queryVo.setBuild(keyword);
        queryVo.setVersion(keyword);
        queryVo.setDescription(keyword);
        queryVo.setCompatibleVersion(keyword);
        queryVo.setOs(keyword);
        queryVo.setArch(keyword);
        queryVo.setDependency(keyword);

        // 1, 2, 3, ...
        String categoryIds = String.join(",", categoryDao.findByNameLike(keyword));
        queryVo.setCategory(categoryIds);

        queryVo.setPublished(published);
        queryVo.setDeleted(deleted);

        return pluginDao.findAllByPropertyPaginated(queryVo, page, size);
    }

    @Override
    public List<Plugin> exact(Map<String, String> parameters) throws ResultException {
        String name = parameters.get("name");
        String version = parameters.get("version");
        String os = parameters.get("os");
        String arch = parameters.get("arch");
//        if (name == null) {
//            throw new ResultException(PLUGIN_NAME_EMPTY_ERROR);
//        }
//        if (version == null) {
//            throw new ResultException(PLUGIN_VERSION_EMPTY_ERROR);
//        }
//        if (os == null) {
//            throw new ResultException(PLUGIN_OS_EMPTY_ERROR);
//        }
//        if (arch == null) {
//            throw new ResultException(PLUGIN_ARCH_EMPTY_ERROR);
//        }

        QueryVo queryVo = new QueryVo();
        queryVo.setName(name);
        queryVo.setVersion(version);
        queryVo.setOs(os);
        queryVo.setArch(arch);
        queryVo.setPublished(true);
        queryVo.setDeleted(false);
        List<Plugin> plugins = pluginDao.findExactByProperties(queryVo);
        if (plugins.isEmpty()) {
            throw new PluginNotFoundException();
        }
        return plugins;
    }

    @Override
    public List<Plugin> listAll(
        String categoryId,
        Boolean published,
        Boolean deleted,
        Integer page,
        Integer size
    ) {
        if (page == null || size == null || page <= 0 || size <= 0) {
            return pluginDao.findAll(categoryId, published, deleted);
        }

        int totalPage = pluginDao.listCount(categoryId, published, deleted) / size + 1;
        if (page > totalPage) {
            return pluginDao.findAllPaginated(categoryId, published, deleted, totalPage, size);
        }
        return pluginDao.findAllPaginated(categoryId, published, deleted, page, size);
    }

    @Override
    public void remove(String pluginId, Boolean deleted) throws ResultException {
        Plugin plugin = pluginDao.findById(pluginId).orElseThrow(PluginNotFoundException::new);
        plugin.setDeleted(deleted);
        pluginDao.update(plugin);
    }

    @Override
    public void delete(String pluginId, Boolean force) throws ResultException {
        Plugin plugin = pluginDao.findById(pluginId).orElseThrow(PluginNotFoundException::new);
        if (Objects.equals(force, true)) {
            delete(pluginId);
        } else {
            if (!plugin.getDeleted()) {
                throw new ResultException(PLUGIN_NOT_IN_RECYCLE_BIN);
            } else {
                delete(pluginId);
            }
        }
    }

    @Override
    public String findCategoryPath(String pluginId) throws ResultException {
        List<String> categoryIds = new ArrayList<>();
        Plugin plugin = pluginDao.findById(pluginId).orElseThrow(PluginNotFoundException::new);
        Category category = plugin.getCategory();
        if (category != null) {
            String currentCategoryId = category.getCategoryId();
            categoryIds.add(currentCategoryId);
            findParentCategoryId(currentCategoryId, categoryIds);
            return flatCategoryId(categoryIds);
        }
        return "";
    }

    @Override
    public Plugin findById(String pluginId) throws ResultException {
        return pluginDao.findById(pluginId).orElseThrow(PluginNotFoundException::new);
    }

    @Override
    public String findLatestVersion(Map<String, String> parameters) throws ResultException {
        String name = parameters.get("name");
        String compatibleVersion = parameters.get("compatibleVersion");
        String os = parameters.get("os");
        String arch = parameters.get("arch");
        if (name == null) {
            throw new ResultException(PLUGIN_NAME_EMPTY_ERROR);
        }
        if (compatibleVersion == null) {
            throw new ResultException(PLUGIN_COMPATIBLE_VERSION_EMPTY_ERROR);
        }
        if (os == null) {
            throw new ResultException(PLUGIN_OS_EMPTY_ERROR);
        }
        if (arch == null) {
            throw new ResultException(PLUGIN_ARCH_EMPTY_ERROR);
        }

        QueryVo queryVo = new QueryVo();
        queryVo.setName(name);
        queryVo.setCompatibleVersion(compatibleVersion);
        queryVo.setOs(os);
        queryVo.setArch(arch);
        queryVo.setPublished(true);
        queryVo.setDeleted(false);
        List<Plugin> plugins = pluginDao.findExactByProperties(queryVo);
        if (plugins.isEmpty()) {
            throw new PluginNotFoundException();
        }

        return plugins.stream()
                .map(Plugin::getVersion)
                .max(new VersionComparator())
                .get();
    }

    @Override
    public Integer listCount(String categoryId, Boolean published, Boolean deleted) {
        return pluginDao.listCount(categoryId, published, deleted);
    }

    @Override
    public Integer searchCount(String keyword, Boolean published, Boolean deleted) {
        QueryVo queryVo = new QueryVo();
        queryVo.setPluginId(keyword);
        queryVo.setName(keyword);
        queryVo.setBuild(keyword);
        queryVo.setVersion(keyword);
        queryVo.setDescription(keyword);
        queryVo.setCompatibleVersion(keyword);
        queryVo.setOs(keyword);
        queryVo.setArch(keyword);
        queryVo.setDependency(keyword);
        queryVo.setPublished(published);
        queryVo.setDeleted(deleted);

        return pluginDao.searchCount(queryVo);
    }

    private void findParentCategoryId(String categoryId, List<String> categoryIds) {
        categoryDao.findById(categoryId).ifPresent(category -> {
            String parentId = category.getParentId();
            if (!"0".equals(parentId)) {
                categoryIds.add(parentId);
                findParentCategoryId(parentId, categoryIds);
            }
        });
    }

    private String flatCategoryId(List<String> categoryIds) {
        StringJoiner joiner = new StringJoiner("/");
        int size = categoryIds.size();
        for (int i = size - 1; i >= 0; i--) {
            joiner.add(categoryIds.get(i));
        }
        return joiner.toString();
    }

    private String createEntityTypesJson(Plugin plugin) throws JsonProcessingException {
        String name = plugin.getName();
        String version = plugin.getVersion();
        Objects.requireNonNull(name);
        Objects.requireNonNull(version);

        Map<String, String> fields = new HashMap<>();
        fields.put("name", name);
        fields.put("version", version);

        Map<String, Map<String, String>> descriptor = new HashMap<>();
        descriptor.put("fields", fields);
        return objectMapper.writeValueAsString(descriptor);
    }

    private void delete(String pluginId) throws DeletePluginFailedException {
        pluginDao.deleteById(pluginId);
        try {
            if (blobstoreRest.exists(pluginId)) {
                blobstoreRest.removeBlobEntityById(pluginId);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new DeletePluginFailedException();
        }
    }

    private void validate(Plugin plugin) throws ResultException {
        String name = plugin.getName();
        String version = plugin.getVersion();
        if (StringUtils.isBlank(name) || StringUtils.isBlank(version)) {
            throw new ResultException(PLUGIN_NAME_VERSION_BLANK_ERROR);
        }

        Pattern pattern = Pattern.compile("(([0-9]|([1-9]([0-9]*))).){2}([0-9]|([1-9]([0-9]*)))");
        Matcher matcher = pattern.matcher(version);
        if (!matcher.matches()) {
            throw new InvalidVersionFormatException();
        }
    }
}
