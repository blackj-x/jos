package cn.loock.jdproxy.exception;

import org.springframework.http.HttpStatus;

public class ErrorMessageUtil {

    //--400
    public static final String PARAMETER_TYPE_ERROR = "400|必填字段类型错误";
    public static final String PARAMETER_NOT_EXIST = "400|必填字段不存在";
    public static final String PARAMETER_OPTIONAL_ERROR = "400|可选字段值错误";
    public static final String PARAMETER_ERROR = "400|请求参数错误";
    public static final String PARAMETER_OUT_OF_RANGE = "400|参数值超过范围";
    public static final String PARAMETER_ILLEGAL_EXT = "400|不合法的文件扩展名";
    public static final String PERMISSION_DENIED = "403|没有相关权限";
    public static final String TEXT_ILLEGAL = "403|文本内容非法";
    public static final String REQUEST_IP_LIMIT = "403|请求IP受限";
    public static final String RESPONSE_NULL_DATA = "404|请求内容不存在";
    public static final String RESPONSE_NULL_FILE = "404|文件不存在";


    //--500
    public static final String SYSTEM_ERROR = "500|内部错误";

    //1000
    public static final String JD_CLIENT_EXECUTE_ERROR = "1000|执行错误";
    public static final String JD_CLIENT_REQUEST_CREATE_ERROR = "1001|创建request失败";

    public static int getErrorCode(String errorMessage) {
        int code;
        if (errorMessage == null) {
            code = 500;
        } else {
            code = Integer.parseInt(errorMessage.split("\\|")[0]);
        }
        return code;
    }

    public static String getErrorMessage(String errorMessage) {
        if (errorMessage == null) {
            errorMessage = SYSTEM_ERROR;
        }
        return errorMessage.split("\\|")[1];
    }

    public static HttpStatus getHttpStatus(String errMessage) {
        int code = getErrorCode(errMessage);
        if (code >= 1000) {
            code = 403;
        }
        return HttpStatus.valueOf(code);
    }

}
