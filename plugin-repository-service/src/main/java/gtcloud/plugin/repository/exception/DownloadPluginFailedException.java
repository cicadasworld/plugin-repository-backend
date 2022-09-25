package gtcloud.plugin.repository.exception;

import gtcloud.plugin.repository.result.ResultException;
import gtcloud.plugin.repository.result.ResultStatus;

public class DownloadPluginFailedException extends ResultException {

    public DownloadPluginFailedException() {
        this(ResultStatus.DOWNLOAD_PLUGIN_FAILED);
    }

    public DownloadPluginFailedException(ResultStatus resultStatus) {
        super(resultStatus);
    }
}
