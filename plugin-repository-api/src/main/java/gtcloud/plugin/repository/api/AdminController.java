package gtcloud.plugin.repository.api;

import gtcloud.plugin.repository.domain.entity.Category;
import gtcloud.plugin.repository.domain.entity.Plugin;
import gtcloud.plugin.repository.domain.vo.Page;
import gtcloud.plugin.repository.result.Result;
import gtcloud.plugin.repository.result.ResultException;
import gtcloud.plugin.repository.security.JwtService;
import gtcloud.plugin.repository.security.JwtSubject;
import gtcloud.plugin.repository.service.CategoryService;
import gtcloud.plugin.repository.service.PluginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gtcloud.plugin.repository.result.ResultStatus.UNAUTHORIZED_ERROR;
import static gtcloud.plugin.repository.result.ResultStatus.UPLOAD_FILE_FAILED;

@Tag(name = "插件管理(管理员角色)")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final PluginService pluginService;
    private final CategoryService categoryService;
    private final JwtService jwtService;

    @Autowired
    public AdminController(PluginService pluginService,
                           CategoryService categoryService,
                           JwtService jwtService) {
        this.pluginService = pluginService;
        this.categoryService = categoryService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "验证用户令牌", description = "验证用户令牌是否有效")
    @GetMapping("/auth/verify")
    public Result<Void> verify(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        StringBuilder builder = new StringBuilder(authorizationHeader);
        int pos = authorizationHeader.indexOf("Bearer");
        if (pos != -1) {
            String token = builder.delete(pos, pos + "Bearer".length() + 1).toString();
            JwtSubject subject = jwtService.verifyToken(token);
            if (subject != null) {
                return Result.success();
            }
        }
        return Result.failure(UNAUTHORIZED_ERROR);
    }

    @Operation(summary = "用户退出", description = "登录用户主动登出")
    @GetMapping("auth/logout")
    public Result<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        StringBuilder builder = new StringBuilder(authorizationHeader);
        int pos = authorizationHeader.indexOf("Bearer");
        if (pos != -1) {
            String token = builder.delete(pos, pos + "Bearer".length() + 1).toString();
            jwtService.deleteToken(token);
            return Result.success();
        }
        return Result.failure(UNAUTHORIZED_ERROR);
    }

    @Operation(summary = "列举所有插件", description = "按类别Id、是否已发布、是否已移入回收站列举所有插件(支持分页)")
    @GetMapping("/plugins/list")
    public Result<Page<Plugin>> list(
        @RequestParam(required = false, name = "categoryId") String categoryId,
        @RequestParam(required = false, name = "published") Boolean published,
        @RequestParam(required = false, name = "deleted") Boolean deleted,
        @RequestParam(required = false, name = "page") Integer page,
        @RequestParam(required = false, name = "size") Integer size) {
        Integer totalPages = pluginService.listCount(categoryId, published, deleted);
        List<Plugin> plugins = pluginService.listAll(categoryId, published, deleted, page, size);
        Page<Plugin> data = new Page<>(totalPages, plugins);
        return Result.success(data);
    }

    @Operation(summary = "模糊查询插件", description = "按关键字、是否已发布、是否已移入回收站查询所有插件(支持分页)")
    @GetMapping("/plugins/search")
    public Result<Page<Plugin>> search(
        @RequestParam(required = false, name = "keyword") String keyword,
        @RequestParam(required = false, name = "published") Boolean published,
        @RequestParam(required = false, name = "deleted") Boolean deleted,
        @RequestParam(required = false, name = "page") Integer page,
        @RequestParam(required = false, name = "size") Integer size
    ) {
        Integer totalPages = pluginService.searchCount(keyword, published, deleted);
        List<Plugin> plugins = pluginService.search(keyword, published, deleted, page, size);
        Page<Plugin> data = new Page<>(totalPages, plugins);
        return Result.success(data);
    }

    @Operation(summary = "添加插件", description = "向插件仓库里添加一个插件")
    @PostMapping("/plugins/add")
    public Result<Map<String, String>> add(@RequestBody Plugin plugin) throws ResultException {
        String pluginId = pluginService.add(plugin);
        Map<String, String> data = new HashMap<>();
        data.put("pluginId", pluginId);
        return Result.success(data);
    }

    @Operation(summary = "上传插件", description = "按插件Id上传某个插件压缩文件")
    @PostMapping("/plugins/upload/{id}")
    public Result<Void> upload(@PathVariable("id") String pluginId,
                         @RequestParam("files") MultipartFile[] multipartFiles) throws ResultException {
        if (multipartFiles.length == 0) {
            return Result.failure(UPLOAD_FILE_FAILED);
        }
        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile.isEmpty()) {
                return Result.failure(UPLOAD_FILE_FAILED);
            }
            try {
                Path path = Files.createTempFile(null, null);
                multipartFile.transferTo(path);
                pluginService.upload(pluginId, multipartFile.getOriginalFilename(), path.toFile());
                Files.delete(path);
            } catch (IOException e) {
                return Result.failure(UPLOAD_FILE_FAILED);
            }
        }
        return Result.success();
    }

    @Operation(summary = "获取插件详情", description = "按插件Id或插件Id列表获取某个或多个插件详情")
    @GetMapping("/plugins/{id}")
    public Result<List<Plugin>> findPluginById(@PathVariable("id") List<String> pluginIds) throws ResultException {
        List<Plugin> data = new ArrayList<>();
        for (String pluginId : pluginIds) {
            Plugin plugin = pluginService.findById(pluginId);
            data.add(plugin);
        }
        return Result.success(data);
    }

    @Operation(summary = "修改插件", description = "修改某个插件")
    @PutMapping("/plugins/modify")
    public Result<Void> modify(@RequestBody Plugin plugin) throws ResultException {
        pluginService.modify(plugin);
        return Result.success();
    }

    @Operation(summary = "发布插件", description = "按插件Id发布某个插件")
    @PostMapping("/plugins/publish/{id}")
    public Result<Void> publish(@PathVariable("id") String pluginId,
                          @RequestParam("published") Boolean published) throws ResultException {
        pluginService.publish(pluginId, published);
        return Result.success();
    }

    @Operation(summary = "移除插件", description = "按插件Id将某个插件移入回收站")
    @PostMapping("/plugins/remove/{id}")
    public Result<Void> remove(@PathVariable("id") String pluginId,
                         @RequestParam("deleted") Boolean deleted) throws ResultException {
        pluginService.remove(pluginId, deleted);
        return Result.success();
    }

    @Operation(summary = "删除插件", description = "按插件Id删除某个插件")
    @DeleteMapping("/plugins/delete/{id}")
    public Result<Void> deletePlugin(
            @PathVariable("id") String pluginId,
            @RequestParam(required = false, name = "force") Boolean force) throws ResultException {
        pluginService.delete(pluginId, force);
        return Result.success();
    }

    @Operation(summary = "添加插件类别", description = "添加一个插件类别")
    @PostMapping("/categories/add")
    public Result<Map<String, String>> add(@RequestBody Category category) throws ResultException {
        String categoryId = categoryService.add(category);
        Map<String, String> data = new HashMap<>();
        data.put("categoryId",categoryId);
        return Result.success(data);
    }

    @Operation(summary = "修改插件类别", description = "修改某个插件类别")
    @PutMapping("/categories/modify")
    public Result<Void> modify(@RequestBody Category category) throws ResultException {
        categoryService.modify(category);
        return Result.success();
    }

    @Operation(summary = "列举所有插件类别", description = "按类别父Id列举其所属的所有插件")
    @GetMapping("/categories/list")
    public Result<List<Category>> listAll(@RequestParam(required = false, name = "parentId") String parentId) {
        List<Category> categories = categoryService.listAll(parentId);
        return Result.success(categories);
    }

    @Operation(summary = "获取类别详情", description = "按类别Id获取某个类别详情")
    @GetMapping("/categories/{id}")
    public Result<Category> findCategoryById(@PathVariable("id") String categoryId) throws ResultException {
        Category category = categoryService.findById(categoryId);
        return Result.success(category);
    }

    @Operation(summary = "删除插件类别", description = "按类别Id删除某个插件类别")
    @DeleteMapping("/categories/delete/{id}")
    public Result<Void> deleteCategory(@PathVariable("id") String categoryId) throws ResultException {
        categoryService.delete(categoryId);
        return Result.success();
    }
}
