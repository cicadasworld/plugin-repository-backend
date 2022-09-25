package gtcloud.plugin.repository.exception;

import gtcloud.plugin.repository.result.ResultException;
import gtcloud.plugin.repository.result.ResultStatus;

public class UploadPluginFailedException extends ResultException {

    public UploadPluginFailedException() {
        this(ResultStatus.UPLOAD_PLUGIN_FAILED);
    }

    public UploadPluginFailedException(ResultStatus resultStatus) {
        super(resultStatus);
    }
}
