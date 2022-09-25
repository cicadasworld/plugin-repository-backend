package gtcloud.plugin.repository.exception;

import gtcloud.plugin.repository.result.ResultException;
import gtcloud.plugin.repository.result.ResultStatus;

public class DeletePluginFailedException extends ResultException {

    public DeletePluginFailedException() {
        this(ResultStatus.DELETE_PLUGIN_FAILED);
    }

    public DeletePluginFailedException(ResultStatus resultStatus) {
        super(resultStatus);
    }
}
