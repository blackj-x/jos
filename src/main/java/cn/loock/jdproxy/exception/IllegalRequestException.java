package cn.loock.jdproxy.exception;

public class IllegalRequestException extends RuntimeException {

    public IllegalRequestException() {
        super(ErrorMessageUtil.PARAMETER_ERROR);
    }

    public IllegalRequestException(String s) {
        super(s);
    }

    public IllegalRequestException(Exception e) {
        super(e);
    }
}
