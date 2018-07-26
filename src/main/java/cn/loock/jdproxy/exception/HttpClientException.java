package cn.loock.jdproxy.exception;


public class HttpClientException extends IllegalRequestException {

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String message, Exception e) {
        super(message, e);
    }
}
