package gtcloud.plugin.repository.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;

public class Plugin {

    private String pluginId;
    private String name;
    private LocalDate build;
    private String version;
    private String description;
    private String compatibleVersion;
    private String os;
    private String arch;
    private String dependency;
    private Category category;
    @JsonIgnore
    private Boolean published;
    @JsonIgnore
    private Boolean deleted;

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBuild() {
        return build;
    }

    public void setBuild(LocalDate build) {
        this.build = build;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompatibleVersion() {
        return compatibleVersion;
    }

    public void setCompatibleVersion(String compatibleVersion) {
        this.compatibleVersion = compatibleVersion;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public String getDependency() {
        return dependency;
    }

    public void setDependency(String dependency) {
        this.dependency = dependency;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Plugin{" +
                "pluginId='" + pluginId + '\'' +
                ", name='" + name + '\'' +
                ", build=" + build +
                ", version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", compatibleVersion='" + compatibleVersion + '\'' +
                ", os='" + os + '\'' +
                ", arch='" + arch + '\'' +
                ", dependency='" + dependency + '\'' +
                ", category=" + category +
                ", published=" + published +
                ", deleted=" + deleted +
                '}';
    }
}
