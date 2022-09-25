package gtcloud.plugin.repository.exception;

import gtcloud.plugin.repository.result.ResultException;
import gtcloud.plugin.repository.result.ResultStatus;

public class PluginNotFoundException extends ResultException {

    public PluginNotFoundException() {
        this(ResultStatus.PLUGIN_NOT_FOUND);
    }

    public PluginNotFoundException(ResultStatus resultStatus) {
        super(resultStatus);
    }
}
