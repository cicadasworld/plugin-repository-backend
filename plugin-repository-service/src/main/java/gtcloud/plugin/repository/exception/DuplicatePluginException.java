package gtcloud.plugin.repository.exception;

import gtcloud.plugin.repository.result.ResultException;
import gtcloud.plugin.repository.result.ResultStatus;

public class DuplicatePluginException extends ResultException {

    public DuplicatePluginException() {
        this(ResultStatus.DUPLICATE_PLUGIN_ERROR);
    }

    public DuplicatePluginException(ResultStatus resultStatus) {
        super(resultStatus);
    }
}
