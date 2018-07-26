package cn.loock.jdproxy.service;

import cn.loock.jdproxy.bean.Config;
import cn.loock.jdproxy.utils.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ZhiyuanG
 * Date: 2018/7/25.
 * Time: 上午9:29
 */
@Component
public class OAuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthService.class);
    @Autowired
    private HttpClients httpClients;

    @Autowired
    private Config config;

    public Map<String, String> applyCode() {
        String url = "https://oauth.jd.com/oauth/authorize";
        Map<String, Object> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", config.getAppKey());
        params.put("redirect_uri", "http://jostest.lieguanjia.com/jos/oauth");
        params.put("state", "state");
        String s = httpClients.get(url, params);
        LOGGER.info(s);
        // waiting redirect
        return null;
    }

    public void getAccessToken(String code) {
        String url = "https://oauth.jd.com/oauth/token";
        Map<String, Object> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", config.getAppKey());
        params.put("client_secret", config.getAppSecret());
        params.put("code", code);
        params.put("redirect_uri", "http://jostest.lieguanjia.com/jos/oauth");
        params.put("scope", "read");
        params.put("state", "authorization_code");

        String s = httpClients.get(url, params);
        LOGGER.info(s);
        //json access_token

    }

    public void refreshAccessToken(String refreshToken) {
        String url = "https://oauth.jd.com/oauth/token";
        Map<String, Object> params = new HashMap<>();
        params.put("grant_type", "refresh_token");
        params.put("client_id", config.getAppKey());
        params.put("client_secret", config.getAppSecret());
        params.put("refresh_token", refreshToken);
        params.put("redirect_uri", "http://jostest.lieguanjia.com/jos/oauth");
        params.put("scope", "read");
        params.put("state", "refresh_token");

        String s = httpClients.get(url, params);
        LOGGER.info(s);
        //json access_token

    }

    public void auth() {
//        applyCode();
//        getAccessToken("Er7yXL");
//        refreshAccessToken("90e05db1-6f4d-4469-8525-30c89ddb5131");
    }
}
