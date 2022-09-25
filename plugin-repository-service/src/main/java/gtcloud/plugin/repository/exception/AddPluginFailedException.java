package gtcloud.plugin.repository.exception;

import gtcloud.plugin.repository.result.ResultException;
import gtcloud.plugin.repository.result.ResultStatus;

public class AddPluginFailedException extends ResultException {
    public AddPluginFailedException() {
        this(ResultStatus.ADD_PLUGIN_FAILED);
    }

    public AddPluginFailedException(ResultStatus resultStatus) {
        super(resultStatus);
    }
}
