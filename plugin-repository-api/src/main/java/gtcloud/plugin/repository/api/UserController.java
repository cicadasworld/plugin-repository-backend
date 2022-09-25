package gtcloud.plugin.repository.api;

import gtcloud.plugin.repository.domain.entity.Attachment;
import gtcloud.plugin.repository.domain.entity.Category;
import gtcloud.plugin.repository.domain.entity.Plugin;
import gtcloud.plugin.repository.domain.vo.LoginUser;
import gtcloud.plugin.repository.domain.vo.Page;
import gtcloud.plugin.repository.exception.DownloadPluginFailedException;
import gtcloud.plugin.repository.result.Result;
import gtcloud.plugin.repository.result.ResultException;
import gtcloud.plugin.repository.security.AuthenticationService;
import gtcloud.plugin.repository.security.JwtService;
import gtcloud.plugin.repository.security.JwtSubject;
import gtcloud.plugin.repository.service.CategoryService;
import gtcloud.plugin.repository.service.PluginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gtcloud.plugin.repository.result.ResultStatus.FORBIDDEN_ERROR;
import static gtcloud.plugin.repository.result.ResultStatus.UNAUTHORIZED_ERROR;

@Tag(name = "插件管理(普通用户角色)")
@RestController
@RequestMapping("/user")
public class UserController {

    private final PluginService pluginService;
    private final CategoryService categoryService;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @Autowired
    public UserController(PluginService pluginService,
                          CategoryService categoryService,
                          AuthenticationService authenticationService,
                          JwtService jwtService) {
        this.pluginService = pluginService;
        this.categoryService = categoryService;
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "列举所有插件", description = "按类别Id列举所有插件(支持分页)")
    @GetMapping("/plugins/list")
    public Result<Page<Plugin>> list(@RequestParam(required = false, name = "categoryId") String categoryId,
                       @RequestParam(required = false, name = "page") Integer page,
                       @RequestParam(required = false, name = "size") Integer size) {
        Integer totalPages = pluginService.listCount(categoryId, true, false);
        List<Plugin> plugins = pluginService.listAll(categoryId, true, false, page, size);
        Page<Plugin> data = new Page<>(totalPages, plugins);
        return Result.success(data);
    }

    @Operation(summary = "模糊查询插件", description = "按关键字查询插件(支持分页)")
    @GetMapping("/plugins/search")
    public Result<Page<Plugin>> search(
        @RequestParam(required = false, name = "keyword") String keyword,
        @RequestParam(required = false, name = "page") Integer page,
        @RequestParam(required = false, name = "size") Integer size
    ) {
        Integer totalPages = pluginService.searchCount(keyword,true, false);
        List<Plugin> plugins = pluginService.search(keyword,true,false, page, size);
        Page<Plugin> data = new Page<>(totalPages, plugins);
        return Result.success(data);
    }

    @Operation(summary = "查询某个插件", description = "按插件名、版本号、操作系统和处理器架构精确查询某个插件")
    @PostMapping("/plugins/exact")
    public Result<List<Plugin>> exact(@RequestBody Map<String, String> parameters) throws ResultException {
        List<Plugin> plugins = pluginService.exact(parameters);
        return Result.success(plugins);
    }

    @Operation(summary = "查询最新插件", description = "按插件名、兼容版本号、操作系统和处理器架构查询最新插件版本号")
    @PostMapping("/plugins/latest")
    public Result<Map<String, String>> findLatestVersion(@RequestBody Map<String, String> parameters) throws ResultException {
        String version = pluginService.findLatestVersion(parameters);
        Map<String, String> data = new HashMap<>();
        data.put("version", version);
        return Result.success(data);
    }

    // "categoryPath": "{根类别Id}/{子类别Id}/.../{本级类别Id}"
    @Operation(summary = "获取插件类别路径", description = "按插件Id获取插件类别路径")
    @GetMapping("/plugins/category-path/{id}")
    public Result<Map<String, String>> findCategoryPath(@PathVariable("id") String pluginId) throws ResultException {
        Plugin plugin = pluginService.findById(pluginId);
        if (plugin.getPublished() && !plugin.getDeleted()) {
            String categoryPath = pluginService.findCategoryPath(pluginId);
            Map<String, String> data = new HashMap<>();
            data.put("categoryPath", categoryPath);
            return Result.success(data);
        }
        return Result.failure(FORBIDDEN_ERROR);
    }

    @Operation(summary = "获取插件详情", description = "按插件Id或插件Id列表获取某个或多个插件详情")
    @GetMapping("/plugins/{id}")
    public Result<List<Plugin>> findPluginById(@PathVariable("id") List<String> pluginIds) throws ResultException {
        List<Plugin> data = new ArrayList<>();
        for (String pluginId : pluginIds) {
            Plugin plugin = pluginService.findById(pluginId);
            if (plugin.getPublished() && !plugin.getDeleted()) {
                data.add(plugin);
            } else {
                return Result.failure(FORBIDDEN_ERROR);
            }
        }
        return Result.success(data);
    }

    @Operation(summary = "下载插件", description = "按插件Id下载某个插件")
    @GetMapping("/plugins/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable("id") String pluginId) throws ResultException {
        Plugin plugin = pluginService.findById(pluginId);
        if (plugin.getPublished() && !plugin.getDeleted()) {
            Attachment attachment = pluginService.download(pluginId);
            String contentDisposition = attachment.getContentDisposition();
            File file = attachment.getFile();
            try {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(new InputStreamResource(new FileInputStream(file)));
            } catch (FileNotFoundException e) {
                throw new DownloadPluginFailedException();
            } finally {
                if (file != null) {
                    file.delete();
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @Operation(summary = "获取插件类别", description = "按类别Id获取某个插件类别")
    @GetMapping("/categories/{id}")
    public Result<Category> findCategoryById(@PathVariable("id") String categoryId) throws ResultException {
        Category category = categoryService.findById(categoryId);
        return Result.success(category);
    }

    @Operation(summary = "用户登录", description = "用户登录")
    @PostMapping("/auth/login")
    public Result<Map<String, String>> login(@RequestBody LoginUser loginUser) throws ResultException {
        if (authenticationService.isValid(loginUser)) {
            JwtSubject subject = new JwtSubject(loginUser.getUsername());
            String token = jwtService.createToken(subject);
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            return Result.success(data);
        }
        return Result.failure(UNAUTHORIZED_ERROR);
    }
}
