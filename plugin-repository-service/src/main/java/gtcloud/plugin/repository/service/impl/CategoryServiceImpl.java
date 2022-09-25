package gtcloud.plugin.repository.service.impl;

import gtcloud.plugin.repository.dao.CategoryDao;
import gtcloud.plugin.repository.domain.entity.Category;
import gtcloud.plugin.repository.exception.CategoryNotFoundException;
import gtcloud.plugin.repository.exception.DuplicateCategoryException;
import gtcloud.plugin.repository.result.ResultException;
import gtcloud.plugin.repository.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static gtcloud.plugin.repository.result.ResultStatus.CATEGORY_NAME_BLANK_ERROR;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDao categoryDao;

    @Autowired
    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public String add(Category category) throws ResultException {
        validate(category);

        String categoryId = UUID.randomUUID().toString().replace("-", "");
        category.setCategoryId(categoryId);
        categoryDao.insert(category);
        return categoryId;
    }

    @Override
    public void modify(Category category) throws ResultException {
        validate(category);

        String categoryId = category.getCategoryId();
        if (!categoryDao.existsById(categoryId)) {
            throw new CategoryNotFoundException();
        }
        categoryDao.update(category);
    }

    @Override
    public List<Category> listAll(String parentId) {
        if (parentId != null) {
            return categoryDao.findByParentId(parentId);
        }
        return categoryDao.findAll();
    }

    @Override
    public List<Category> listChildren(String parentId) {
        return categoryDao.findByParentId(parentId);
    }

    @Override
    public void delete(String categoryId) throws ResultException {
        if (!categoryDao.existsById(categoryId)) {
            throw new CategoryNotFoundException();
        }
        categoryDao.deleteById(categoryId);
    }

    @Override
    public Category findById(String categoryId) throws ResultException {
        return categoryDao.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
    }

    private void validate(Category category) throws ResultException {
        String name = category.getName();
        if (StringUtils.isBlank(name)) {
            throw new ResultException(CATEGORY_NAME_BLANK_ERROR);
        }

        if (categoryDao.existsByName(name)) {
            throw new DuplicateCategoryException();
        }
    }
}
