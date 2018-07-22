package cn.loock.jdproxy.controller;

import cn.loock.jdproxy.exception.ErrorMessageUtil;
import cn.loock.jdproxy.exception.IllegalRequestException;
import cn.loock.jdproxy.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class BaseController {

    protected static final String KEY_RESULT = "result";
    protected static final String KEY_STATUS = "status";
    protected static final String KEY_ID = "id";

    protected void checkParamString(String param, String field) {
        if (param == null || param.length() == 0) {
            throw new IllegalRequestException(ErrorMessageUtil.PARAMETER_NOT_EXIST + field);
        }
    }

    protected void checkParamInt(Integer param, String field) {
        if (param == null) {
            throw new IllegalRequestException(ErrorMessageUtil.PARAMETER_NOT_EXIST + field);
        }
    }

    protected void checkParamLong(Long param, String field) {
        if (param == null) {
            throw new IllegalRequestException(ErrorMessageUtil.PARAMETER_NOT_EXIST + field);
        }
    }

    protected Map<String, Object> returnMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(key, value);
        return map;
    }

    protected String getJsonStringWithDefault(JsonNode node, String field, String defaultValue, boolean need) {
        return JsonUtil.asText(node, field, defaultValue, need);
    }

    protected String getJsonString(JsonNode node, String field, boolean need) {
        return this.getJsonStringWithDefault(node, field, null, need);
    }


    protected int getJsonIntWithDefault(JsonNode node, String field, Integer defaultValue, boolean need) {
        return JsonUtil.asInt(node, field, defaultValue, need);
    }

    protected int getJsonInt(JsonNode node, String field, boolean need) {
        return this.getJsonIntWithDefault(node, field, 0, need);
    }

    protected Integer getJsonIntegerWithDefault(JsonNode node, String field, Integer defaultValue, boolean need) {
        return JsonUtil.asInt(node, field, defaultValue, need);
    }

    protected Integer getJsonInteger(JsonNode node, String field, boolean need) {
        return this.getJsonIntegerWithDefault(node, field, null, need);
    }
}