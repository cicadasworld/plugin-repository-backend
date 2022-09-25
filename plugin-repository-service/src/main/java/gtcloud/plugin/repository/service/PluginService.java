package gtcloud.plugin.repository.service;

import gtcloud.plugin.repository.domain.entity.Attachment;
import gtcloud.plugin.repository.domain.entity.Plugin;
import gtcloud.plugin.repository.result.ResultException;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface PluginService {

    /**
     * 向仓库里添加一个插件
     * @param plugin 插件
     * @return 插件Id
     * @throws ResultException 业务异常
     */
    String add(Plugin plugin) throws ResultException;

    /**
     * 插件上传
     * @param pluginId 插件Id
     * @param fileName 插件文件名
     * @param file 要上传的文件
     * @throws ResultException 业务异常
     */
    void upload(String pluginId, String fileName, File file) throws ResultException;

    /**
     * 插件更新
     * @param plugin 插件
     * @throws ResultException 业务异常
     */
    void modify(Plugin plugin) throws ResultException;

    /**
     * 插件发布
     * @param pluginId 插件Id
     * @param published 发布时设置为true, 反之设置为false
     * @throws ResultException 业务异常
     */
    void publish(String pluginId, Boolean published) throws ResultException;

    /**
     * 插件下载
     * @param pluginId 插件Id
     * @throws ResultException 业务异常
     * @return 附件
     */
    Attachment download(String pluginId) throws ResultException;

    /**
     * 按关键字查询插件
     * @param keyword 搜索关键字
     * @param published 发布状态
     * @param deleted 逻辑删除状态
     * @param page 当前页码
     * @param size 每页条数
     * @return 插件列表
     */
    List<Plugin> search(
        String keyword,
        Boolean published,
        Boolean deleted,
        Integer page,
        Integer size
    );

    /**
     * 按属性精确查询插件
     * @param parameters 查询参数
     * @return 插件列表
     * @throws ResultException 业务异常
     */
    List<Plugin> exact(Map<String, String> parameters) throws ResultException;

    /**
     * 列举所有插件
     * @param categoryId 插件类别Id
     * @param published 发布状态
     * @param deleted 逻辑删除状态
     * @param page 当前页码
     * @param size 每页条数
     * @return 插件列表
     */
    List<Plugin> listAll(
        String categoryId,
        Boolean published,
        Boolean deleted,
        Integer page,
        Integer size
    );

    /**
     * 把插件移入回收站
     * @param pluginId 插件Id
     * @param deleted 把插件移入回收站时设置为true, 反之移出回收站时设置为false
     * @throws ResultException 业务异常
     */
    void remove(String pluginId, Boolean deleted) throws ResultException;

    /**
     * 删除插件
     * @param pluginId 插件Id
     * @param force 强制删除
     * @throws ResultException 业务异常
     */
    void delete(String pluginId, Boolean force) throws ResultException;

    /**
     * 获取插件所属类别的完整类别路径，形如："{根类别Id}/{子类别Id}/.../{本级类别Id}"
     * @param pluginId 插件Id
     * @return 类别的完整类别路径
     * @throws ResultException 业务异常
     */
    String findCategoryPath(String pluginId) throws ResultException;

    /**
     * 插件详情
     * @param pluginId 插件Id
     * @return 插件详情
     * @throws ResultException 业务异常
     */
    Plugin findById(String pluginId) throws ResultException;

    /**
     * 获取插件的最新版本号
     * @param parameters 查询参数
     * @return 查询插件的最新版本号
     * @throws ResultException 业务异常
     */
    String findLatestVersion(Map<String, String> parameters) throws ResultException;

    /**
     * 获取插件总数
     * @param categoryId 插件类别
     * @param published 发布状态
     * @param deleted 逻辑删除状态
     * @return 插件总数
     */
    Integer listCount(String categoryId, Boolean published, Boolean deleted);

    /**
     * 获取插件总数
     * @param keyword 搜索关键字
     * @param published 发布状态
     * @param deleted 逻辑删除状态
     * @return 插件总数
     */
    Integer searchCount(String keyword, Boolean published, Boolean deleted);
}
