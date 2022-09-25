package gtcloud.plugin.repository.exception;

import gtcloud.plugin.repository.result.ResultException;
import gtcloud.plugin.repository.result.ResultStatus;

public class CategoryNotFoundException extends ResultException {

    public CategoryNotFoundException() {
        this(ResultStatus.CATEGORY_NOT_FOUND);
    }

    public CategoryNotFoundException(ResultStatus resultStatus) {
        super(resultStatus);
    }
}
