package gtcloud.plugin.repository.dao;

import gtcloud.plugin.repository.domain.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public CategoryDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void insert(Category category) {
        String sql = "insert into category (category_id, name, parent_id) " +
                "values (:categoryId, :name, :parentId)";
        SqlParameterSource sqlParameters = new MapSqlParameterSource()
                .addValue("categoryId", category.getCategoryId())
                .addValue("name", category.getName())
                .addValue("parentId", category.getParentId());
        namedParameterJdbcTemplate.update(sql, sqlParameters);
    }

    public void update(Category category) {
        String sql = "update category set name = :name, parent_id = :parentId where category_id = :categoryId";
        SqlParameterSource sqlParameters = new MapSqlParameterSource()
                .addValue("categoryId", category.getCategoryId())
                .addValue("name", category.getName())
                .addValue("parentId", category.getParentId());
        namedParameterJdbcTemplate.update(sql, sqlParameters);
    }

    public void deleteById(String id) {
        String sql = "delete from category where category_id = :categoryId";
        SqlParameterSource sqlParameters = this.createIdParameterSource(id);
        namedParameterJdbcTemplate.update(sql, sqlParameters);
    }

    public Boolean existsById(String id) {
        String sql = "select count(*) from category where category_id = :categoryId";
        SqlParameterSource sqlParameters = this.createIdParameterSource(id);
        return namedParameterJdbcTemplate.queryForObject(sql, sqlParameters, Boolean.class);
    }

    public Boolean existsByName(String name) {
        String sql = "select count(*) from category where name = :name";
        SqlParameterSource sqlParameters = new MapSqlParameterSource().addValue("name", name);
        return namedParameterJdbcTemplate.queryForObject(sql, sqlParameters, Boolean.class);
    }

    public Optional<Category> findById(String id) {
        String sql = "select category_id, name, parent_id from category where category_id = :categoryId";
        SqlParameterSource sqlParameters = this.createIdParameterSource(id);
        try {
            Category category = namedParameterJdbcTemplate.queryForObject(sql, sqlParameters, this::mapToCategory);
            return Optional.ofNullable(category);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Category> findAll() {
        String sql = "select category_id, name, parent_id from category";
        return namedParameterJdbcTemplate.query(sql, this::mapToCategory);
    }

    public List<Category> findByParentId(String parentId) {
        String sql = "select category_id, name, parent_id from category where parent_id = :parentId";
        SqlParameterSource sqlParameters = new MapSqlParameterSource().addValue("parentId", parentId);
        return namedParameterJdbcTemplate.query(sql, sqlParameters, this::mapToCategory);
    }

    public List<String> findByNameLike(String name) {
        String sql = "select category_id from category where name like '%" + name + "%'";
        return namedParameterJdbcTemplate.query(sql, this::mapToCategoryId);
    }

    private Category mapToCategory(ResultSet rs, int rowNum) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getString("category_id"));
        category.setName(rs.getString("name"));
        category.setParentId(rs.getString("parent_id"));
        return category;
    }

    private String mapToCategoryId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("category_id");
    }

    private SqlParameterSource createIdParameterSource(String id) {
        return new MapSqlParameterSource().addValue("categoryId", id);
    }
}
