package cn.loock.jdproxy.utils;

import cn.loock.jdproxy.exception.ErrorMessageUtil;
import cn.loock.jdproxy.exception.IllegalRequestException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;


public class JsonUtil {

    private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);


    public static JsonNode getNode(JsonNode node, String field, boolean need) {
        JsonNode fieldNode = node.get(field);
        if (need && isNullNode(fieldNode)) {
            throw new IllegalRequestException(ErrorMessageUtil.PARAMETER_NOT_EXIST + field);
        }
        return fieldNode;
    }

    public static int asInt(JsonNode node, String field, int defaultValue, boolean need) {
        JsonNode fieldNode = getNode(node, field, need);
        if (need) {
            if (fieldNode.isTextual()) {
                try {
                    int value = Integer.parseInt(fieldNode.asText());
                    return value;
                } catch (NumberFormatException e) {
                    throw new IllegalRequestException(ErrorMessageUtil.PARAMETER_TYPE_ERROR + field);
                }
            } else if (!fieldNode.isInt()) {
                throw new IllegalRequestException(ErrorMessageUtil.PARAMETER_TYPE_ERROR + field);
            }
        }
        if (isNullNode(fieldNode)) {
            return defaultValue;
        } else {
            return fieldNode.asInt(defaultValue);
        }
    }

    public static double asDouble(JsonNode node, String field, double defaultValue, boolean need) {
        JsonNode fieldNode = getNode(node, field, need);
        if (need) {
            if (fieldNode.isTextual()) {
                try {
                    double value = Double.parseDouble(fieldNode.asText());
                    return value;
                } catch (NumberFormatException e) {
                    throw new IllegalRequestException(ErrorMessageUtil.PARAMETER_TYPE_ERROR + field);
                }
            } else if (fieldNode.isNumber()) {
                return fieldNode.asDouble();
            } else if (!fieldNode.isFloatingPointNumber()) {
                throw new IllegalRequestException(ErrorMessageUtil.PARAMETER_TYPE_ERROR + field);
            }
        }
        return fieldNode == null ? defaultValue : fieldNode.asDouble(defaultValue);
    }


    private static boolean isNullNode(JsonNode node) {
        if (node == null || node.isNull()) {
            return true;
        }
        return false;
    }

    public static long asLong(JsonNode node, String field, long defaultValue, boolean need) {
        JsonNode fieldNode = getNode(node, field, need);
        if (need) {
            if (fieldNode.isTextual()) {
                try {
                    long value = Long.parseLong(fieldNode.asText());
                    return value;
                } catch (NumberFormatException e) {
                    throw new IllegalRequestException(ErrorMessageUtil.PARAMETER_TYPE_ERROR + field);
                }
            } else if (!fieldNode.isLong() && !fieldNode.isInt()) {
                throw new IllegalRequestException(ErrorMessageUtil.PARAMETER_TYPE_ERROR + field);
            }
        }
        return fieldNode == null ? defaultValue : fieldNode.asLong(defaultValue);
    }

    public static String asText(JsonNode node, String field, String defaultValue, boolean need) {
        JsonNode fieldNode = getNode(node, field, need);
        String text = asText(fieldNode);
        if (need) {
            if (text == null || text.length() == 0) {
                throw new IllegalRequestException(ErrorMessageUtil.PARAMETER_TYPE_ERROR + field);
            }
            return text;
        }
        return text == null ? defaultValue : text;
    }

    public static String asText(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText().trim();
    }


    public static String asText(JsonNode node, String field, String defaultValue, boolean need, int maxLength) {
        String value = asText(node, field, defaultValue, need);
        if (value != null && value.length() > maxLength) {
            value = value.substring(0, maxLength);
        }
        return value;
    }

    public static JsonNode asArray(JsonNode node, String field, boolean need) {
        JsonNode fieldNode = getNode(node, field, need);
        if (need) {
            if (!fieldNode.isArray()) {
                throw new IllegalRequestException(ErrorMessageUtil.PARAMETER_TYPE_ERROR + field);
            }
        }
        return fieldNode;
    }


    public static JsonNode jsonFromString(String string) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        JsonParser jp = factory.createParser(string);
        return mapper.readTree(jp);
    }

    public static Map jsonStr2Map(String jsonStr) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonStr, Map.class);
    }

    public static String asText(JsonNode node, int maxLength) {
        String value = asText(node);
        if (value != null && value.length() > maxLength) {
            value = value.substring(0, maxLength);
        }
        return value;
    }

    public static String toJson(Object object) {

        return toJson(object, false, false);
    }

    public static String toJson(Object object, boolean useAnnotation, boolean ignoreNull) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (useAnnotation) {
            objectMapper.configure(MapperFeature.USE_ANNOTATIONS, true);
        }
        if (ignoreNull) {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        String json = null;
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.error("Obj toJson error:", e);
        }
        return json;
    }

    public static JsonNode toNode(Object object) {
        String jsonStr;
        if (object instanceof String) {
            jsonStr = (String) object;
        } else {
            jsonStr = toJson(object);
        }
        try {
            return jsonFromString(jsonStr);
        } catch (IOException e) {
            logger.error("Obj toNode error:", e);
            return null;
        }
    }

    public static JsonNode toNode(Object object, boolean useAnnotation, boolean ignoreNull) {
        String jsonStr;
        if (object instanceof String) {
            jsonStr = (String) object;
        } else {
            jsonStr = toJson(object, useAnnotation, ignoreNull);
        }
        try {
            return jsonFromString(jsonStr);
        } catch (IOException e) {
            logger.error("Obj toNode error:", e);
            return null;
        }
    }

    public static <T> T fromJson(String content, Class<T> valueType) {
        ObjectMapper mapper = new ObjectMapper();
        mapper = mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        T result = null;
        try {
            result = mapper.readValue(content, valueType);
        } catch (IOException e) {
            logger.error("from json error", e);
        }
        return result;
    }

}
