package gtcloud.plugin.repository.exception;

import gtcloud.plugin.repository.result.ResultException;
import gtcloud.plugin.repository.result.ResultStatus;

public class ModifyPluginFailedException extends ResultException {

    public ModifyPluginFailedException() {
        this(ResultStatus.MODIFY_PLUGIN_FAILED);
    }

    public ModifyPluginFailedException(ResultStatus resultStatus) {
        super(resultStatus);
    }
}
