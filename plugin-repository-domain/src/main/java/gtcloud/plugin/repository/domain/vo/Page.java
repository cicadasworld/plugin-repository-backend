package gtcloud.plugin.repository.domain.vo;

import java.util.List;

public class Page<T> {

    private Integer totalPages;
    private List<T> data;

    public Page(Integer totalPages, List<T> data) {
        this.totalPages = totalPages;
        this.data = data;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public List<T> getData() {
        return data;
    }
}
