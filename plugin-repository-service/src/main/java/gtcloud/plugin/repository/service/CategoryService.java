package gtcloud.plugin.repository.service;

import gtcloud.plugin.repository.domain.entity.Category;
import gtcloud.plugin.repository.result.ResultException;

import java.util.List;

public interface CategoryService {

    /**
     * 添加类别
     * @param category 类别
     * @return 类别Id
     * @throws ResultException 业务异常
     */
    String add(Category category) throws ResultException;

    /**
     * 更新类别
     * @param category 类别
     * @throws ResultException 业务异常
     */
    void modify(Category category) throws ResultException;

    /**
     * 列举所有类别
     * @param parentId 父类别Id
     * @return 类别列表
     */
    List<Category> listAll(String parentId);

    /**
     * 列举父类别下的所有子类别
     * @param parentId 父类别Id
     * @return 类别列表
     */
    List<Category> listChildren(String parentId);

    /**
     * 删除类别及其子类别
     * @param categoryId 类别Id
     * @throws ResultException 业务异常
     */
    void delete(String categoryId) throws ResultException;

    /**
     * 类别信息
     * @param categoryId 类别Id
     * @return 类别信息
     * @throws ResultException 业务异常
     */
    Category findById(String categoryId) throws ResultException;
}
