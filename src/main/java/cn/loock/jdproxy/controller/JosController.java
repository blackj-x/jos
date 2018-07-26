package cn.loock.jdproxy.controller;

import cn.loock.jdproxy.bean.Config;
import cn.loock.jdproxy.bean.ResponseResult;
import cn.loock.jdproxy.exception.ErrorMessageUtil;
import cn.loock.jdproxy.exception.IllegalRequestException;
import cn.loock.jdproxy.service.ClassService;
import cn.loock.jdproxy.service.OAuthService;
import cn.loock.jdproxy.utils.JsonUtil;
import cn.loock.jdproxy.utils.PackageUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.request.JdRequest;
import com.jd.open.api.sdk.response.AbstractResponse;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ZhiyuanG
 * Date: 2018/7/20.
 * Time: 下午9:38
 */
@RestController
@RequestMapping("/jos")
public class JosController extends BaseController {
    private static Logger LOGGER = LoggerFactory.getLogger(JosController.class);
    @Autowired
    private ClassService classService;

    @Autowired
    private Config config;

    @Autowired
    private OAuthService oAuthService;

    /**
     * {
     * "requestName":"LasSpareZerostockHandleSearchRequest",
     * "category":"HouseEI",
     * "params":{
     * "begin":"jingdong",
     * "end":"end",
     * "index":123,
     * "vc":"vc",
     * "token":"token"
     * }
     * }
     * category: com.jd.open.api.sdk.request 包下的子包
     *
     * @param jsonNode
     * @return
     */
    @RequestMapping(value = "/invoke", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResult invoke(@RequestBody JsonNode jsonNode) {
        String className = getJsonString(jsonNode, "requestName", true);
        String category = getJsonString(jsonNode, "category", true);
        JsonNode params = JsonUtil.getNode(jsonNode, "params", true);
        JdRequest instance = getJdRequest(category, className, params);
        oAuthService.auth();
        JdClient client = new DefaultJdClient(config.getServerUrl(), config.getAccessToken(), config.getAppKey(), config.getAppSecret());
        AbstractResponse execute;
        try {
            execute = client.execute(instance);
        } catch (JdException e) {
            LOGGER.error("execute request error", e);
            throw new IllegalRequestException(ErrorMessageUtil.JD_CLIENT_EXECUTE_ERROR);
        }
        return new ResponseResult(execute);
    }

    private JdRequest getJdRequest(String category, String className, JsonNode params) {
        String forName = "com.jd.open.api.sdk.request." + category + "." + className;
        JdRequest instance = (JdRequest) classService.getInstance(forName);
        Iterator<Map.Entry<String, JsonNode>> it = params.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> next = it.next();
            String key = next.getKey();
            Object value = next.getValue().textValue();
            try {
                PropertyUtils.setProperty(instance, key, value);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                LOGGER.error("创建JDRequest失败", e);
                throw new IllegalRequestException(ErrorMessageUtil.JD_CLIENT_REQUEST_CREATE_ERROR);

            }
        }
        return instance;
    }

    @RequestMapping(value = "/debug", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResult debug(@RequestBody JsonNode jsonNode) {
        String className = getJsonString(jsonNode, "requestName", true);
        String category = getJsonString(jsonNode, "category", true);
        JsonNode params = JsonUtil.getNode(jsonNode, "params", true);
        JdRequest instance = getJdRequest(category, className, params);
        String packageName = "com.jd.open.api.sdk.request." + category;
        List<String> classNames = PackageUtil.getClassName(packageName);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("request", instance);
        resultMap.put("classNames", classNames);
        return new ResponseResult(instance);
    }

    @RequestMapping("/oauth")
    public ResponseResult oauth(@RequestParam(value = "code", required = false) String code, @RequestParam(value = "code", required = false) String state) {
        oAuthService.getAccessToken(code);
        return new ResponseResult();
    }
}
