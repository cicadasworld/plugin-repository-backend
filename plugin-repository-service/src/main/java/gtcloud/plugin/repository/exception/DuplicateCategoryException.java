package gtcloud.plugin.repository.exception;

import gtcloud.plugin.repository.result.ResultException;
import gtcloud.plugin.repository.result.ResultStatus;

public class DuplicateCategoryException extends ResultException {

    public DuplicateCategoryException() {
        this(ResultStatus.DUPLICATE_CATEGORY_ERROR);
    }

    public DuplicateCategoryException(ResultStatus resultStatus) {
        super(resultStatus);
    }
}
