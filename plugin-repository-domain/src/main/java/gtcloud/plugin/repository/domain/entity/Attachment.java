package gtcloud.plugin.repository.domain.entity;

import java.io.File;

public class Attachment {

    private String contentDisposition;
    private File file;

    public String getContentDisposition() {
        return contentDisposition;
    }

    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
