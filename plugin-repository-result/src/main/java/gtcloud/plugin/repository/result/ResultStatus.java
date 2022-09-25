package gtcloud.plugin.repository.result;

import org.springframework.http.HttpStatus;

public enum ResultStatus {
    SUCCESS(HttpStatus.OK, 0, "OK"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, -1, "Bad Request"),
    NOT_FOUND(HttpStatus.NOT_FOUND, -1, "Not Found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, -1, "Internal Server Error"),
    UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, -1, "Unauthorized"),
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, -1, "Forbidden"),
    METHOD_NOT_SUPPORTED_ERROR(HttpStatus.METHOD_NOT_ALLOWED, -1, "Request Method Not Supported"),
    SQL_INJECTION_DETECTED(HttpStatus.BAD_REQUEST, -1, "Bad Request, Sql injection detected."),
    XSS_DETECTED(HttpStatus.BAD_REQUEST, -1, "Bad Request, XSS detected."),
    MISSING_REQUEST_PARAMETER_ERROR(HttpStatus.BAD_REQUEST, -1, "Missing Request Parameter"),
    UPLOAD_FILE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, -1, "Upload File Failed"),
    PLUGIN_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, -1, "Plugin Not Found"),
    DUPLICATE_PLUGIN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, -1, "Duplicate Plugin with the Same Name, Version, Os and Arch"),
    CATEGORY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, -1, "Category Not Found"),
    DUPLICATE_CATEGORY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, -1, "Duplicate Category with the Same Name"),
    DELETE_BLOB_ENTITY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, -1, "Delete Blob Entity Failed"),
    EXPIRED_TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, -1, "Expired Token Error"),
    INVALID_TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, -1, "Invalid Token Error"),
    INVALID_VERSION_FORMAT_ERROR(HttpStatus.BAD_REQUEST, -1, "Version only contains number and separated by dot(.) eg: '1.0.2'"),
    CATEGORY_NAME_BLANK_ERROR(HttpStatus.BAD_REQUEST, -1, "Category is mandatory"),
    ADD_PLUGIN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, -1, "Add Plugin Failed"),
    UPLOAD_PLUGIN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, -1, "Upload Plugin Failed"),
    MODIFY_PLUGIN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, -1, "Modify Plugin Failed"),
    DOWNLOAD_PLUGIN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, -1, "Download Plugin Failed"),
    DELETE_PLUGIN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, -1, "Delete Plugin Failed"),
    PLUGIN_NAME_VERSION_BLANK_ERROR(HttpStatus.BAD_REQUEST, -1, "Name and version is mandatory"),
    PLUGIN_NAME_EMPTY_ERROR(HttpStatus.BAD_REQUEST, -1, "'name' may not be empty"),
    PLUGIN_VERSION_EMPTY_ERROR(HttpStatus.BAD_REQUEST, -1, "'version' may not be empty"),
    PLUGIN_COMPATIBLE_VERSION_EMPTY_ERROR(HttpStatus.BAD_REQUEST, -1, "'compatibleVersion' may not be empty"),
    PLUGIN_OS_EMPTY_ERROR(HttpStatus.BAD_REQUEST, -1, "'os' may not be empty"),
    PLUGIN_ARCH_EMPTY_ERROR(HttpStatus.BAD_REQUEST, -1, "'arch' may not be empty"),
    PLUGIN_NOT_IN_RECYCLE_BIN(HttpStatus.BAD_REQUEST, -1, "Please remove the Plugin to the recycle bin first"),
    MIFS_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, -1, "MIFS Server Error"),
    USERNAME_OR_PASSWORD_ERROR(HttpStatus.BAD_REQUEST, -1, "Username Not Exist or Password Error"),
    ;

    /**
     * 返回的HTTP状态码, 符合http请求
     */
    private HttpStatus httpStatus;

    /**
     * 业务状态码
     */
    private int retcode;

    /**
     * 业务异常信息描述
     */
    private String retmsg;

    ResultStatus(HttpStatus httpStatus, int retcode, String retmsg) {
        this.httpStatus = httpStatus;
        this.retcode = retcode;
        this.retmsg = retmsg;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public int getRetcode() {
        return retcode;
    }

    public String getRetmsg() {
        return retmsg;
    }
}
