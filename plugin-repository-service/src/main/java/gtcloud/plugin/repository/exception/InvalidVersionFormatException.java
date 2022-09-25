package gtcloud.plugin.repository.exception;

import gtcloud.plugin.repository.result.ResultException;
import gtcloud.plugin.repository.result.ResultStatus;

public class InvalidVersionFormatException extends ResultException {

    public InvalidVersionFormatException() {
        this(ResultStatus.INVALID_VERSION_FORMAT_ERROR);
    }

    public InvalidVersionFormatException(ResultStatus resultStatus) {
        super(resultStatus);
    }
}
