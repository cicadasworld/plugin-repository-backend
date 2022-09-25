package gtcloud.plugin.repository.dao;

import gtcloud.plugin.repository.domain.entity.Plugin;
import gtcloud.plugin.repository.domain.vo.QueryVo;
import gtcloud.plugin.repository.utils.PredicateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Repository
public class PluginDao {

    private static final Logger LOG = LoggerFactory.getLogger(PluginDao.class);

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final CategoryDao categoryDao;

    @Autowired
    public PluginDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                     CategoryDao categoryDao) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.categoryDao = categoryDao;
    }

    public Integer listCount(String categoryId, Boolean published, Boolean deleted) {
        String sql = "select count(*) from plugin";
        StringBuilder sqlBuilder = new StringBuilder(sql);
        StringBuilder whereClause = new StringBuilder(" where 1 = 1");

        if (categoryId != null) {
            whereClause.append(" and category_id = '")
                    .append(categoryId)
                    .append("'");
        }

        if (published != null) {
            whereClause.append(" and published = ")
                    .append(published ? 1 : 0);
        }

        if (deleted!= null) {
            whereClause.append(" and deleted = ")
                    .append(deleted ? 1 : 0);
        }

        String querySql = sqlBuilder.append(whereClause).toString();
        return namedParameterJdbcTemplate.queryForObject(querySql, new HashMap<>(), Integer.class);
    }

    public Integer searchCount(QueryVo queryVo) {
        String sql = "select plugin_id, name, build, version, description, compatible_version, os, arch, dependency, category_id, published, deleted from plugin";
        StringBuilder sqlBuilder = new StringBuilder(sql);

        StringBuilder whereClause = new StringBuilder(" where 1 = 1");

        String pluginId = queryVo.getPluginId();
        if (!StringUtils.isEmpty(pluginId)) {
            whereClause.append(" or plugin_id like '%")
                    .append(pluginId)
                    .append("%'");
        }

        String name = queryVo.getName();
        if (!StringUtils.isEmpty(name)) {
            whereClause.append(" or name like '%")
                    .append(name)
                    .append("%'");
        }

        String build = queryVo.getBuild();
        if (!StringUtils.isEmpty(build)) {
            whereClause.append(" or build like '%")
                    .append(build)
                    .append("%'");
        }

        String version = queryVo.getVersion();
        if (!StringUtils.isEmpty(version)) {
            whereClause.append(" or version like '%")
                    .append(version)
                    .append("%'");
        }

        String description = queryVo.getDescription();
        if (!StringUtils.isEmpty(description)) {
            whereClause.append(" or description like '%")
                    .append(description)
                    .append("%'");
        }

        String compatibleVersion = queryVo.getCompatibleVersion();
        if (!StringUtils.isEmpty(compatibleVersion)) {
            whereClause.append(" or compatible_version like '%")
                    .append(compatibleVersion)
                    .append("%'");
        }

        String os = queryVo.getOs();
        if (!StringUtils.isEmpty(os)) {
            whereClause.append(" or os like '%")
                    .append(os)
                    .append("%'");
        }

        String arch = queryVo.getArch();
        if (!StringUtils.isEmpty(arch)) {
            whereClause.append(" or arch like '%")
                    .append(arch)
                    .append("%'");
        }

        String dependency = queryVo.getDependency();
        if (!StringUtils.isEmpty(dependency)) {
            whereClause.append(" or dependency like '%")
                    .append(dependency)
                    .append("%'");
        }

        String categoryIds = queryVo.getCategory();
        if (!StringUtils.isEmpty(categoryIds)) {
            String[] ids = categoryIds.split(",");
            StringBuilder inClause = new StringBuilder(" or category_id in (");
            for (int i = 0; i < ids.length; i++) {
                if (i == ids.length - 1) {
                    inClause.append("'").append(ids[i]).append("')");
                } else {
                    inClause.append("'").append(ids[i]).append("',");
                }
            }
            whereClause.append(inClause);
        }

        // trim
        String querySql = trim(sqlBuilder.append(whereClause).toString());
        LOG.debug(querySql);

        Boolean published = queryVo.getPublished();
        Boolean deleted = queryVo.getDeleted();

        PredicateBuilder<Plugin> predicateBuilder = new PredicateBuilder<>();
        predicateBuilder.addPredicate(published != null, plugin -> Objects.equals(plugin.getPublished(), published))
                .addPredicate(deleted != null, plugin -> Objects.equals(plugin.getDeleted(), deleted));

        Predicate<Plugin> predicate = predicateBuilder.build();

        return (int) namedParameterJdbcTemplate.query(querySql, this::mapToPlugin)
                .stream()
                .filter(predicate)
                .count();
    }

    public void insert(Plugin plugin) {
        String sql = "insert into plugin (plugin_id, name, build, version, description, compatible_version, os, arch, dependency, category_id, published, deleted) " +
                "values (:pluginId, :name, :build, :version, :description, :compatibleVersion, :os, :arch, :dependency, :categoryId, :published, :deleted)";
        SqlParameterSource sqlParameters = new MapSqlParameterSource()
            .addValue("pluginId", plugin.getPluginId())
            .addValue("name", plugin.getName())
            .addValue("build", plugin.getBuild() != null ? plugin.getBuild().toString() : null)
            .addValue("version", plugin.getVersion())
            .addValue("description", plugin.getDescription())
            .addValue("compatibleVersion", plugin.getCompatibleVersion())
            .addValue("os", plugin.getOs())
            .addValue("arch", plugin.getArch())
            .addValue("dependency", plugin.getDependency())
            .addValue("categoryId", plugin.getCategory() != null ? plugin.getCategory().getCategoryId() : null)
            .addValue("published", plugin.getPublished() != null ? plugin.getPublished(): 0)
            .addValue("deleted", plugin.getDeleted() != null ? plugin.getDeleted() : 0);
        namedParameterJdbcTemplate.update(sql, sqlParameters);
    }

    public void update(Plugin plugin) {
        String sql = "update plugin set " +
                "name = :name, " +
                "build = :build, " +
                "version = :version, " +
                "description = :description, " +
                "compatible_version = :compatibleVersion, " +
                "os = :os, " +
                "arch = :arch, " +
                "dependency = :dependency, " +
                "category_id = :categoryId, " +
                "published = :published, " +
                "deleted = :deleted " +
                "where plugin_id = :pluginId";
        SqlParameterSource sqlParameters = new MapSqlParameterSource()
            .addValue("pluginId", plugin.getPluginId())
            .addValue("name", plugin.getName())
            .addValue("build", plugin.getBuild() != null ? plugin.getBuild().toString() : null)
            .addValue("version", plugin.getVersion())
            .addValue("description", plugin.getDescription())
            .addValue("compatibleVersion", plugin.getCompatibleVersion())
            .addValue("os", plugin.getOs())
            .addValue("arch", plugin.getArch())
            .addValue("dependency", plugin.getDependency())
            .addValue("categoryId", plugin.getCategory() != null ? plugin.getCategory().getCategoryId() : null)
            .addValue("published", plugin.getPublished() != null ? plugin.getPublished(): 0)
            .addValue("deleted", plugin.getDeleted() != null ? plugin.getDeleted() : 0);
        namedParameterJdbcTemplate.update(sql, sqlParameters);
    }

    public void deleteById(String id) {
        String sql = "delete from plugin where plugin_id = :pluginId";
        SqlParameterSource sqlParameters = this.createIdParameterSource(id);
        namedParameterJdbcTemplate.update(sql, sqlParameters);
    }

    public Boolean existsById(String id) {
        String sql = "select count(*) from plugin where plugin_id = :pluginId";
        SqlParameterSource sqlParameters = this.createIdParameterSource(id);
        return namedParameterJdbcTemplate.queryForObject(sql, sqlParameters, Boolean.class);
    }

    public Boolean existsByNameVersionOsArch(String name, String version, String os, String arch) {
        String sql = "select count(*) from plugin where name = :name and version = :version and os = :os and arch = :arch";
        SqlParameterSource sqlParameters = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("version", version)
                .addValue("os", os)
                .addValue("arch", arch);
        return namedParameterJdbcTemplate.queryForObject(sql, sqlParameters, Boolean.class);
    }

    public Optional<Plugin> findById(String id) {
        String sql = "select plugin_id, name, build, version, description, compatible_version, os, arch, dependency, category_id, published, deleted from plugin " +
                "where plugin_id = :pluginId";
        SqlParameterSource sqlParameters = this.createIdParameterSource(id);
        try {
            Plugin plugin = namedParameterJdbcTemplate.queryForObject(sql, sqlParameters, this::mapToPlugin);
            return Optional.ofNullable(plugin);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Plugin> findAll(String categoryId, Boolean published, Boolean deleted) {
        return findAllPaginated(categoryId, published, deleted, null, null);
    }

    public List<Plugin> findAllPaginated(String categoryId, Boolean published, Boolean deleted, Integer page, Integer size) {
        String sql = "select plugin_id, name, build, version, description, compatible_version, os, arch, dependency, category_id, published, deleted from plugin";
        StringBuilder sqlBuilder = new StringBuilder(sql);

        StringBuilder whereClause = new StringBuilder(" where 1 = 1");

        if (categoryId != null) {
            whereClause.append(" and category_id = '")
                    .append(categoryId)
                    .append("'");
        }

        if (published != null) {
            whereClause.append(" and published = ")
                    .append(published ? 1 : 0);
        }

        if (deleted!= null) {
            whereClause.append(" and deleted = ")
                    .append(deleted ? 1 : 0);
        }

        // 分页
        if (page != null && size != null) {
            String limitClause = " limit :index, :size";
            long index = (long) (page - 1) * size;
            SqlParameterSource sqlParameters = new MapSqlParameterSource()
                    .addValue("index", index)
                    .addValue("size", size);
            String querySql = sqlBuilder.append(whereClause)
                    .append(limitClause)
                    .toString();
            LOG.debug(querySql);
            return namedParameterJdbcTemplate.query(querySql, sqlParameters, this::mapToPlugin);
        }

        String querySql = sqlBuilder.append(whereClause).toString();
        LOG.debug(querySql);
        return namedParameterJdbcTemplate.query(querySql, this::mapToPlugin);
    }

    public List<Plugin> findAllByPropertyPaginated(QueryVo queryVo, Integer page, Integer size) {
        String sql = "select plugin_id, name, build, version, description, compatible_version, os, arch, dependency, category_id, published, deleted from plugin";
        StringBuilder sqlBuilder = new StringBuilder(sql);

        StringBuilder whereClause = new StringBuilder(" where 1 = 1");

        String pluginId = queryVo.getPluginId();
        if (!StringUtils.isEmpty(pluginId)) {
            whereClause.append(" or plugin_id like '%")
                    .append(pluginId)
                    .append("%'");
        }

        String name = queryVo.getName();
        if (!StringUtils.isEmpty(name)) {
            whereClause.append(" or name like '%")
                    .append(name)
                    .append("%'");
        }

        String build = queryVo.getBuild();
        if (!StringUtils.isEmpty(build)) {
            whereClause.append(" or build like '%")
                    .append(build)
                    .append("%'");
        }

        String version = queryVo.getVersion();
        if (!StringUtils.isEmpty(version)) {
            whereClause.append(" or version like '%")
                    .append(version)
                    .append("%'");
        }

        String description = queryVo.getDescription();
        if (!StringUtils.isEmpty(description)) {
            whereClause.append(" or description like '%")
                    .append(description)
                    .append("%'");
        }

        String compatibleVersion = queryVo.getCompatibleVersion();
        if (!StringUtils.isEmpty(compatibleVersion)) {
            whereClause.append(" or compatible_version like '%")
                    .append(compatibleVersion)
                    .append("%'");
        }

        String os = queryVo.getOs();
        if (!StringUtils.isEmpty(os)) {
            whereClause.append(" or os like '%")
                    .append(os)
                    .append("%'");
        }

        String arch = queryVo.getArch();
        if (!StringUtils.isEmpty(arch)) {
            whereClause.append(" or arch like '%")
                    .append(arch)
                    .append("%'");
        }

        String dependency = queryVo.getDependency();
        if (!StringUtils.isEmpty(dependency)) {
            whereClause.append(" or dependency like '%")
                    .append(dependency)
                    .append("%'");
        }

        String categoryIds = queryVo.getCategory();
        if (!StringUtils.isEmpty(categoryIds)) {
            String[] ids = categoryIds.split(",");
            StringBuilder inClause = new StringBuilder(" or category_id in (");
            for (int i = 0; i < ids.length; i++) {
                if (i == ids.length - 1) {
                    inClause.append("'").append(ids[i]).append("')");
                } else {
                    inClause.append("'").append(ids[i]).append("',");
                }
            }
            whereClause.append(inClause);
        }

        String querySql = trim(sqlBuilder.append(whereClause).toString());

        Boolean published = queryVo.getPublished();
        Boolean deleted = queryVo.getDeleted();

        PredicateBuilder<Plugin> predicateBuilder = new PredicateBuilder<>();
        predicateBuilder.addPredicate(published != null, plugin -> Objects.equals(plugin.getPublished(), published))
                .addPredicate(deleted != null, plugin -> Objects.equals(plugin.getDeleted(), deleted));
        Predicate<Plugin> predicate = predicateBuilder.build();

        List<Plugin> plugins = namedParameterJdbcTemplate.query(querySql, this::mapToPlugin)
                .stream()
                .filter(predicate)
                .collect(toList());

        if (page == null || size == null || page <= 0 || size <= 0) {
            return plugins;
        }

        // 分页
        int startIndex = (page - 1) * size;
        int endIndex;
        Integer totalCount = this.searchCount(queryVo);
        if (startIndex > totalCount) {
            return emptyList();
        }

        int index = startIndex + size;
        if (index > totalCount) {
            endIndex = totalCount;
        } else {
            endIndex = index;
        }

        List<Plugin> result = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            result.add(plugins.get(i));
        }
        return result;
    }

    public List<Plugin> findExactByProperties(QueryVo queryVo) {
        String sql = "select plugin_id, name, build, version, description, compatible_version, os, arch, dependency, category_id, published, deleted from plugin";
        StringBuilder sqlBuilder = new StringBuilder(sql);

        StringBuilder whereClause = new StringBuilder(" where 1 = 1");

        String name = queryVo.getName();
        if (!StringUtils.isEmpty(name)) {
            whereClause.append(" and name = :name");
        }

        String build = queryVo.getBuild();
        if (!StringUtils.isEmpty(build)) {
            whereClause.append(" and build = :build");
        }

        String version = queryVo.getVersion();
        if (!StringUtils.isEmpty(version)) {
            whereClause.append(" and version = :version");
        }

        String description = queryVo.getDescription();
        if (!StringUtils.isEmpty(description)) {
            whereClause.append(" and description = :description");
        }

        String compatibleVersion = queryVo.getCompatibleVersion();
        if (!StringUtils.isEmpty(compatibleVersion)) {
            whereClause.append(" and compatible_version = :compatibleVersion");
        }

        String os = queryVo.getOs();
        if (!StringUtils.isEmpty(os)) {
            whereClause.append(" and os = :os");
        }

        String arch = queryVo.getArch();
        if (!StringUtils.isEmpty(arch)) {
            whereClause.append(" and arch = :arch");
        }

        Boolean published = queryVo.getPublished();
        if (published != null) {
            whereClause.append(" and published = ")
                    .append(published ? 1 : 0);
        }

        Boolean deleted = queryVo.getDeleted();
        if (deleted!= null) {
            whereClause.append(" and deleted = ")
                    .append(deleted ? 1 : 0);
        }

        String querySql = sqlBuilder.append(whereClause)
                .toString();

        SqlParameterSource sqlParameters = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("build", build)
                .addValue("version", version)
                .addValue("description", description)
                .addValue("compatibleVersion", compatibleVersion)
                .addValue("os", os)
                .addValue("arch", arch);

        return namedParameterJdbcTemplate.query(querySql, sqlParameters, this::mapToPlugin);
    }

    private Plugin mapToPlugin(ResultSet rs, int rowNum) throws SQLException {
        Plugin plugin = new Plugin();
        plugin.setPluginId(rs.getString("plugin_id"));
        plugin.setName(rs.getString("name"));
        Optional.ofNullable(rs.getDate("build"))
                .ifPresent(d -> plugin.setBuild(d.toLocalDate()));
        plugin.setVersion(rs.getString("version"));
        plugin.setDescription(rs.getString("description"));
        plugin.setCompatibleVersion(rs.getString("compatible_version"));
        plugin.setOs(rs.getString("os"));
        plugin.setArch(rs.getString("arch"));
        plugin.setDependency(rs.getString("dependency"));
        categoryDao.findById(rs.getString("category_id"))
                .ifPresent(plugin::setCategory);
        plugin.setPublished(rs.getBoolean("published"));
        plugin.setDeleted(rs.getBoolean("deleted"));
        return plugin;
    }

    private SqlParameterSource createIdParameterSource(String id) {
        return new MapSqlParameterSource().addValue("pluginId", id);
    }

    private String trim(String concatSql) {
        StringBuilder builder = new StringBuilder(concatSql);
        String target = " 1 = 1 or";
        int pos = concatSql.indexOf(target);
        if (pos != -1) {
            return builder.delete(pos, pos + target.length()).toString();
        }
        return concatSql;
    }
}
