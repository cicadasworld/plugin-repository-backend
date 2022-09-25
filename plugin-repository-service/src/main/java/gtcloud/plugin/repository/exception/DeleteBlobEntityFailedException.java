package gtcloud.plugin.repository.exception;

import gtcloud.plugin.repository.result.ResultException;
import gtcloud.plugin.repository.result.ResultStatus;

public class DeleteBlobEntityFailedException extends ResultException {

    public DeleteBlobEntityFailedException() {
        this(ResultStatus.DELETE_BLOB_ENTITY_FAILED);
    }

    public DeleteBlobEntityFailedException(ResultStatus resultStatus) {
        super(resultStatus);
    }
}
