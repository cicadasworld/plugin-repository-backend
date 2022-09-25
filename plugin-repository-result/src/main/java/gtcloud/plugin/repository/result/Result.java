package gtcloud.plugin.repository.result;

public class Result<T> {

    /**
     * 业务错误码
     */
    private int retcode;

    /**
     * 信息描述
     */
    private String retmsg;

    /**
     * 返回参数
     */
    private T retdata;

    public Result(ResultStatus resultStatus, T retdata) {
        this.retcode = resultStatus.getRetcode();
        this.retmsg = resultStatus.getRetmsg();
        this.retdata = retdata;
    }

    public int getRetcode() {
        return retcode;
    }

    public String getRetmsg() {
        return retmsg;
    }

    public T getRetdata() {
        return retdata;
    }

    public static Result<Void> success() {
        return new Result<Void>(ResultStatus.SUCCESS, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>(ResultStatus.SUCCESS, data);
    }

    public static <T> Result<T> success(ResultStatus resultStatus, T data) {
        if (resultStatus == null) {
            return success(data);
        }
        return new Result<T>(resultStatus, data);
    }

    public static <T> Result<T> failure() {
        return new Result<T>(ResultStatus.INTERNAL_SERVER_ERROR, null);
    }

    public static <T> Result<T> failure(ResultStatus resultStatus, T data) {
        if (resultStatus == null) {
            return new Result<T>(ResultStatus.INTERNAL_SERVER_ERROR, null);
        }
        return new Result<T>(resultStatus, data);
    }

    public static <T> Result<T> failure(ResultStatus resultStatus) {
        return failure(resultStatus, null);
    }
}
